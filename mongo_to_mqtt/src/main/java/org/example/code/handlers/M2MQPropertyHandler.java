package org.example.code.handlers;

import org.example.code.common.M2MQProperty;
import org.example.code.common.M2MQTaskResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import static org.example.code.common.M2MQProperty.*;

public class M2MQPropertyHandler {

    private static M2MQPropertyHandler singleInstance;

    private final Dictionary<M2MQProperty, String> kvs;

    private M2MQPropertyHandler() {
        System.out.println("Created an instance of PropertyHandlerImpl. Ready to use.");
        this.kvs = new Hashtable<>();
    }

    public static M2MQPropertyHandler getInstance() {
        if( null == singleInstance ){
            singleInstance = new M2MQPropertyHandler();
        }
        return singleInstance;
    }

    public String getPropertyValue(M2MQProperty p) {
        return kvs.get(p);
    }

    public M2MQTaskResult start() {
        System.out.println("Starting PropertyHandlerImpl. Loading all properties available in config.ini file");
        return loadProperties();
    }

    private M2MQTaskResult loadProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(getFileAbsolutePath()));

            kvs.put(MQTT_CLOUD_SENDER, properties.getProperty(MQTT_CLOUD_SENDER.getValue()));
            kvs.put(MQTT_TOPIC_SENDER_TEMP_G16, properties.getProperty(MQTT_TOPIC_SENDER_TEMP_G16.getValue()));
            kvs.put(MQTT_TOPIC_SENDER_LAB_G16, properties.getProperty(MQTT_TOPIC_SENDER_LAB_G16.getValue()));
            kvs.put(MONGODB_ADDRESS, properties.getProperty(MONGODB_ADDRESS.getValue()));
            kvs.put(MONGODB_USER, properties.getProperty(MONGODB_USER.getValue()));
            kvs.put(MONGODB_USER_PASSWORD, properties.getProperty(MONGODB_USER_PASSWORD.getValue()));
            kvs.put(MONGODB_AUTHENTICATION_ENABLED, properties.getProperty(MONGODB_AUTHENTICATION_ENABLED.getValue()));
            kvs.put(MONGODB_REPLICA, properties.getProperty(MONGODB_REPLICA.getValue()));
            kvs.put(MONGODB_DATABASE, properties.getProperty(MONGODB_DATABASE.getValue()));
            kvs.put(MONGODB_COLLECTION_TEMP, properties.getProperty(MONGODB_COLLECTION_TEMP.getValue()));
            kvs.put(MONGODB_COLLECTION_LAB, properties.getProperty(MONGODB_COLLECTION_LAB.getValue()));
            kvs.put(RUNNER, properties.getProperty(RUNNER.getValue()));

            System.out.println("loadProperties with taskType result = "+ M2MQTaskResult.OK.name());
            return M2MQTaskResult.OK;

        } catch (IOException e) {
            System.out.println("Exception caught: "+e.getMessage());
            System.out.println("Task result = "+ M2MQTaskResult.ERROR_LOADING_PROPERTIES.name());
            return M2MQTaskResult.ERROR_LOADING_PROPERTIES;
        }
    }

    private String getFileAbsolutePath() {
        return "/home/jafonso/Documents/JOAO/faculdade/PISID/mongo_to_mqtt/src/main/java/org/example/resources/config.ini";
    }
}
