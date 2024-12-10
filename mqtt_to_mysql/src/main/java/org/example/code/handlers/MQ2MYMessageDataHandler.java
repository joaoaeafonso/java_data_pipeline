package org.example.code.handlers;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.code.common.MQ2MYMessageType;
import org.example.code.common.MQ2MYTaskResult;
import org.example.code.common.MQ2MYPair;
import org.example.code.managers.MQ2MYExperienceManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Hashtable;
import java.util.regex.Pattern;


public class MQ2MYMessageDataHandler {

    private final Hashtable<Integer, JSONObject> kvsd;
    private Hashtable<MQ2MYPair<Integer, Integer>, Integer> validCorridor;

    private double temperatureDelta = Double.MIN_VALUE;
    private double temperatureMax = Double.MIN_VALUE;
    private double temperatureMin = Double.MIN_VALUE;
    private double temperatureLastRead = Double.MIN_VALUE;

    private boolean isExperienceRunning = false;
    private boolean previousExperienceRunning = false;

    private boolean isRecurrentErrorTemp = false;
    private boolean isRecurrentErrorMove = false;
    private boolean isRecurrentOutlier = false;


    public MQ2MYMessageDataHandler() {
        System.out.println("Created an instance of MQ2MYMessageDataHandler. Ready to use.");
        this.kvsd = new Hashtable<>();
        this.validCorridor = new Hashtable<>();
    }

    private void clearAllKvs() {
        System.out.println("Experience ended, clearing all internal kvs.");
        this.validCorridor.clear();
        this.temperatureDelta = Double.MIN_VALUE;
        this.temperatureMax = Double.MIN_VALUE;
        this.temperatureMin = Double.MIN_VALUE;
        this.temperatureLastRead = Double.MIN_VALUE;
    }

    public void setTemperatureValuesForExperience() {
        this.temperatureDelta = MQ2MYExperienceManager.getInstance().getTemperatureDelta();
        this.temperatureMax = MQ2MYExperienceManager.getInstance().getMaxTemperature();
        this.temperatureMin = MQ2MYExperienceManager.getInstance().getMinTemperature();
    }

    public void handleMessage(String topic, MqttMessage msg) {
        if(!topic.contains("temp") && !topic.contains("lab")) {
            System.out.println("Message not relevant. Ignoring.");
            return;
        }

        JSONObject jsonObject = new JSONObject(msg.toString());
        MQ2MYMessageType msgType;

        if(kvsd.containsKey(jsonObject.getInt("MongoDBId"))) {
            System.out.println("Duplicate message, not adding to the Database.");
            return;
        }

        kvsd.put(jsonObject.getInt("MongoDBId"), jsonObject);

        if (MQ2MYTaskResult.OK != checkErrorMessage(jsonObject, topic)) {
            System.out.println("Received error message. Sent to ExperienceManager");
            return;
        }

        if(topic.contains("temp")) {
            MQ2MYTaskResult result = checkMessageValidityTemp(jsonObject);
            if( MQ2MYTaskResult.OK != result) {

                if( MQ2MYTaskResult.ERROR_TEMP_BAD_VALUE == result ) {
                    checkIsRecurrentTempError();
                    MQ2MYExperienceManager.getInstance().handleMessage(jsonObject, MQ2MYMessageType.TEMP_BAD_READING);
                    return;
                }

                if( MQ2MYTaskResult.ERROR_TEMP_EXCEEDED_MIN_VALUE == result) {
                    checkIsRecurrentTempError();
                    MQ2MYExperienceManager.getInstance().handleMessage(jsonObject, MQ2MYMessageType.TEMP_MIN_VALUE);
                    return;
                }

                if( MQ2MYTaskResult.ERROR_TEMP_EXCEEDED_MAX_VALUE == result) {
                    checkIsRecurrentTempError();
                    MQ2MYExperienceManager.getInstance().handleMessage(jsonObject, MQ2MYMessageType.TEMP_MAX_VALUE);
                    return;
                }

                System.out.println("Identified an Outlier. Sending message to ExperienceManager.");
                MQ2MYExperienceManager.getInstance().handleMessage(jsonObject, MQ2MYMessageType.TEMP_OUTLIER);
                checkIsRecurrentOutlier();
                return;
            }

            msgType = MQ2MYMessageType.TEMPERATURE;
            MQ2MYExperienceManager.getInstance().clearRecurrentInvalidDataTemperatureCounter();
            MQ2MYExperienceManager.getInstance().clearRecurrentOutlierRecurrentCounter();
            this.isRecurrentOutlier = false;
            this.isRecurrentErrorTemp = false;

        } else {
            MQ2MYTaskResult result = checkFieldValidityLab(jsonObject);
            if( MQ2MYTaskResult.OK != result ) {
                checkIsRecurrentMoveError();

                if( MQ2MYTaskResult.ERROR_NO_VALID_LAB_CONFIG == result ) {
                    System.out.println("Unable to Load Labyrinth config. Sending Critical alert.");
                    MQ2MYExperienceManager.getInstance().handleMessage(jsonObject, MQ2MYMessageType.LAB_MOVE_INVALID_CONFIG);
                    return;
                }

                System.out.println("Identified a Movement error. Sending message to ExperienceManager.");
                MQ2MYExperienceManager.getInstance().handleMessage(jsonObject, MQ2MYMessageType.LAB_MOVE_INVALID_CORRIDOR);
                return;
            }

            msgType = MQ2MYMessageType.MOVEMENT;
            this.isRecurrentErrorMove = false;
            MQ2MYExperienceManager.getInstance().clearRecurrentInvalidDataMovementCounter();
        }

        MQ2MYExperienceManager.getInstance().handleMessage(jsonObject, msgType);
    }

