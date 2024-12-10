package org.example.code.handlers;

import org.example.code.common.MQ2MYTaskCompleteListener;
import org.example.code.common.MQ2MYTaskResult;

import java.util.concurrent.CompletableFuture;

public class MQ2MYMqttConnectionHandler {

    private static MQ2MYMqttConnectionHandler singleInstance;
    private MQ2MYMqttConnectionHandlerThreadRunner runnerClientOneLab = null;
    private MQ2MYMqttConnectionHandlerThreadRunner runnerCLientTwoTemp = null;

    private MQ2MYMqttConnectionHandler() {
        System.out.println("Created an instance of MQ2MYMqttConnectionHandler. Ready to use.");
    }

    public static MQ2MYMqttConnectionHandler getInstance() {
        if( null == singleInstance ){
            singleInstance = new MQ2MYMqttConnectionHandler();
        }
        return singleInstance;
    }

    public MQ2MYTaskResult start() {
        final CompletableFuture<MQ2MYTaskResult> resultFutureOne = new CompletableFuture<>();
        final CompletableFuture<MQ2MYTaskResult> resultFutureTwo = new CompletableFuture<>();

        MQ2MYTaskCompleteListener listenerClientOne = returnTaskListener(resultFutureOne);
        MQ2MYTaskCompleteListener listenerClientTwo = returnTaskListener(resultFutureTwo);

        runnerClientOneLab = new MQ2MYMqttConnectionHandlerThreadRunner(listenerClientOne, true);
        runnerCLientTwoTemp = new MQ2MYMqttConnectionHandlerThreadRunner(listenerClientTwo, false);

        runnerClientOneLab.start();
        runnerCLientTwoTemp.start();

        if( MQ2MYTaskResult.OK != resultFutureOne.join() || MQ2MYTaskResult.OK != resultFutureTwo.join()) {
            return MQ2MYTaskResult.ERROR_MQTT_THREAD_STARTER;
        }
        return MQ2MYTaskResult.OK;
    }

    public static MQ2MYTaskCompleteListener returnTaskListener(CompletableFuture<MQ2MYTaskResult> resultFuture) {
        return resultFuture::complete;
    }
}