package org.example.code.handlers;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.code.common.C2MTaskResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Hashtable;
import java.util.regex.Pattern;

public class C2MMessageDataHandler {
    private final Hashtable<String, JSONObject> kvsd;
    private final C2MMongoDBHandler databaseHandler;
    private int documentCounter = -1;

    public C2MMessageDataHandler(boolean isLab) {
        System.out.println("Created an instance of C2MMessageDataHandler. Ready to use.");
        this.databaseHandler = new C2MMongoDBHandler(isLab);
        this.kvsd = new Hashtable<>();
    }

    public void handleMessage(String topic, MqttMessage msg) {
        JSONObject jsonObject = new JSONObject(msg.toString());
        C2MTaskResult result;

        if( -1 == documentCounter ) {
            documentCounter = databaseHandler.getLastInsertedDocumentId() + 1;
        }

        if(topic.contains("temp")) {
            result = checkFieldValidityTemp(jsonObject);
        } else {
            result = checkFieldValidityLab(jsonObject);
        }

        if(C2MTaskResult.OK != result) {
            jsonObject.put("ERROR", "DATA_TYPE_OR_FORMAT");
            System.out.println("Message is not valid. Task result with value = "+result.name());
        }

        if(kvsd.containsKey(jsonObject.getString("Hora"))) {
            jsonObject.put("ERROR", "DUPLICATE");
            System.out.println("Duplicate message, adding to the Database with error field.");
        }

        jsonObject.put("MongoDBId", documentCounter);

        kvsd.put(jsonObject.getString("Hora"), jsonObject);
        databaseHandler.insertData(jsonObject.toString());
        documentCounter++;
    }

    private C2MTaskResult checkFieldValidityLab(JSONObject obj) {
        String timestampRegex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.?\\d*";
        String roomRegex = "[1-9]";

        if(!obj.has("Hora") || !obj.has("SalaOrigem") || !obj.has("SalaDestino")) {
            return C2MTaskResult.ERROR_MISSING_VALUES_IN_LAB_MESSAGE;
        }

        try {
            String hourValue = obj.getString("Hora");
            if(!Pattern.matches(timestampRegex, hourValue)) {
                return C2MTaskResult.ERROR_TIMESTAMP_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return C2MTaskResult.ERROR_TIMESTAMP_WRONG_VALUE_TYPE;
        }

        try {
            int originRoomValue = obj.getInt("SalaOrigem");
            if(!Pattern.matches(roomRegex, Integer.toString(originRoomValue))) {
                return C2MTaskResult.ERROR_ORIGIN_ROOM_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return C2MTaskResult.ERROR_ORIGIN_ROOM_WRONG_VALUE_TYPE;
        }

        try {
            int destinationRoomValue = obj.getInt("SalaDestino");
            if(!Pattern.matches(roomRegex, Integer.toString(destinationRoomValue))) {
                return C2MTaskResult.ERROR_DESTINATION_ROOM_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return C2MTaskResult.ERROR_DESTINATION_ROOM_WRONG_VALUE_TYPE;
        }
        return C2MTaskResult.OK;
    }

    private C2MTaskResult checkFieldValidityTemp(JSONObject obj) {
        String temperatureRegex = "\\d+\\.?\\d*";
        String timestampRegex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.?\\d*";
        String sensorRegex = "[1-9]";

        if(!obj.has("Hora") || !obj.has("Leitura") || !obj.has("Sensor")) {
            return C2MTaskResult.ERROR_MISSING_VALUES_IN_TEMP_MESSAGE;
        }

        try {
            String hourValue = obj.getString("Hora");
            if(!Pattern.matches(timestampRegex, hourValue)) {
                return C2MTaskResult.ERROR_TIMESTAMP_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return C2MTaskResult.ERROR_TIMESTAMP_WRONG_VALUE_TYPE;
        }

        try {
            double temperatureValue = obj.getDouble("Leitura");
            if(!Pattern.matches(temperatureRegex, Double.toString(temperatureValue))) {
                return C2MTaskResult.ERROR_TEMP_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return C2MTaskResult.ERROR_TEMP_WRONG_VALUE_TYPE;
        }

        try {
            int sensorValue = obj.getInt("Sensor");
            if(!Pattern.matches(sensorRegex, Integer.toString(sensorValue))) {
                return C2MTaskResult.ERROR_SENSOR_WRONG_FORMAT;
            }
        } catch (JSONException ex) {
            return C2MTaskResult.ERROR_SENSOR_WRONG_VALUE_TYPE;
        }
        return C2MTaskResult.OK;
    }
}