    private void checkIsRecurrentTempError() {
        MQ2MYExperienceManager instance = MQ2MYExperienceManager.getInstance();

        if(isRecurrentErrorTemp) {
            instance.increaseRecurrentInvalidDataTemperatureCounter();
            instance.increaseTemperatureErrorCounter();
            return;
        }

        isRecurrentErrorTemp = true;
        instance.increaseTemperatureErrorCounter();
    }

    private void checkIsRecurrentMoveError() {
        MQ2MYExperienceManager instance = MQ2MYExperienceManager.getInstance();

        if(isRecurrentErrorMove) {
            instance.increaseRecurrentInvalidDataMovementCounter();
            instance.increaseMovementErrorCounter();
            return;
        }

        isRecurrentErrorMove = true;
        instance.increaseMovementErrorCounter();
    }

    private void checkIsRecurrentOutlier() {
        MQ2MYExperienceManager instance = MQ2MYExperienceManager.getInstance();

        if(isRecurrentOutlier) {
            instance.increaseRecurrentOutlierRecurrentCounter();
            instance.increaseTemperatureErrorCounter();
            return;
        }

        isRecurrentOutlier = true;
        instance.increaseTemperatureErrorCounter();
    }

    private MQ2MYTaskResult checkErrorMessage(JSONObject jsonObject, String topic) {
        if( !jsonObject.has("ERROR") ) {
            return MQ2MYTaskResult.OK;
        }

        if(topic.contains("temp")) {
            checkIsRecurrentTempError();
        } else {
            checkIsRecurrentMoveError();
        }

        MQ2MYExperienceManager
                .getInstance()
                .handleMessage(jsonObject, (topic.contains("temp")) ? MQ2MYMessageType.INVALID_DATA_TEMPERATURE : MQ2MYMessageType.INVALID_DATA_MOVEMENT);
        return MQ2MYTaskResult.ERROR_MESSAGE_RECEIVED;
    }

    private MQ2MYTaskResult checkMessageValidityTemp(JSONObject obj) {
        String temperatureRegex = "\\d+\\.?\\d*";
        double temperatureValue;

        if(!isExperienceRunning()) {
            System.out.println("Experience is not Running. Temperature message is valid.");
            return MQ2MYTaskResult.OK;
        } else {

            try {
                temperatureValue = obj.getDouble("Leitura");
                if(!Pattern.matches(temperatureRegex, Double.toString(temperatureValue))) {
                    return MQ2MYTaskResult.ERROR_TEMP_WRONG_FORMAT;
                }
            } catch (JSONException ex) {
                return MQ2MYTaskResult.ERROR_TEMP_WRONG_VALUE_TYPE;
            }

            if( Double.MIN_VALUE == temperatureLastRead ) {
                temperatureLastRead = temperatureValue;
            }

            double variation = Math.abs(temperatureValue - temperatureLastRead);
            System.out.println("VARIATION ==== "+variation);

            if( temperatureValue <= temperatureMin ) {
                if( variation >= temperatureDelta ) {

                    double percentageVariation = Math.abs( (temperatureValue - temperatureLastRead) / temperatureLastRead ) * 100;
                    if( percentageVariation >= 30 ) {
                        System.out.println("Wrong temperature reading, Sending message to ExperienceManager.");
                        return MQ2MYTaskResult.ERROR_TEMP_BAD_VALUE;
                    }

                    System.out.println("Outlier Identified. Proceeding.");
                    return MQ2MYTaskResult.ERROR_OUTLIER;
                } else {
                    System.out.println("Temperature exceeded Min value. Sending Critical alert.");
                    return MQ2MYTaskResult.ERROR_TEMP_EXCEEDED_MIN_VALUE;
                }

            } else if ( temperatureValue >= temperatureMax ) {
                if( variation >= temperatureDelta ) {
                    double percentageVariation = Math.abs( (temperatureValue - temperatureLastRead) / temperatureLastRead ) * 100;
                    if( percentageVariation >= 30 ) {
                        System.out.println("Wrong temperature reading, Sending message to ExperienceManager.");
                        return MQ2MYTaskResult.ERROR_TEMP_BAD_VALUE;
                    }

                    System.out.println("Outlier Identified. Proceeding.");
                    return MQ2MYTaskResult.ERROR_OUTLIER;

                } else {
                    System.out.println("Temperature exceeded Max value. Sending Critical alert.");
                    return MQ2MYTaskResult.ERROR_TEMP_EXCEEDED_MAX_VALUE;
                }

            } else {
                if( Math.abs( temperatureMax - temperatureValue ) <= 3 ) {
                    System.out.println("Temperature is almost at the maximum value. Generating Alert.");
                    MQ2MYExperienceManager.getInstance().handleMessage(obj, MQ2MYMessageType.TEMP_APPROX_MAX_VALUE);
                }

                if( Math.abs( temperatureMin - temperatureValue ) <= 3 ) {
                    System.out.println("Temperature is almost at the minimum value. Generating Alert.");
                    MQ2MYExperienceManager.getInstance().handleMessage(obj, MQ2MYMessageType.TEMP_APPROX_MIN_VALUE);
                }

                if( variation >= temperatureDelta ) {
                    System.out.println("Outlier identified. Proceeding.");
                    temperatureLastRead = temperatureValue;
                    return MQ2MYTaskResult.ERROR_OUTLIER;
                }
            }

            temperatureLastRead = temperatureValue;
        }
        return MQ2MYTaskResult.OK;
    }

