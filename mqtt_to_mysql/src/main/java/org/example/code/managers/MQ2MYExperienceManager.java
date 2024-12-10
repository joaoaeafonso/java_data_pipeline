package org.example.code.managers;

import org.example.code.common.*;
import org.example.code.handlers.MQ2MYMySQLCloudDBHandler;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;
import java.util.concurrent.*;


public class MQ2MYExperienceManager {

    private static MQ2MYExperienceManager singleInstance;
    private final MQ2MYMySQLDBHandler mq2MYMySQLDBHandler;
    private boolean isExperienceRunning = false;

    private final BlockingQueue<JSONObject> tempMessageQueue;
    private final BlockingQueue<JSONObject> moveMessageQueue;
    private final BlockingQueue<JSONObject> alertMessageQueue;
    private final ScheduledExecutorService schedulerTemperature;
    private final ScheduledExecutorService schedulerMovements;
    private final ScheduledExecutorService schedulerAlerts;
    private final ScheduledExecutorService ratMovementTimer;

    // Experience Data
    private int experienceId = Integer.MIN_VALUE;
    private double temperatureDelta = Double.MIN_VALUE;
    private double temperatureMax = Double.MIN_VALUE;
    private double temperatureMin = Double.MIN_VALUE;
    private int maxNumberOfRatsInRoom = Integer.MIN_VALUE;
    private int maxNumberOfErrors = Integer.MIN_VALUE;
    private int maxNumberOfErrorsTemperature = Integer.MIN_VALUE;
    private int maxNumberOfErrorsMovement = Integer.MIN_VALUE;
    private int totalNumberOfRats = Integer.MIN_VALUE;
    private long numberOfSecondsWithoutRatMovement = Integer.MIN_VALUE;
    private final Hashtable<Integer, Integer> numRatsPerRoom;

    // Recurrent Counters
    private int invalidDataTemperatureRecurrentCounter = 0;
    private int invalidDataMovementRecurrentCounter = 0;
    private int outlierRecurrentCounter = 0;

    // Error Counters
    private int temperatureErrorCounter = 0;
    private int movementErrorCounter = 0;
    private int totalErrorCounter = 0;

    // Timers
    private long elapsedTimeSinceLastRatMovement = 0;

    // Batches
    private final ArrayList<JSONObject> temperatureBatch;
    private final ArrayList<JSONObject> movementBatch;

    private MQ2MYAlertPriority lastMovementPrioritySent = MQ2MYAlertPriority.UNKNOWN;

    private MQ2MYExperienceManager() {
        System.out.println("Created an instance of MQ2MYExperienceManager. Ready to use.");
        this.tempMessageQueue = new LinkedBlockingQueue<>();
        this.moveMessageQueue = new LinkedBlockingQueue<>();
        this.alertMessageQueue = new LinkedBlockingQueue<>();
        this.schedulerTemperature = Executors.newSingleThreadScheduledExecutor();
        this.schedulerMovements = Executors.newSingleThreadScheduledExecutor();
        this.schedulerAlerts = Executors.newSingleThreadScheduledExecutor();
        this.ratMovementTimer = Executors.newSingleThreadScheduledExecutor();
        this.numRatsPerRoom = new Hashtable<>();
        this.mq2MYMySQLDBHandler = MQ2MYMySQLDBHandler.getInstance();
        this.mq2MYMySQLDBHandler.connect();
        this.temperatureBatch = new ArrayList<>();
        this.movementBatch = new ArrayList<>();
    }

    public static MQ2MYExperienceManager getInstance() {
        if( null == singleInstance ){
            singleInstance = new MQ2MYExperienceManager();
        }
        return singleInstance;
    }

    public Hashtable<Integer, Integer> getFinalRatMovement() {
        return this.numRatsPerRoom;
    }

    public void increaseTemperatureErrorCounter() {
        this.temperatureErrorCounter++;
        this.totalErrorCounter++;
    }

    public void increaseMovementErrorCounter() {
        this.movementErrorCounter++;
        this.totalErrorCounter++;
    }

    public void increaseRecurrentInvalidDataMovementCounter() {
        this.invalidDataMovementRecurrentCounter++;
    }

    public void clearRecurrentInvalidDataMovementCounter() {
        this.invalidDataMovementRecurrentCounter = 0;
    }

    public void increaseRecurrentInvalidDataTemperatureCounter() {
        this.invalidDataTemperatureRecurrentCounter++;
    }

