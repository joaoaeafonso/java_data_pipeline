package org.example.code.handlers;

import org.example.code.common.MQ2MYPair;
import org.example.code.common.MQ2MYTaskResult;

import java.sql.*;
import java.util.Hashtable;

import static org.example.code.common.MQ2MYProperty.*;

public class MQ2MYMySQLCloudDBHandler {

    private static MQ2MYMySQLCloudDBHandler singleInstance;
    private Connection connection = null;

    private MQ2MYMySQLCloudDBHandler() {
        System.out.println("Created an instance of MQ2MYMySQLCloudDBHandler. Ready to use.");
    }

    public static MQ2MYMySQLCloudDBHandler getInstance() {
        if( null == singleInstance ){
            singleInstance = new MQ2MYMySQLCloudDBHandler();
        }
        return singleInstance;
    }

    private MQ2MYTaskResult connect() {
        MQ2MYPropertyHandler instance = MQ2MYPropertyHandler.getInstance();
        try {
            Class.forName(MQ2MYPropertyHandler.getInstance().getPropertyValue(MYSQL_CLASS_DRIVER));

            this.connection = DriverManager.getConnection(
                    getCompleteUrl(),
                    instance.getPropertyValue(MYSQL_CLOUD_DB_USER),
                    instance.getPropertyValue(MYSQL_CLOUD_DB_PASSWORD)
            );

        } catch (Exception ex) {
            System.out.println("Error connecting to MySQL Cloud Server. Cause: " + ex.getCause());
            return MQ2MYTaskResult.ERROR_CREATING_MYSQL_CLOUD_CLIENT;
        }
        return MQ2MYTaskResult.OK;
    }

    public Hashtable<MQ2MYPair<Integer, Integer>, Integer> loadLabyrinthConfig() {
        Hashtable<MQ2MYPair<Integer, Integer>, Integer> result = new Hashtable<>();
        if( null == this.connection ) {
            if( MQ2MYTaskResult.OK != connect() ) {
                System.out.println("Error connecting to MySQL Cloud server. Unable to provide Lab configuration.");

                // DUMMY CONFIG PORQUE O PROFESSOR E FIXE
                result.put(new MQ2MYPair<>(1, 2), 1);
                result.put(new MQ2MYPair<>(2, 3), 2);
                result.put(new MQ2MYPair<>(2, 4), 3);
                result.put(new MQ2MYPair<>(2, 5), 4);
                result.put(new MQ2MYPair<>(3, 6), 5);
                result.put(new MQ2MYPair<>(5, 6), 6);
                result.put(new MQ2MYPair<>(6, 7), 7);
                result.put(new MQ2MYPair<>(7, 2), 7);

                return result;
            }
        }

        try {

            Statement statement = this.connection.createStatement();
            String query = "SELECT * FROM corredor;";

            ResultSet resultSet = statement.executeQuery(query);

            result = parseResultSet(resultSet);

            resultSet.close();
            statement.close();
            this.connection.close();

        } catch (SQLException ex) {
            System.out.println("Error executing statement. Cause: " + ex.getCause());
            return result;
        }

        return result;
    }

    private Hashtable<MQ2MYPair<Integer, Integer>, Integer> parseResultSet(ResultSet resultSet) {
        Hashtable<MQ2MYPair<Integer, Integer>, Integer> result = new Hashtable<>();
        try {
            if( !resultSet.next() ) {
                System.out.println("Result set is empty. Query did not retrieve anything.");
                return result;
            } else {
                int counter = 1;
                resultSet.next();

                do {

                    int originRoom = resultSet.getInt("salaa");
                    int destinationRoom = resultSet.getInt("salab");

                    result.put(new MQ2MYPair<>(originRoom, destinationRoom), counter);
                    counter++;
                } while (resultSet.next());
            }

        } catch (Exception ex) {
            System.out.println("Error parsing result set. Cause: " + ex.getCause());
        }

        return result;
    }

    private String getCompleteUrl() {
        MQ2MYPropertyHandler instance = MQ2MYPropertyHandler.getInstance();
        return  instance.getPropertyValue(MYSQL_CLOUD_DB_HOST) +":"+
                instance.getPropertyValue(MYSQL_CLOUD_DB_PORT) +"/"+
                instance.getPropertyValue(MYSQL_CLOUD_DATABASE);
    }
}
