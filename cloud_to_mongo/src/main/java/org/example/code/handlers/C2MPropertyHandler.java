package org.example.code.handlers;

import org.example.code.common.C2MProperty;
import org.example.code.common.C2MTaskResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import static org.example.code.common.C2MProperty.*;

public class C2MPropertyHandler {

    private static C2MPropertyHandler singleInstance;

    private final Dictionary<C2MProperty, String> kvs;

    private C2MPropertyHandler() {
        System.out.println("Created an instance of PropertyHandlerImpl. Ready to use.");
        this.kvs = new Hashtable<>();
    }

    public static C2MPropertyHandler getInstance() {
        if( null == singleInstance ){
            singleInstance = new C2MPropertyHandler();
        }
        return singleInstance;
    }

    public String getPropertyValue(C2MProperty p) {
        return kvs.get(p);
    }

    public C2MTaskResult start() {
        System.out.println("Starting PropertyHandlerImpl. Loading all properties available in config.ini file");
        return loadProperties();
    }

    private C2MTaskResult loadProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(getFileAbsolutePath()));

            kvs.put(MQTT_CLOUD_RECEIVER, properties.getProperty(MQTT_CLOUD_RECEIVER.getValue()));
            kvs.put(MQTT_TOPIC_RECEIVER_TEMP_G16, properties.getProperty(MQTT_TOPIC_RECEIVER_TEMP_G16.getValue()));
            kvs.put(MQTT_TOPIC_RECEIVER_LAB_G16, properties.getProperty(MQTT_TOPIC_RECEIVER_LAB_G16.getValue()));
            kvs.put(MONGODB_ADDRESS, properties.getProperty(MONGODB_ADDRESS.getValue()));
            kvs.put(MONGODB_USER, properties.getProperty(MONGODB_USER.getValue()));
            kvs.put(MONGODB_USER_PASSWORD, properties.getProperty(MONGODB_USER_PASSWORD.getValue()));
            kvs.put(MONGODB_AUTHENTICATION_ENABLED, properties.getProperty(MONGODB_AUTHENTICATION_ENABLED.getValue()));
            kvs.put(MONGODB_REPLICA, properties.getProperty(MONGODB_REPLICA.getValue()));
            kvs.put(MONGODB_DATABASE, properties.getProperty(MONGODB_DATABASE.getValue()));
            kvs.put(MONGODB_COLLECTION_TEMP, properties.getProperty(MONGODB_COLLECTION_TEMP.getValue()));
            kvs.put(MONGODB_COLLECTION_LAB, properties.getProperty(MONGODB_COLLECTION_LAB.getValue()));
            kvs.put(RUNNER, properties.getProperty(RUNNER.getValue()));

            System.out.println("loadProperties with taskType result = "+ C2MTaskResult.OK.name());
            return C2MTaskResult.OK;

        } catch (IOException e) {
            System.out.println("Exception caught: "+e.getMessage());
            System.out.println("Task result = "+ C2MTaskResult.ERROR_LOADING_PROPERTIES.name());
            return C2MTaskResult.ERROR_LOADING_PROPERTIES;
        }
    }

    private String getFileAbsolutePath() {
        return "/home/jafonso/Documents/JOAO/faculdade/PISID/cloud_to_mongo/src/main/java/org/example/resources/config.ini";
    }
}
