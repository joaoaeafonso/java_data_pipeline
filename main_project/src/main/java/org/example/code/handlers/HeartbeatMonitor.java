package org.example.code.handlers;

import org.example.CtoMApp;
import org.example.MoToMqApp;
import org.example.MqToMsApp;
import org.example.code.common.MainProperty;
import org.example.code.common.MainTaskResult;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class HeartbeatMonitor {

    private static HeartbeatMonitor singleInstance;
    private final Map<Class<?>, Object> objectClassMap = new HashMap<>();

    private HeartbeatMonitor() {
        System.out.println("Created an instance of HeartbeatMonitor. Ready to use.");
    }

    public static HeartbeatMonitor getInstance() {
        if( null == singleInstance ) {
            singleInstance = new HeartbeatMonitor();
        }
        return singleInstance;
    }

    public MainTaskResult startHeartbeatMonitor() {
        MainPropertyHandler instance = MainPropertyHandler.getInstance();

        if( instance.getPropertyValue(MainProperty.RUNNER).contains("pc1") ) {
            addObjectToMonitor(CtoMApp.class, null);
            addObjectToMonitor(MoToMqApp.class, null);

        } else {
            addObjectToMonitor(MqToMsApp.class, null);

        }

        startHeartbeatMonitoring();

        return MainTaskResult.OK;
    }

    private void startHeartbeatMonitoring() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkObjectStatus();
            }
        }, 0, 3000);
    }

    private void checkObjectStatus() {
        System.out.println("Starting object heartbeat check.");
        String[] args = {};

        for (Map.Entry<Class<?>, Object> entry : objectClassMap.entrySet()) {
            Class<?> cls = entry.getKey();
            Object object = entry.getValue();

            if (object == null) {
                System.out.println("Object has been reclaimed by garbage collector.");
                try {
                    Constructor<?> constructor = cls.getDeclaredConstructor();
                    Object item = constructor.newInstance();

                    objectClassMap.put(cls, item);

                    Method mainMethod = item.getClass().getDeclaredMethod("main", String[].class);
                    mainMethod.invoke(null, (Object) args);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    System.out.println("Exception occurred: "+e.getCause());
                }
            }
        }
    }

    private void addObjectToMonitor(Class<?> cls, Object object) {
        objectClassMap.put(cls, object);
    }
}
