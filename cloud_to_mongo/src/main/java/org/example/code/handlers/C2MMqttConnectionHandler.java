package org.example.code.handlers;

import org.example.code.common.C2MTaskCompleteListener;
import org.example.code.common.C2MTaskResult;

import java.util.concurrent.CompletableFuture;

public class C2MMqttConnectionHandler {

    private static C2MMqttConnectionHandler singleInstance;
    private C2MMqttConnectionHandlerThreadRunner runnerClientOneLab = null;
    private C2MMqttConnectionHandlerThreadRunner runnerCLientTwoTemp = null;

    private C2MMqttConnectionHandler() {
        System.out.println("Created an instance of C2MMqttConnectionHandler. Ready to use.");
    }

    public static C2MMqttConnectionHandler getInstance() {
        if( null == singleInstance ){
            singleInstance = new C2MMqttConnectionHandler();
        }
        return singleInstance;
    }

    public C2MTaskResult start() {
        final CompletableFuture<C2MTaskResult> resultFutureOne = new CompletableFuture<>();
        final CompletableFuture<C2MTaskResult> resultFutureTwo = new CompletableFuture<>();

        C2MTaskCompleteListener listenerClientOne = returnTaskListener(resultFutureOne);
        C2MTaskCompleteListener listenerClientTwo = returnTaskListener(resultFutureTwo);

        runnerClientOneLab = new C2MMqttConnectionHandlerThreadRunner(listenerClientOne, true);
        runnerCLientTwoTemp = new C2MMqttConnectionHandlerThreadRunner(listenerClientTwo, false);

        runnerClientOneLab.start();
        runnerCLientTwoTemp.start();

        if( C2MTaskResult.OK != resultFutureOne.join() || C2MTaskResult.OK != resultFutureTwo.join()) {
            return C2MTaskResult.ERROR_MQTT_THREAD_STARTER;
        }
        return C2MTaskResult.OK;
    }

    public static C2MTaskCompleteListener returnTaskListener(CompletableFuture<C2MTaskResult> resultFuture) {
        return resultFuture::complete;
    }
}
