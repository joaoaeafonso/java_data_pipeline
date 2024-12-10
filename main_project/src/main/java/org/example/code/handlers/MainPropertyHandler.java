package org.example.code.handlers;

import org.example.code.common.MainProperty;
import org.example.code.common.MainTaskResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import static org.example.code.common.MainProperty.RUNNER;

public class MainPropertyHandler {

    private static MainPropertyHandler singleInstance;

    private final Dictionary<MainProperty, String> kvs;

    private MainPropertyHandler() {
        System.out.println("Created an instance of MainPropertyHandler. Ready to use.");
        this.kvs = new Hashtable<>();
    }

    public static MainPropertyHandler getInstance() {
        if( null == singleInstance ){
            singleInstance = new MainPropertyHandler();
        }
        return singleInstance;
    }

    public String getPropertyValue(MainProperty p) {
        return kvs.get(p);
    }

    public MainTaskResult start() {
        System.out.println("Starting MainPropertyHandler. Loading all properties available in config.ini file");
        return loadProperties();
    }

    private MainTaskResult loadProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(getFileAbsolutePath()));

            kvs.put(RUNNER, properties.getProperty(RUNNER.getValue()));

            System.out.println("loadProperties with taskType result = "+ MainTaskResult.OK.name());
            return MainTaskResult.OK;

        } catch (IOException e) {
            System.out.println("Exception caught: "+e.getMessage());
            System.out.println("Task result = "+ MainTaskResult.ERROR_LOADING_PROPERTIES.name());
            return MainTaskResult.ERROR_LOADING_PROPERTIES;
        }
    }

    private String getFileAbsolutePath() {
        return "/home/jafonso/Documents/JOAO/faculdade/PISID/main_project/src/main/java/org/example/resources/config.ini";
    }
}