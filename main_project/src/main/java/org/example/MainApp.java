package org.example;

import org.example.code.common.MainTaskResult;
import org.example.code.handlers.HeartbeatMonitor;
import org.example.code.handlers.MainPropertyHandler;

import java.io.IOException;

public class MainApp {

    public static void main( String[] args ) {

        MainPropertyHandler ph = MainPropertyHandler.getInstance();
        if( MainTaskResult.OK != ph.start() ) {
            System.out.println("Error loading properties, application start aborted");
            throw new RuntimeException();
        }

        HeartbeatMonitor hm = HeartbeatMonitor.getInstance();
        if( MainTaskResult.OK != hm.startHeartbeatMonitor() ) {
            System.out.println("Error starting Heartbeat Monitor, application start aborted");
            throw new RuntimeException();
        }

        System.out.println("All objects Successfully instanced in MainApp. Starting.");
    }
}