    public void clearRecurrentInvalidDataTemperatureCounter() {
        this.invalidDataTemperatureRecurrentCounter = 0;
    }

    public void increaseRecurrentOutlierRecurrentCounter() {
        this.outlierRecurrentCounter++;
    }

    public void clearRecurrentOutlierRecurrentCounter() {
        this.outlierRecurrentCounter = 0;
    }

    public MQ2MYTaskResult startExperienceManager() {
        System.out.println("Starting ExperienceManager.");
        startSchedulers();
        return MQ2MYTaskResult.OK;
    }

    public boolean isExperienceRunning() {
        return isExperienceRunning;
    }

    public void toggleExperienceRunning(boolean value) {
        this.isExperienceRunning = value;
    }

    public double getTemperatureDelta() {
        return this.temperatureDelta;
    }

    public double getMaxTemperature() {
        return this.temperatureMax;
    }

    public double getMinTemperature() {
        return this.temperatureMin;
    }

    public void setExperienceId(int value) {
        this.experienceId = value;
    }

    public void setRatNumbersForExperience(int total, int ratsPerRoom, int numOfSecsNoMove) {
        this.maxNumberOfRatsInRoom = ratsPerRoom;
        this.totalNumberOfRats = total;
        this.numberOfSecondsWithoutRatMovement = numOfSecsNoMove;
    }

    public void setTemperatureValuesForExperience(double delta, double max, double min) {
        this.temperatureDelta = delta;
        this.temperatureMax = max;
        this.temperatureMin = min;
    }

    public void setAllMaxErrorValues(int max, int maxTemp, int maxMove) {
        this.maxNumberOfErrors = max;
        this.maxNumberOfErrorsMovement = maxMove;
        this.maxNumberOfErrorsTemperature = maxTemp;
    }

    public void clearAllExperienceData() {
        experienceId = -1;
        temperatureDelta = Double.MIN_VALUE;
        temperatureMax = Double.MIN_VALUE;
        temperatureMin = Double.MIN_VALUE;
        maxNumberOfRatsInRoom = Integer.MIN_VALUE;
        maxNumberOfErrors = Integer.MIN_VALUE;
        maxNumberOfErrorsTemperature = Integer.MIN_VALUE;
        maxNumberOfErrorsMovement = Integer.MIN_VALUE;
        numberOfSecondsWithoutRatMovement = Integer.MIN_VALUE;
        totalNumberOfRats = Integer.MIN_VALUE;
        invalidDataTemperatureRecurrentCounter = 0;
        invalidDataMovementRecurrentCounter = 0;
        outlierRecurrentCounter = 0;
        temperatureErrorCounter = 0;
        movementErrorCounter = 0;
        totalErrorCounter = 0;
        elapsedTimeSinceLastRatMovement = 0;
        lastMovementPrioritySent = MQ2MYAlertPriority.UNKNOWN;
        numRatsPerRoom.clear();
    }

    public Hashtable<MQ2MYPair<Integer, Integer>, Integer> getValidCorridors() {
        System.out.println("Getting labyrinth configuration");

        Hashtable<MQ2MYPair<Integer, Integer>, Integer> result = MQ2MYMySQLCloudDBHandler.getInstance().loadLabyrinthConfig();
        if( result.isEmpty() ) {
            System.out.println("Unable to get Labyrinth configuration. Experience is not valid.");
        } else {
            MQ2MYMySQLDBHandler.getInstance().handleMazeConfig(result, this.experienceId);

            Set<MQ2MYPair<Integer, Integer>> setOfKeys = result.keySet();

            for (MQ2MYPair<Integer, Integer> pair : setOfKeys) {
                if (!numRatsPerRoom.containsKey(pair.first())) {
                    numRatsPerRoom.put(pair.first(), 0);
                }

                if (!numRatsPerRoom.containsKey(pair.second())) {
                    numRatsPerRoom.put(pair.second(), 0);
                }
            }

            if( Integer.MIN_VALUE != totalNumberOfRats ) {
                numRatsPerRoom.put(1, totalNumberOfRats);
            }
        }
        return result;
    }

