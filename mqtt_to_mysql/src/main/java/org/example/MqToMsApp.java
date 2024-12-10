package org.example;

import org.example.code.common.MQ2MYTaskResult;
import org.example.code.handlers.MQ2MYMqttConnectionHandler;
import org.example.code.handlers.MQ2MYPropertyHandler;
import org.example.code.managers.MQ2MYExperienceManager;
import org.example.code.managers.MQ2MYRestControllersManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class MqToMsApp {
    public static void main( String[] args ) {
        SpringApplication app = new SpringApplication(MqToMsApp.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        app.run(args);

        MQ2MYPropertyHandler ph = MQ2MYPropertyHandler.getInstance();
        if( MQ2MYTaskResult.OK != ph.start() ) {
            System.out.println("Error loading properties, application start aborted");
            throw new RuntimeException();
        }

        MQ2MYMqttConnectionHandler m2mMqttConnHandler = MQ2MYMqttConnectionHandler.getInstance();
        if( MQ2MYTaskResult.OK != m2mMqttConnHandler.start()) {
            System.out.println("Error starting MQTT clients, application start aborted");
            throw new RuntimeException();
        }

        MQ2MYExperienceManager experienceManager = MQ2MYExperienceManager.getInstance();
        if( MQ2MYTaskResult.OK != experienceManager.startExperienceManager() ) {
            System.out.println("Error starting Experience Manager, application start aborted");
            throw new RuntimeException();
        }

        MQ2MYRestControllersManager servletsManager = MQ2MYRestControllersManager.getInstance();
        if( MQ2MYTaskResult.OK != servletsManager.startServlets() ) {
            System.out.println("Error starting Servlets Manager, application start aborted");
            throw new RuntimeException();
        }

        System.out.println("All objects Successfully instanced in MQTT to MySQL App. Starting.");

    }
}
