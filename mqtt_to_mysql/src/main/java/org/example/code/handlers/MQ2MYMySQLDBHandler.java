package org.example.code.handlers;

import org.example.code.common.MQ2MYPair;
import org.example.code.dto.mobile.MQ2MYAndroidAlertPayload;
import org.example.code.dto.mobile.MQ2MYAndroidRatsInRoomPayload;
import org.example.code.dto.mobile.MQ2MYAndroidTemperatureSensorXPayload;
import org.example.code.dto.web.MQ2MYAlertInfoPayload;
import org.example.code.dto.web.MQ2MYExperienceInformationPayload;
import org.example.code.dto.web.MQ2MYMovementReadingPayload;
import org.example.code.dto.web.MQ2MYTemperatureReadingPayload;
import org.example.code.managers.MQ2MYExperienceManager;
import org.json.JSONObject;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.example.code.common.MQ2MYProperty.*;


public class MQ2MYMySQLDBHandler {

    private static MQ2MYMySQLDBHandler singleInstance;
    private Connection connection = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private MQ2MYMySQLDBHandler() {
        System.out.println("Created an instance of MQ2MYMySQLDBHandler. Ready to use.");
    }

    public static MQ2MYMySQLDBHandler getInstance() {
        if( null == singleInstance ) {
            singleInstance = new MQ2MYMySQLDBHandler();
        }
        return singleInstance;
    }

    public void startExperience(int experienceID) {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Starting experience");

        String statementString = "{ call IniciarExperiencia(?) }";

        try {
            CallableStatement callableStatement = connection.prepareCall(statementString);

            callableStatement.setInt(1, experienceID);

            boolean execute = callableStatement.execute();
            System.out.println("Login SP called with result = "+execute);

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
        }
    }

    public List<MQ2MYAndroidTemperatureSensorXPayload> getAllTemperatureReadingsSensor1(int sensor) {
        if( null == connection ) {
            this.connect();
        }

        List<MQ2MYAndroidTemperatureSensorXPayload> result = new ArrayList<>();

        String query;
        if( 1 == sensor ) {
            query = "SELECT DataHora, Leitura FROM dados_sensor_um;";
        } else {
            query = "SELECT DataHora, Leitura FROM dados_sensor_dois;";
        }

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()) {
                MQ2MYAndroidTemperatureSensorXPayload reading = new MQ2MYAndroidTemperatureSensorXPayload();

                reading.setLeitura(resultSet.getDouble("Leitura"));
                reading.setDataHora(resultSet.getTimestamp("DataHora").toLocalDateTime());

                result.add(reading);
            }