    public void handleMessage(JSONObject jsonObject, MQ2MYMessageType type) {
        jsonObject.put("ExperienceID", experienceId);

        switch (type) {
            case TEMP_OUTLIER:
                jsonObject.put("ReadingType", "Temperatura");
                handleTemperatureOutlierMessage(jsonObject);
                break;
            case TEMP_BAD_READING:
                jsonObject.put("ReadingType", "Temperatura");
                handleTemperatureBadReadingMessage(jsonObject);
                break;
            case TEMP_APPROX_MAX_VALUE:
                jsonObject.put("ReadingType", "Temperatura");
                handleTemperatureMaxValueApproximationMessage(jsonObject);
                break;
            case TEMP_APPROX_MIN_VALUE:
                jsonObject.put("ReadingType", "Temperatura");
                handleTemperatureMinValueApproximationMessage(jsonObject);
                break;
            case LAB_MOVE_INVALID_CORRIDOR:
                jsonObject.put("ReadingType", "Movimento");
                handleMovementInvalidCorridorMessage(jsonObject);
                break;
            case LAB_MOVE_INVALID_CONFIG:
                jsonObject.put("ReadingType", "Movimento");
                handleMovementInvalidConfigMessage(jsonObject);
                break;
            case INVALID_DATA_TEMPERATURE:
                jsonObject.put("ReadingType", "Temperatura");
                handleInvalidDataTemperatureMessage(jsonObject);
                break;
            case INVALID_DATA_MOVEMENT:
                jsonObject.put("ReadingType", "Movimento");
                handleInvalidDataMovementMessage(jsonObject);
                break;
            case TEMPERATURE:
                jsonObject.put("ReadingType", "Temperatura");
                handleTemperatureMessage(jsonObject);
                break;
            case MOVEMENT:
                jsonObject.put("ReadingType", "Movimento");
                handleMovementMessage(jsonObject);
                break;
            case TEMP_MIN_VALUE:
                jsonObject.put("ReadingType", "Temperatura");
                handleTemperatureMinValueReachedMessage(jsonObject);
                break;
            case TEMP_MAX_VALUE:
                jsonObject.put("ReadingType", "Temperatura");
                handleTemperatureMaxValueReachedMessage(jsonObject);
        }

        checkErrorsAllowed();
    }

    private void checkErrorsAllowed() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ExperienceID", experienceId);

        double totalErrorPercentage = Math.abs( (totalErrorCounter) / this.maxNumberOfErrors ) * 100;
        double tempErrorPercentage = Math.abs( (temperatureErrorCounter) / this.maxNumberOfErrorsTemperature ) * 100;
        double moveErrorPercentage = Math.abs( (movementErrorCounter) / this.maxNumberOfErrorsMovement ) * 100;

