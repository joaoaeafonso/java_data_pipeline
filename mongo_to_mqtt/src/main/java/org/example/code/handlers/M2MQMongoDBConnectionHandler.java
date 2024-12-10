package org.example.code.handlers;

import org.example.code.common.M2MQTaskCompleteListener;
import org.example.code.common.M2MQTaskResult;
import java.util.concurrent.CompletableFuture;

public class M2MQMongoDBConnectionHandler {

    private static M2MQMongoDBConnectionHandler singleInstance;
    private M2MQMongoDBConnectionHandlerRunner runnerClientOneLab = null;

    private M2MQMongoDBConnectionHandler() {
        System.out.println("Created an instance of M2MQMongoDBConnectionHandler. Ready to use.");
    }

    public static M2MQMongoDBConnectionHandler getInstance() {
        if( null == singleInstance ){
            singleInstance = new M2MQMongoDBConnectionHandler();
        }
        return singleInstance;
    }

    public M2MQTaskResult start() {
        final CompletableFuture<M2MQTaskResult> resultFuture = new CompletableFuture<>();

        M2MQTaskCompleteListener listenerClientOne = returnTaskListener(resultFuture);

        runnerClientOneLab = new M2MQMongoDBConnectionHandlerRunner(listenerClientOne, true);

        runnerClientOneLab.startExecution();

        if( M2MQTaskResult.OK != resultFuture.join() ) {
            return M2MQTaskResult.ERROR_MONGO_THREAD_STARTER;
        }
        return M2MQTaskResult.OK;
    }

    public static M2MQTaskCompleteListener returnTaskListener(CompletableFuture<M2MQTaskResult> resultFuture) {
        return resultFuture::complete;
    }
}
