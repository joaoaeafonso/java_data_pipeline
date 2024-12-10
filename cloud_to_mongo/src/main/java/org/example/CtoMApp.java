package org.example;

import org.example.code.common.C2MTaskResult;
import org.example.code.handlers.C2MMqttConnectionHandler;
import org.example.code.handlers.C2MPropertyHandler;

public class CtoMApp {

    public static void main( String[] args ) {

        C2MPropertyHandler c2MPropertyHandler = C2MPropertyHandler.getInstance();
        if( C2MTaskResult.OK != c2MPropertyHandler.start()) {
            System.out.println("Error loading properties, application start aborted");
            throw new RuntimeException();
        }

        C2MMqttConnectionHandler c2MMqttConnectionHandler = C2MMqttConnectionHandler.getInstance();
        if( C2MTaskResult.OK != c2MMqttConnectionHandler.start()) {
            System.out.println("Error starting MQTT clients, application start aborted");
            throw new RuntimeException();
        }

        System.out.println("All objects Successfully instanced in Cloud to MongoDB App. Starting.");
    }
}
