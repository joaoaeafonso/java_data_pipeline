package org.example.code.handlers;

import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.example.code.common.M2MQTaskCompleteListener;
import org.example.code.common.M2MQTaskResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.example.code.common.M2MQProperty.*;

public class M2MQMongoDBConnectionHandlerRunner {

    private final M2MQTaskCompleteListener listener;
    private final Boolean isLab;
    private MongoDatabase mongoDB;
    private MongoCollection<Document> mongoDBCollectionTemp;
    private MongoCollection<Document> mongoDBCollectionMove;
    private final M2MQMqttConnectionHandler m2MQMqttConnectionHandler;

    M2MQMongoDBConnectionHandlerRunner(M2MQTaskCompleteListener listener, Boolean isLab) {
        this.listener = listener;
        this.isLab = isLab;
        this.m2MQMqttConnectionHandler = new M2MQMqttConnectionHandler(this.isLab);
    }

    public void startExecution(){
        M2MQTaskResult result = startRunner();
        listener.onTaskComplete(result);
    }

    private M2MQTaskResult startRunner() {
        System.out.println("Starting MongoDB Connection Handler Runner");

        if( M2MQTaskResult.OK != createMongoDBConnection() ) {
            System.out.println("Error starting mongoDB client. Aborting.");
            return M2MQTaskResult.ERROR_MONGODB_START_UP;
        }

        if( M2MQTaskResult.OK != m2MQMqttConnectionHandler.startClient() ) {
            System.out.println("Error starting Mqtt client. Aborting.");
            return M2MQTaskResult.ERROR_MQTT_START_UP;
        }

        handleTempCollectionChangeEvents();
        handleMoveCollectionChangeEvents();
        this.m2MQMqttConnectionHandler.startPostingTempMessagesToMqttTopic();
        this.m2MQMqttConnectionHandler.startPostingMoveMessagesToMqttTopic();

        return M2MQTaskResult.OK;
    }

    private M2MQTaskResult createMongoDBConnection() {
        System.out.println("Attempting connection to MongoDB client in M2MQ Application.");
        try {
            M2MQPropertyHandler instance = M2MQPropertyHandler.getInstance();
            MongoClient mongoClient = MongoClients.create(getMongoCompleteUri());

            mongoDB = mongoClient.getDatabase(instance.getPropertyValue(MONGODB_DATABASE));
            mongoDBCollectionTemp = mongoDB.getCollection(instance.getPropertyValue(MONGODB_COLLECTION_TEMP));
            mongoDBCollectionMove = mongoDB.getCollection(instance.getPropertyValue(MONGODB_COLLECTION_LAB));
            System.out.println("Connection successful. MongoDB in M2MQ Application is now available to use");
            return M2MQTaskResult.OK;
        } catch (Exception e) {
            System.out.println("Exception caught: "+e.getMessage());
            return M2MQTaskResult.ERROR_MONGODB_START_UP;
        }
    }

    private void handleTempCollectionChangeEvents() {
        CompletableFuture.runAsync( () -> {
            mongoDBCollectionTemp.watch().forEach((Consumer<? super ChangeStreamDocument<Document>>) item -> {
                Document document = item.getFullDocument();

                assert null != document;
                System.out.println("Adding document to Temperature MQTT queue = "+document.toJson());
                m2MQMqttConnectionHandler.addDocumentToTempQueue(document);
            });
        });
    }

    private void handleMoveCollectionChangeEvents() {
        CompletableFuture.runAsync( () -> {
            mongoDBCollectionMove.watch().forEach((Consumer<? super ChangeStreamDocument<Document>>) item -> {
                Document document = item.getFullDocument();

                assert null != document;
                System.out.println("Adding document to Movement MQTT queue = "+document.toJson());
                m2MQMqttConnectionHandler.addDocumentToMoveQueue(document);
            });
        } );
    }

    private String getMongoCompleteUri() {
        String mongoAddress = M2MQPropertyHandler.getInstance().getPropertyValue(MONGODB_ADDRESS) + "/";
        String replicaSet = "?replicaSet="+ M2MQPropertyHandler.getInstance().getPropertyValue(MONGODB_REPLICA);
        return "mongodb://"+mongoAddress+replicaSet;
    }
}
