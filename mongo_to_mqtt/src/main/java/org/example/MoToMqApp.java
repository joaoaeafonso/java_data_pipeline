package org.example;

import org.example.code.common.M2MQTaskResult;
import org.example.code.handlers.M2MQMongoDBConnectionHandler;
import org.example.code.handlers.M2MQPropertyHandler;


public class MoToMqApp {
    public static void main( String[] args ) {

        M2MQPropertyHandler ph = M2MQPropertyHandler.getInstance();
        if( M2MQTaskResult.OK != ph.start() ) {
            System.out.println("Error loading properties, application start aborted");
            throw new RuntimeException();
        }

        M2MQMongoDBConnectionHandler mongoHandler = M2MQMongoDBConnectionHandler.getInstance();
        if( M2MQTaskResult.OK != mongoHandler.start() ) {
            System.out.println("Error starting MongoDB clients, application start aborted");
            throw new RuntimeException();
        }

        System.out.println("All objects Successfully instanced in MongoDB to MQTT App. Starting.");
    }
}