    private boolean isExperienceRunning() {
        if( MQ2MYExperienceManager.getInstance().isExperienceRunning() ) {
            System.out.println("Experience is running.");
            this.previousExperienceRunning = this.isExperienceRunning;
            this.isExperienceRunning = true;

            if(     Double.MIN_VALUE == this.temperatureMin ||
                    Double.MIN_VALUE == this.temperatureMax ||
                    Double.MIN_VALUE == this.temperatureDelta ) {
                setTemperatureValuesForExperience();
            }
            return true;
        } else {

            this.isExperienceRunning = false;
            if( this.previousExperienceRunning ) {
                System.out.println("Experience was running. Clearing all internal kvs.");
                clearAllKvs();
                this.previousExperienceRunning = false;
            } else {
                System.out.println("Experience is not running.");
            }
            return false;
        }
    }

    private MQ2MYTaskResult checkFieldValidityLab(JSONObject obj) {
        String timestampRegex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.?\\d*";
        String roomRegex = "[1-9]";

        if(!obj.has("Hora") || !obj.has("SalaOrigem") || !obj.has("SalaDestino")) {
            return MQ2MYTaskResult.ERROR_MISSING_VALUES_IN_LAB_MESSAGE;
        }

        try {
            String hourValue = obj.getString("Hora");
            if(!Pattern.matches(timestampRegex, hourValue)) {
                return MQ2MYTaskResult.ERROR_TIMESTAMP_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return MQ2MYTaskResult.ERROR_TIMESTAMP_WRONG_VALUE_TYPE;
        }

        int originRoomValue;
        try {
            originRoomValue = obj.getInt("SalaOrigem");
            if(!Pattern.matches(roomRegex, Integer.toString(originRoomValue))) {
                return MQ2MYTaskResult.ERROR_ORIGIN_ROOM_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return MQ2MYTaskResult.ERROR_ORIGIN_ROOM_WRONG_VALUE_TYPE;
        }

        int destinationRoomValue;
        try {
            destinationRoomValue = obj.getInt("SalaDestino");
            if(!Pattern.matches(roomRegex, Integer.toString(destinationRoomValue))) {
                return MQ2MYTaskResult.ERROR_DESTINATION_ROOM_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return MQ2MYTaskResult.ERROR_DESTINATION_ROOM_WRONG_VALUE_TYPE;
        }

        if( !isExperienceRunning() ) {
            System.out.println("Experiment not running, not checking for movement validity.");
        } else {

            MQ2MYTaskResult result = isMovementValid(new MQ2MYPair<>(originRoomValue, destinationRoomValue));
            if( MQ2MYTaskResult.OK != result) {
                if( MQ2MYTaskResult.ERROR_NO_VALID_LAB_CONFIG == result ) {
                    return MQ2MYTaskResult.ERROR_NO_VALID_LAB_CONFIG;
                }
                return MQ2MYTaskResult.ERROR_MOVEMENT_NOT_VALID;
            }
        }

        return MQ2MYTaskResult.OK;
    }

    private MQ2MYTaskResult isMovementValid(MQ2MYPair<Integer, Integer> MQ2MYPair) {
        if( this.validCorridor.isEmpty() ) {
            System.out.println("Loading valid corridor list.");
            this.validCorridor = MQ2MYExperienceManager.getInstance().getValidCorridors();

            if( this.validCorridor.isEmpty() ) {
                System.out.println("Unable to get Labyrinth configuration. Experience is not valid.");
                return MQ2MYTaskResult.ERROR_NO_VALID_LAB_CONFIG;
            }
        }

        if( this.validCorridor.containsKey(MQ2MYPair) ) {
            return MQ2MYTaskResult.OK;
        }

        return MQ2MYTaskResult.ERROR_MOVEMENT_NOT_VALID;
    }
}
