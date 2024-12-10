package org.example.code.handlers;

import org.example.code.common.MQ2MYProperty;
import org.example.code.common.MQ2MYTaskResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import static org.example.code.common.MQ2MYProperty.*;

public class MQ2MYPropertyHandler {

    private static MQ2MYPropertyHandler singleInstance;
    private final Dictionary<MQ2MYProperty, String> kvs;

    private MQ2MYPropertyHandler() {
        System.out.println("Created an instance of MQ2MYPropertyHandler. Ready to use.");
        this.kvs = new Hashtable<>();
    }

    public static MQ2MYPropertyHandler getInstance() {
        if( null == singleInstance ){
            singleInstance = new MQ2MYPropertyHandler();
        }
        return singleInstance;
    }

    public String getPropertyValue(MQ2MYProperty p) {
        return kvs.get(p);
    }

    public MQ2MYTaskResult start() {
        System.out.println("Starting MQ2MYPropertyHandler. Loading all properties available in config.ini file");
        return loadProperties();
    }

    private MQ2MYTaskResult loadProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(getFileAbsolutePath()));

            kvs.put(MQTT_CLOUD_RECEIVER, properties.getProperty(MQTT_CLOUD_RECEIVER.getValue()));
            kvs.put(MQTT_TOPIC_RECEIVER_TEMP_G16, properties.getProperty(MQTT_TOPIC_RECEIVER_TEMP_G16.getValue()));
            kvs.put(MQTT_TOPIC_RECEIVER_LAB_G16, properties.getProperty(MQTT_TOPIC_RECEIVER_LAB_G16.getValue()));
            kvs.put(MYSQL_CLASS_DRIVER, properties.getProperty(MYSQL_CLASS_DRIVER.getValue()));
            kvs.put(MYSQL_CLOUD_DB_USER, properties.getProperty(MYSQL_CLOUD_DB_USER.getValue()));
            kvs.put(MYSQL_CLOUD_DB_PASSWORD, properties.getProperty(MYSQL_CLOUD_DB_PASSWORD.getValue()));
            kvs.put(MYSQL_CLOUD_DB_HOST, properties.getProperty(MYSQL_CLOUD_DB_HOST.getValue()));
            kvs.put(MYSQL_CLOUD_DB_PORT, properties.getProperty(MYSQL_CLOUD_DB_PORT.getValue()));
            kvs.put(MYSQL_CLOUD_DATABASE, properties.getProperty(MYSQL_CLOUD_DATABASE.getValue()));
            kvs.put(MYSQL_DB_USER, properties.getProperty(MYSQL_DB_USER.getValue()));
            kvs.put(MYSQL_DB_PASSWORD, properties.getProperty(MYSQL_DB_PASSWORD.getValue()));
            kvs.put(MYSQL_DB_HOST, properties.getProperty(MYSQL_DB_HOST.getValue()));
            kvs.put(MYSQL_DB_PORT, properties.getProperty(MYSQL_DB_PORT.getValue()));
            kvs.put(MYSQL_DATABASE, properties.getProperty(MYSQL_DATABASE.getValue()));
            kvs.put(RUNNER, properties.getProperty(RUNNER.getValue()));

            System.out.println("loadProperties with taskType result = "+ MQ2MYTaskResult.OK.name());
            return MQ2MYTaskResult.OK;

        } catch (IOException e) {
            System.out.println("Exception caught: "+e.getMessage());
            System.out.println("Task result = "+ MQ2MYTaskResult.ERROR_LOADING_PROPERTIES.name());
            return MQ2MYTaskResult.ERROR_LOADING_PROPERTIES;
        }
    }

    private String getFileAbsolutePath() {
        return "/home/jafonso/Documents/JOAO/faculdade/PISID/mqtt_to_mysql/src/main/java/org/example/resources/config.ini";
    }
}