            resultSet.close();
            ps.close();

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
        }
        return result;
    }

    public void updateRatInRoom(int experienceID, int room, int numRats) {
        if( null == connection ) {
            this.connect();
        }
        System.out.println("Updating number of rats with value = "+numRats+" to Room = "+room);

        String statementString = "{ call AddOrUpdateMoviSalaFinal(?, ?, ?) }";
        try {
            CallableStatement callableStatement = connection.prepareCall(statementString);

            callableStatement.setInt(1, room);
            callableStatement.setInt(2, experienceID);
            callableStatement.setInt(3, numRats);

            boolean execute = callableStatement.execute();
            System.out.println("Login SP called with result = "+execute);

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
        }
    }

    public List<MQ2MYAndroidRatsInRoomPayload> getAllRatsInRooms() {
        if( null == connection ) {
            this.connect();
        }

        List<MQ2MYAndroidRatsInRoomPayload> result = new ArrayList<>();
        String query = "SELECT IdSala, NumeroRatosFinal FROM total_ratos_sala;";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()) {

                MQ2MYAndroidRatsInRoomPayload reading = new MQ2MYAndroidRatsInRoomPayload();
                reading.setIdSala(resultSet.getInt("IdSala"));
                reading.setNumRats(resultSet.getInt("NumeroRatosFinal"));

                result.add(reading);
            }

            resultSet.close();
            ps.close();

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
        }
        return result;
    }

    public List<MQ2MYAndroidAlertPayload> getAllMobileRequestAlerts() {
        if( null == connection ) {
            this.connect();
        }

        List<MQ2MYAndroidAlertPayload> result = new ArrayList<>();

        String query = "SELECT Descricao, TemperaturaMedida, SalaOrigem, SalaDestino, Sensor, TipoAlerta, Hora FROM alertas_60_minutos;";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()) {
                MQ2MYAndroidAlertPayload alerta = new MQ2MYAndroidAlertPayload();
                alerta.setDescricao(resultSet.getString("Descricao"));
                alerta.setTemperaturaMedida(resultSet.getDouble("TemperaturaMedida"));
                alerta.setSalaOrigem(resultSet.getString("SalaOrigem"));
                alerta.setSalaDestino(resultSet.getString("SalaDestino"));
                alerta.setSensor(resultSet.getString("Sensor"));
                alerta.setTipoAlerta(resultSet.getString("TipoAlerta"));
                alerta.setHora(resultSet.getTimestamp("Hora").toLocalDateTime());

                result.add(alerta);
            }

            resultSet.close();
            ps.close();

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
        }
        return result;
    }

    public void stopExperience(int experienceID) {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Stopping experience");

        String statementString = "{ call TerminaExperiencia(?) }";

        try {
            CallableStatement callableStatement = connection.prepareCall(statementString);

            callableStatement.setInt(1, experienceID);

            boolean execute = callableStatement.execute();
            System.out.println("Login SP called with result = "+execute);

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
        }
    }

    public boolean isExperienceRunning() {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Checking if there is any experience running.");
        String statementString = "{call ValidaExperienciaADecorrer(?)}";

        try {
            CallableStatement callableStatement = connection.prepareCall(statementString);

            callableStatement.registerOutParameter(1, java.sql.Types.BOOLEAN);

            boolean execute = callableStatement.execute();
            System.out.println("Login SP called with result = "+execute);

            return callableStatement.getBoolean(1);

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            return true;
        }
    }

    public boolean validateLogin(String username, String password) {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Validating user login with username = "+username);

        String statementString = "{call ValidaLogin(?, ?, ?)}";

        try {
            CallableStatement callableStatement = connection.prepareCall(statementString);

            callableStatement.setString(1, username);
            callableStatement.setString(2, password);

            callableStatement.registerOutParameter(3, java.sql.Types.BOOLEAN);

            boolean execute = callableStatement.execute();
            System.out.println("Login SP called with result = "+execute);

            return callableStatement.getBoolean(3);
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
            return false;
        }
    }

    public void handleMazeConfig(Hashtable<MQ2MYPair<Integer, Integer>, Integer> config, int idExperience) {
        if( null == connection ) {
            System.out.println("MySQL connection is null, reconnecting.");
            this.connect();
        }

        System.out.println("Adding maze config to MySQL.");

        try {
            String statementString = "{ call AddCorredor(?, ?, ?, ?) }";
            CallableStatement callableStatement = this.connection.prepareCall(statementString);

            this.connection.setAutoCommit(false);

            for( MQ2MYPair<Integer, Integer> pair : config.keySet() ) {

                callableStatement.setInt(1, idExperience);
                callableStatement.setInt(2, pair.first());
                callableStatement.setInt(3, pair.second());
                callableStatement.setBoolean(4, false);

                callableStatement.addBatch();
            }

            callableStatement.executeBatch();
            this.connection.commit();

            System.out.println("Maze config added successfully.");

            callableStatement.close();

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            System.out.println("Exception Caught. Cause: "+ex.getMessage());
            System.out.println("Exception Caught. Cause: "+ Arrays.toString(ex.getStackTrace()));
            this.connect();
        }
    }

    public void processTemperatureBatch(ArrayList<JSONObject> dataList) {
        if( null == connection ) {
            System.out.println("MySQL connection is null, reconnecting.");
            this.connect();
        }

        System.out.println("Processing temperature message batch.");

        try {
            String statementString = "{ call AddMedTemp(?, ?, ?, ?, ?, ?, ?) }";
            CallableStatement callableStatement = this.connection.prepareCall(statementString);

            this.connection.setAutoCommit(false);

            for( JSONObject data : dataList ) {
                int isValid = (data.getBoolean("VALID") ) ? 1 : 0;

                callableStatement.setInt(1, data.getInt("MongoDBId"));
                callableStatement.setInt(2, data.getInt("ExperienceID"));
                callableStatement.setTimestamp(3, null);
                callableStatement.setDouble(4, data.getDouble("Leitura"));
                callableStatement.setInt(5, data.getInt("Sensor"));
                callableStatement.setInt(6, isValid);
                callableStatement.setBoolean(7, false);

                callableStatement.addBatch();
            }

            callableStatement.executeBatch();
            this.connection.commit();

            System.out.println("Added batch successfully.");

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getMessage());
        }
    }

    public void processMovementBatch(ArrayList<JSONObject> dataList) {
        if( null == connection ) {
            System.out.println("MySQL connection is null, reconnecting.");
            this.connect();
        }

        System.out.println("Processing movement message batch.");

        try {
            String statementString = "{call AddMedMovi(?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement callableStatement = this.connection.prepareCall(statementString);

            this.connection.setAutoCommit(false);

            for( JSONObject data : dataList ) {

                callableStatement.setInt(1, data.getInt("MongoDBId"));
                callableStatement.setInt(2, data.getInt("ExperienceID"));
                callableStatement.setTimestamp(3, null);
                callableStatement.setDouble(4, data.getInt("SalaOrigem"));
                callableStatement.setInt(5, data.getInt("SalaDestino"));
                callableStatement.setBoolean(6, true);
                callableStatement.setBoolean(7, false);

                callableStatement.addBatch();
            }

            callableStatement.executeBatch();
            this.connection.commit();

            System.out.println("Added batch successfully.");

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getMessage());
        }
    }

    public boolean registerUser(String username, String name, String telefone, String password, String userType) {
        if( null == connection ) {
            this.connect();
        }

        String sql = "{call CriarUtilizador(?, ?, ?, ?, ?)}";
        try {

            CallableStatement stmt = connection.prepareCall(sql);

            stmt.setString(1, username);
            stmt.setString(2, name);
            stmt.setString(3, telefone);
            stmt.setString(4, userType);
            stmt.setString(5, password);

            stmt.execute();

            System.out.println("User registered successfully.");
            return true;
        } catch (SQLException ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
            return false;
        }
    }

    public void insertAlertMessage(JSONObject jsonObject) {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Inserting alert message into MySQL Database.");

        String timestampString;
        try {
            timestampString = jsonObject.getString("Hora");
        } catch (Exception ex){
            timestampString = "";
        }

        int originRoom;
        try {
            originRoom = jsonObject.getInt("SalaOrigem");
        } catch (Exception ex){
            originRoom = -1;
        }

        int destinationRoom;
        try {
            destinationRoom = jsonObject.getInt("SalaDestino");
        } catch (Exception ex){
            destinationRoom = -1;
        }

        int sensorId;
        try {
            sensorId = jsonObject.getInt("Sensor");
        } catch (Exception ex){
            sensorId = -1;
        }

        double reading;
        try {
            reading = jsonObject.getDouble("Leitura");
        } catch (Exception ex){
            reading = -1.0;
        }

        String priority;
        try {
            priority = jsonObject.getString("Priority");
        } catch (Exception ex){
            priority = "Baixo";
        }

        int alertType;
        try {
            alertType = jsonObject.getInt("AlertType");
        } catch (Exception ex){
            alertType = 10;
        }

        String readingType;
        try {
            readingType = jsonObject.getString("ReadingType");
        } catch (Exception ex) {
            readingType = "Movimento";
        }

        int experienceID;
        try {
            experienceID = jsonObject.getInt("ExperienceID");
        } catch (Exception ex){
            experienceID = 2;
        }

        String statementString = "{call AddAlerta(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try {
            CallableStatement callableStatement = connection.prepareCall(statementString);

            callableStatement.setTimestamp(1, null);
            callableStatement.setInt(2, originRoom);
            callableStatement.setInt(3, destinationRoom);
            callableStatement.setInt(4, sensorId);
            callableStatement.setDouble(5, reading);
            callableStatement.setInt(6, experienceID);
            callableStatement.setString(7, priority);
            callableStatement.setInt(8, alertType);
            callableStatement.setString(9, readingType);
            callableStatement.setBoolean(10, false);

            boolean execute = callableStatement.execute();
            this.connection.commit();

            System.out.println("Experience Status updated with result = "+execute);

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
        }
    }

    public void createExperience(
            String description,
            String user,
            int totalNumberOfRats,
            int maxNumberOfRatsInARoom,
            int maxSecondsWithoutMovement,
            double idealTemperature,
            double maxTemperature,
            double minTemperature,
            double temperatureDelta,
            String experienceState,
            int maxNumberOfTemperatureErrors,
            int maxNumberOfMovementErrors,
            int maxNumberOfErrors
    ) {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Updating experience information in MySQL Database.");

        String statementString = "{call ParametrizarExperiencia(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try {

            CallableStatement callableStatement = connection.prepareCall(statementString);

            callableStatement.setString(1, description);
            callableStatement.setString(2, user);
            callableStatement.setInt(3, totalNumberOfRats);
            callableStatement.setInt(4, maxNumberOfRatsInARoom);
            callableStatement.setInt(5, maxSecondsWithoutMovement);
            callableStatement.setDouble(6, idealTemperature);
            callableStatement.setDouble(7, maxTemperature);
            callableStatement.setDouble(8, minTemperature);
            callableStatement.setDouble(9, temperatureDelta);
            callableStatement.setTimestamp(10, null);
            callableStatement.setTimestamp(11, null);
            callableStatement.setTimestamp(12, null);
            callableStatement.setString(13, experienceState);
            callableStatement.setInt(14, maxNumberOfTemperatureErrors);
            callableStatement.setInt(15, maxNumberOfMovementErrors);
            callableStatement.setInt(16, maxNumberOfErrors);
            callableStatement.setBoolean(17, false);

            boolean execute = callableStatement.execute();

            this.connection.commit();
            System.out.println("Experience Status updated with result = "+execute);

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
        }
    }

    public void updateExperienceStatus(JSONObject jsonObject) {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Updating experience status into MySQL Database.");

        int experienceId = jsonObject.getInt("ExperienceID");
        String status = jsonObject.getString("ExperienceStatus");

        String statementString = "{ call UpdateExperienciaStatus(?, ?) }";

        try {

            CallableStatement callableStatement = connection.prepareCall(statementString);

            callableStatement.setInt(1, experienceId);
            callableStatement.setString(2, status);

            boolean execute = callableStatement.execute();
            System.out.println("Experience Status updated with result = "+execute);

            this.connection.commit();

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
        }
    }

    public List<MQ2MYExperienceInformationPayload> getAllExperiencesAvailableToStart(String user) {
        List<MQ2MYExperienceInformationPayload> result = new ArrayList<>();

        try {

            String state = "Por iniciar";
            String query = "SELECT * FROM Experiencia where Utilizador = ? and Estado = ? ;";
            PreparedStatement statement = this.connection.prepareStatement(query);

            statement.setString(1, user);
            statement.setString(2, state);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                int experienceID = resultSet.getInt("IdExperiencia");
                String description = resultSet.getString("Descricao");
                int numRats = resultSet.getInt("NumeroRatos");
                int numRatsPerRoom = resultSet.getInt("LimiteRatosSala");
                int secsWithNoMove = resultSet.getInt("SegundosSemMovimento");
                double idealTemp = resultSet.getDouble("TemperaturaIdeal");
                double maxTemp = resultSet.getDouble("TemperaturaMaxima");
                double minTemp = resultSet.getDouble("TemperaturaMinima");
                double deltaTemp = resultSet.getDouble("VariacaoOutlier");
                String createTime = resultSet.getTimestamp("DataHoraCriacao").toString();
                int maxTempErrors = resultSet.getInt("ErrosTemperaturaAdmitidos");
                int maxMoveErrors = resultSet.getInt("ErrosMovimentoAdmitidos");
                int totalErrors = resultSet.getInt("TotalErrosAdmitidos");

                result.add( new MQ2MYExperienceInformationPayload(
                        experienceID,
                        description,
                        user,
                        numRats,
                        numRatsPerRoom,
                        secsWithNoMove,
                        idealTemp,
                        maxTemp,
                        minTemp,
                        deltaTemp,
                        createTime,
                        maxTempErrors,
                        maxMoveErrors,
                        totalErrors,
                        state
                ));
            }

            resultSet.close();
            statement.close();

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            System.out.println("Exception Caught. Cause: "+ex.getMessage());
            System.out.println("Exception Caught. Cause: "+ Arrays.toString(ex.getStackTrace()));
            this.connect();
        }

        return result;
    }

    public List<MQ2MYTemperatureReadingPayload> getAllTemperatureReadingForExperience(int idExperience) {
        List<MQ2MYTemperatureReadingPayload> result = new ArrayList<>();

        try {
            String query = "SELECT * FROM MedicoesTemperatura where IdExperiencia = ? and Valido = ?;";
            PreparedStatement statement = this.connection.prepareStatement(query);

            statement.setInt(1, idExperience);
            statement.setBoolean(2, true);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                int idMedicao = resultSet.getInt("IdMedicaoTemperatura");
                int idMongo = resultSet.getInt("IdMongo");
                String timestamp = resultSet.getTimestamp("DataHora").toString();
                double leitura = resultSet.getDouble("Leitura");
                int sensor = resultSet.getInt("Sensor");

                result.add( new MQ2MYTemperatureReadingPayload(
                        idMedicao,
                        idMongo,
                        idExperience,
                        timestamp,
                        leitura,
                        sensor
                ) );
            }

            resultSet.close();
            statement.close();

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
        }

        return result;
    }

    public List<MQ2MYMovementReadingPayload> getAllMovementReadingForExperience(int idExperience) {
        List<MQ2MYMovementReadingPayload> result = new ArrayList<>();

        try {
            String query = "SELECT * FROM MedicoesMovimento where IdExperiencia = ?;";
            PreparedStatement statement = this.connection.prepareStatement(query);

            statement.setInt(1, idExperience);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                int idMedicao = resultSet.getInt("IdMedicaoPassagem");
                int idMongo = resultSet.getInt("IdMongo");
                String timestamp = resultSet.getTimestamp("DataHora").toString();
                int salaOrigem = resultSet.getInt("SalaOrigem");
                int salaDestino = resultSet.getInt("SalaDestino");

                result.add( new MQ2MYMovementReadingPayload(
                        idMedicao,
                        idMongo,
                        idExperience,
                        timestamp,
                        salaOrigem,
                        salaDestino
                ));
            }

            resultSet.close();
            statement.close();

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
        }

        return result;
    }

    public List<MQ2MYAlertInfoPayload> getAllAlertsInformation(int idExperience, String priority, String readingType) {
        List<MQ2MYAlertInfoPayload> result = new ArrayList<>();

        try {
            String query = "SELECT * FROM Alerta where IdExperiencia = ? and Prioridade = ? and TipoMedicao = ?;";
            PreparedStatement statement = this.connection.prepareStatement(query);

            statement.setInt(1, idExperience);
            statement.setString(2, priority);
            statement.setString(3, readingType);

            ResultSet resultSet = statement.executeQuery();

            if( resultSet == null ) {
                System.out.println("No items to display, continue.");
            }

            while(true) {
                assert resultSet != null;
                if (!resultSet.next()) break;

                int idAlerta;
                try {
                    idAlerta = resultSet.getInt("IdAlerta");
                } catch (Exception ex) {
                    idAlerta = -1;
                }

                String timestamp;
                try {
                    timestamp = resultSet.getTimestamp("Hora").toString();
                } catch (Exception ex) {
                    timestamp = "";
                }

                int sala;
                try {
                    sala = resultSet.getInt("Sala");
                } catch (Exception ex) {
                    sala = -1;
                }

                int sensor;
                try {
                    sensor = resultSet.getInt("Sensor");
                } catch (Exception ex) {
                    sensor = -1;
                }

                double tempReading;
                try {
                    tempReading = resultSet.getDouble("TemperaturaMedida");
                } catch (Exception ex) {
                    tempReading = -1.0;
                }

                int alertType;
                try {
                    alertType = resultSet.getInt("TipoAlerta");
                } catch (Exception ex) {
                    alertType = -1;
                }

                result.add(new MQ2MYAlertInfoPayload(
                        idAlerta,
                        idExperience,
                        timestamp,
                        sala,
                        sensor,
                        tempReading,
                        priority,
                        alertType,
                        readingType
                ));
            }

            resultSet.close();
            statement.close();

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            System.out.println("Exception Caught. Cause: "+ex.getMessage());
            System.out.println("Exception Caught. Cause: "+ Arrays.toString(ex.getStackTrace()));
            this.connect();
        }

        return result;
    }

    public void updateFinalNumberOfRatsPerRoom(JSONObject jsonObject) {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Updating number of rats per room into MySQL Database.");

        Hashtable<Integer, Integer> numRatsPerRoom = MQ2MYExperienceManager.getInstance().getFinalRatMovement();

        int experienceId = jsonObject.getInt("ExperienceID");
        try {
            this.connection.setAutoCommit(false);

            String statementString = "{ call AddMoviSalaFinal(?, ?, ?) }";

            CallableStatement callableStatement = connection.prepareCall(statementString);

            for (Map.Entry<Integer, Integer> entry : numRatsPerRoom.entrySet()) {
                int roomId = entry.getKey();
                int numberOfRats = entry.getValue();

                callableStatement.setInt(1, roomId);
                callableStatement.setInt(2, experienceId);
                callableStatement.setInt(3, numberOfRats);

                callableStatement.addBatch();
            }

            callableStatement.executeBatch();
            this.connection.commit();

            System.out.println("Updated final rat movements information with success.");

        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
        }
    }

    public void softCleanAllReadingTables() {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Cleaning all reading tables in MySQL Database.");
        String statementString = "{call SoftDelete()}";

        try {

            CallableStatement callableStatement = connection.prepareCall(statementString);

            boolean execute = callableStatement.execute();
            System.out.println("All reading tables cleared with value = "+execute);

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
        }
    }

    public void hardCleanAllReadingTables() {
        if( null == connection ) {
            this.connect();
        }

        System.out.println("Cleaning all reading tables in MySQL Database.");
        String statementString = "{call HardDelete()}";

        try {

            CallableStatement callableStatement = connection.prepareCall(statementString);

            boolean execute = callableStatement.execute();
            System.out.println("All reading tables cleared with value = "+execute);

            callableStatement.close();
        } catch (Exception ex) {
            System.out.println("Exception Caught. Cause: "+ex.getCause());
            this.connect();
        }
    }

    public void connect() {
        MQ2MYPropertyHandler instance = MQ2MYPropertyHandler.getInstance();
        try {

            Class.forName(MQ2MYPropertyHandler.getInstance().getPropertyValue(MYSQL_CLASS_DRIVER));

            this.connection = DriverManager.getConnection(
                    getCompleteUrl(),
                    instance.getPropertyValue(MYSQL_DB_USER),
                    instance.getPropertyValue(MYSQL_DB_PASSWORD)
            );
            System.out.println("Successfully connected to MySQL.");

        } catch (Exception ex) {
            System.out.println("Error connecting to MySQL Server. Cause: " + ex.getMessage());
        }
    }

    private String getCompleteUrl() {
        MQ2MYPropertyHandler instance = MQ2MYPropertyHandler.getInstance();
        return  instance.getPropertyValue(MYSQL_DB_HOST) +":"+
                instance.getPropertyValue(MYSQL_DB_PORT) +"/"+
                instance.getPropertyValue(MYSQL_DATABASE);
    }

}
