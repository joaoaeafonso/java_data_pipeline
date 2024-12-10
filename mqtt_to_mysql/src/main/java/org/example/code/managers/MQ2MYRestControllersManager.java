package org.example.code.managers;

import org.example.code.common.MQ2MYTaskResult;

public class MQ2MYRestControllersManager {

    private static MQ2MYRestControllersManager singleInstance;

    private MQ2MYRestControllersManager() {
        System.out.println("Created an instance of MQ2MYRestControllersManager. Ready to use.");
    }

    public static MQ2MYRestControllersManager getInstance() {
        if( null == singleInstance ){
            singleInstance = new MQ2MYRestControllersManager();
        }
        return singleInstance;
    }

    public MQ2MYTaskResult startServlets() {
        System.out.println("Starting all HTTP Servlets.");

        return MQ2MYTaskResult.OK;
    }

}