        checkTotalErrorsAllowed(totalErrorPercentage, jsonObject);
        checkTemperatureErrorsAllowed(tempErrorPercentage, jsonObject);
        checkMovementErrorsAllowed(moveErrorPercentage, jsonObject);
    }

    private void checkMovementErrorsAllowed(double totalErrorPercentage, JSONObject jsonObject) {
        if ( 30 <= totalErrorPercentage && 50 > totalErrorPercentage ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Baixo.getValue());
            jsonObject.put("AlertType", 10);
        }

        if ( 50 <= totalErrorPercentage && 80 > totalErrorPercentage ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Medio.getValue());
            jsonObject.put("AlertType", 11);
        }

        if ( 80 <= totalErrorPercentage && 100 > totalErrorPercentage ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Alto.getValue());
            jsonObject.put("AlertType", 12);
        }

        if ( 100 <= totalErrorPercentage ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Critico.getValue());
            jsonObject.put("AlertType", 13);
        }

        if( 30 <= totalErrorPercentage ) {
            jsonObject.put("ReadingType", "Movimento");
            System.out.println("Movement Alert generated. Adding message to Database.");
            this.mq2MYMySQLDBHandler.insertAlertMessage(jsonObject);
        }
    }

    private void checkTemperatureErrorsAllowed(double totalErrorPercentage, JSONObject jsonObject) {
        if ( 30 <= totalErrorPercentage && 50 > totalErrorPercentage ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Baixo.getValue());
            jsonObject.put("AlertType", 6);
        }

        if ( 50 <= totalErrorPercentage && 80 > totalErrorPercentage ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Medio.getValue());
            jsonObject.put("AlertType", 7);
        }

        if ( 80 <= totalErrorPercentage && 100 > totalErrorPercentage ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Alto.getValue());
            jsonObject.put("AlertType", 8);
        }

        if ( 100 <= totalErrorPercentage ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Critico.getValue());
            jsonObject.put("AlertType", 9);
        }

        if( 30 <= totalErrorPercentage ) {
            jsonObject.put("ReadingType", "Temperatura");
            System.out.println("Temperature Alert generated. Adding message to Database.");
            this.mq2MYMySQLDBHandler.insertAlertMessage(jsonObject);
        }
    }

    private void checkTotalErrorsAllowed(double totalErrorPercentage, JSONObject jsonObject) {
        if ( 30 <= totalErrorPercentage && 50 > totalErrorPercentage ) {
            jsonObject.put("AlertType", 6);
            jsonObject.put("Priority", MQ2MYAlertPriority.Baixo.getValue());
        }

        if ( 50 <= totalErrorPercentage && 80 > totalErrorPercentage ) {
            jsonObject.put("AlertType", 7);
            jsonObject.put("Priority", MQ2MYAlertPriority.Medio.getValue());
        }

        if ( 80 <= totalErrorPercentage && 100 > totalErrorPercentage ) {
            jsonObject.put("AlertType", 8);
            jsonObject.put("Priority", MQ2MYAlertPriority.Alto.getValue());
        }

        if ( 100 <= totalErrorPercentage ) {
            jsonObject.put("AlertType", 9);
            jsonObject.put("Priority", MQ2MYAlertPriority.Critico.getValue());
        }

        if( 30 <= totalErrorPercentage ) {
            jsonObject.put("ReadingType", "Temperatura");
            System.out.println("Alert generated. Adding message to Database.");
            this.mq2MYMySQLDBHandler.insertAlertMessage(jsonObject);
        }
    }

    private void startSchedulers() {
        System.out.println("Starting schedulers.");
        this.schedulerTemperature.scheduleAtFixedRate( () -> {
            JSONObject item = tempMessageQueue.poll();
            if( null != item) {
                this.temperatureBatch.add(item);

                if( 10 == this.temperatureBatch.size() ) {
                    System.out.println("Processing temperature batch.");
                    this.mq2MYMySQLDBHandler.processTemperatureBatch(this.temperatureBatch);
                    this.temperatureBatch.clear();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

        this.schedulerMovements.scheduleAtFixedRate( () -> {
            JSONObject item = moveMessageQueue.poll();
            if( null != item) {
                this.movementBatch.add(item);

                if( 5 == this.movementBatch.size() ) {
                    System.out.println("Processing movement batch.");
                    this.mq2MYMySQLDBHandler.processMovementBatch(this.movementBatch);
                    this.movementBatch.clear();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

        this.schedulerAlerts.scheduleAtFixedRate( () -> {
            JSONObject item = alertMessageQueue.poll();
            if( null != item) {
                System.out.println("Processing Alert message.");
                this.mq2MYMySQLDBHandler.insertAlertMessage(item);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        this.ratMovementTimer.scheduleAtFixedRate( () -> {
            if(this.isExperienceRunning) {
                long currentTime = System.currentTimeMillis();
                synchronized (this) {

                    if( 0 == this.elapsedTimeSinceLastRatMovement ) {
                        this.elapsedTimeSinceLastRatMovement = currentTime;
                    }

                    long diff = currentTime - this.elapsedTimeSinceLastRatMovement;

                    if ( (numberOfSecondsWithoutRatMovement * 1000 ) < diff) {
                        prepareAndSendNoMovementAlertMessage();
                        this.elapsedTimeSinceLastRatMovement = currentTime;
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void prepareAndSendNoMovementAlertMessage() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ExperienceID", experienceId);

        if( MQ2MYAlertPriority.UNKNOWN == this.lastMovementPrioritySent ) {
            this.lastMovementPrioritySent = MQ2MYAlertPriority.Baixo;

            jsonObject.put("Priority", this.lastMovementPrioritySent.getValue());
            jsonObject.put("AlertType", 10);
        }

        if( MQ2MYAlertPriority.Baixo == this.lastMovementPrioritySent ) {
            this.lastMovementPrioritySent = MQ2MYAlertPriority.Medio;

            jsonObject.put("Priority", this.lastMovementPrioritySent.getValue());
            jsonObject.put("AlertType", 11);
        }

        if( MQ2MYAlertPriority.Medio == this.lastMovementPrioritySent ) {
            this.lastMovementPrioritySent = MQ2MYAlertPriority.Alto;

            jsonObject.put("Priority", this.lastMovementPrioritySent.getValue());
            jsonObject.put("AlertType", 12);
        }

        if( MQ2MYAlertPriority.Alto == this.lastMovementPrioritySent ) {
            this.lastMovementPrioritySent = MQ2MYAlertPriority.Critico;

            jsonObject.put("Priority", this.lastMovementPrioritySent.getValue());
            jsonObject.put("AlertType", 13);
        }

        if( MQ2MYAlertPriority.UNKNOWN != this.lastMovementPrioritySent ) {
            if( MQ2MYAlertPriority.Critico == this.lastMovementPrioritySent ) {
                System.out.println("Already sent a Critical Message, not sending again.");
                return;
            }

            System.out.println("Inserting no rat movement alert in MySQL.");
            jsonObject.put("ReadingType", "Movimento");
            boolean result = this.alertMessageQueue.offer(jsonObject);
            System.out.println("Message added to alert queue with result = "+result);
        }
    }

    private void handleTemperatureMaxValueReachedMessage(JSONObject jsonObject) {
        System.out.println("Handling Max Temperature reached message.");
        jsonObject.put("Priority", MQ2MYAlertPriority.Critico.getValue());
        jsonObject.put("AlertType", 5);

        boolean result = this.alertMessageQueue.offer(jsonObject);
        System.out.println("Message added to alert queue with result = "+result);
    }

    private void handleTemperatureMinValueReachedMessage(JSONObject jsonObject) {
        System.out.println("Handling Min Temperature reached message.");
        jsonObject.put("Priority", MQ2MYAlertPriority.Critico.getValue());
        jsonObject.put("AlertType", 5);

        boolean result = this.alertMessageQueue.offer(jsonObject);
        System.out.println("Message added to alert queue with result = "+result);
    }

    private void handleTemperatureOutlierMessage(JSONObject jsonObject) {
        jsonObject.put("VALID", false);

        if( 3 > this.outlierRecurrentCounter ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Baixo.getValue());
            jsonObject.put("AlertType", 1);
        }

        if( 3 <= this.outlierRecurrentCounter && 5 > this.outlierRecurrentCounter ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Medio.getValue());
            jsonObject.put("AlertType", 2);
        }

        if( 5 <= this.outlierRecurrentCounter ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Alto.getValue());
            jsonObject.put("AlertType", 3);
        }

        boolean result = this.alertMessageQueue.offer(jsonObject);
        System.out.println("Message added to alert queue with result = "+result);
    }

    private void handleTemperatureBadReadingMessage(JSONObject jsonObject) {
        jsonObject.put("VALID", false);

        if( 3 > this.invalidDataTemperatureRecurrentCounter) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Baixo.getValue());
            jsonObject.put("AlertType", 6);
        } else {

            jsonObject.put("Priority", MQ2MYAlertPriority.Medio.getValue());
            jsonObject.put("AlertType", 7);
        }

        boolean result = this.alertMessageQueue.offer(jsonObject);
        System.out.println("Message added to alert queue with result = "+result);
    }

    private void handleTemperatureMaxValueApproximationMessage(JSONObject jsonObject) {
        System.out.println("Handling Max Temperature approximation message.");
        jsonObject.put("Priority", MQ2MYAlertPriority.Alto.getValue());
        jsonObject.put("AlertType", 4);

        boolean result = this.alertMessageQueue.offer(jsonObject);
        System.out.println("Message added to alert queue with result = "+result);
    }

    private void handleTemperatureMinValueApproximationMessage(JSONObject jsonObject) {
        System.out.println("Handling Min Temperature approximation message.");
        jsonObject.put("Priority", MQ2MYAlertPriority.Alto.getValue());
        jsonObject.put("AlertType", 4);

        boolean result = this.alertMessageQueue.offer(jsonObject);
        System.out.println("Message added to alert queue with result = "+result);
    }

    private void handleMovementInvalidCorridorMessage(JSONObject jsonObject) {
        jsonObject.put("VALID", false);

        if( 3 > this.invalidDataMovementRecurrentCounter) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Baixo.getValue());
            jsonObject.put("AlertType", 10);
        } else {
            jsonObject.put("Priority", MQ2MYAlertPriority.Medio.getValue());
            jsonObject.put("AlertType", 11);
        }

        boolean result = this.alertMessageQueue.offer(jsonObject);
        System.out.println("Message added to alert queue with result = "+result);
    }

    private void handleMovementInvalidConfigMessage(JSONObject jsonObject) {
        System.out.println("Handling Invalid Labyrinth Configuration message.");
        jsonObject.put("Priority", MQ2MYAlertPriority.Critico.getValue());
        jsonObject.put("AlertType", 13);

        boolean result = this.alertMessageQueue.offer(jsonObject);
        System.out.println("Message added to alert queue with result = "+result);
    }

    private void handleInvalidDataTemperatureMessage(JSONObject jsonObject) {
        handleTemperatureBadReadingMessage(jsonObject);
    }

    private void handleInvalidDataMovementMessage(JSONObject jsonObject) {
        handleMovementInvalidCorridorMessage(jsonObject);
    }

    private void handleTemperatureMessage(JSONObject jsonObject) {
        System.out.println("Handling temperature reading message.");
        jsonObject.put("VALID", true);

        boolean result = this.tempMessageQueue.offer(jsonObject);
        System.out.println("Message added to temperature queue with result = "+result);
    }

    private void handleMovementMessage(JSONObject jsonObject) {
        System.out.println("Handling movement reading message.");

        if( MQ2MYTaskResult.OK != handleRatMovements(jsonObject) ) {
            System.out.println("Invalid movement. Proceeding.");
            handleInvalidDataMovementMessage(jsonObject);
            return;
        }

        jsonObject.put("VALID", true);

        boolean result = this.moveMessageQueue.offer(jsonObject);
        System.out.println("Message added to movement queue with result = "+result);
    }

    private MQ2MYTaskResult handleRatMovements(JSONObject jsonObject) {
        if(!isExperienceRunning) {
            System.out.println("Experience is not running, rat movement is ok.");
        } else {
            int originRoomValue = -1;
            try {
                originRoomValue = jsonObject.getInt("SalaOrigem");
            } catch (JSONException ex) {
                System.out.println("Exception caught. Cause = "+ex.getCause());
            }

            int destinationRoomValue = -1;
            try {
                destinationRoomValue = jsonObject.getInt("SalaDestino");
            } catch (JSONException ex) {
                System.out.println("Exception caught. Cause = "+ex.getCause());
            }

            if ( 0 == numRatsPerRoom.get(originRoomValue) ) {
                System.out.println("Invalid movement, room has no rats yet!");
                return MQ2MYTaskResult.ERROR_NO_RATS_IN_ROOM_TO_MOVE;
            }

            int numOriginRoom = numRatsPerRoom.get(originRoomValue);
            numOriginRoom--;
            numRatsPerRoom.put(originRoomValue, numOriginRoom);

            MQ2MYMySQLDBHandler.getInstance().updateRatInRoom(this.experienceId, originRoomValue, numOriginRoom);

            int numDestinationRoom = numRatsPerRoom.get(destinationRoomValue);
            numDestinationRoom++;
            numRatsPerRoom.put(destinationRoomValue, numDestinationRoom);

            MQ2MYMySQLDBHandler.getInstance().updateRatInRoom(this.experienceId, destinationRoomValue, numDestinationRoom);

            checkRatDistributionBetweenRooms(numDestinationRoom, jsonObject);

            synchronized (this){
                this.elapsedTimeSinceLastRatMovement = System.currentTimeMillis();
            }
        }
        return MQ2MYTaskResult.OK;
    }

    private void checkRatDistributionBetweenRooms(int numRats, JSONObject jsonObject) {
        double percentageVariation = Math.abs( (numRats) / this.maxNumberOfRatsInRoom ) * 100;

        if( 25 <= percentageVariation && 50 > percentageVariation ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Baixo.getValue());
            jsonObject.put("AlertType", 10);

        } else if ( 50 <= percentageVariation && 75 > percentageVariation ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Medio.getValue());
            jsonObject.put("AlertType", 11);

        } else if ( 75 <= percentageVariation && 99 > percentageVariation ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Alto.getValue());
            jsonObject.put("AlertType", 12);

        } else if ( 100 <= percentageVariation ) {
            jsonObject.put("Priority", MQ2MYAlertPriority.Critico.getValue());
            jsonObject.put("AlertType", 13);

        }

        if( 25 <= percentageVariation ) {
            System.out.println("Inserting rat distribution alert in MySQL");
            boolean result = this.alertMessageQueue.offer(jsonObject);
            System.out.println("Message added to alert queue with result = "+result);
        }
    }
}
