package org.example.code.handlers;

import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.example.code.common.C2MProperty.*;

public class C2MMongoDBHandler {
    private MongoDatabase mongoDB;
    private MongoCollection<Document> mongoDBCollection;
    private final boolean isLab;
    private List<Document> documentBatch;
    private final int BATCH_SIZE = 10;

    public C2MMongoDBHandler(boolean isLab) {
        System.out.println("Created an instance of C2MMongoDBHandler. Ready to use.");
        this.isLab = isLab;
        connect();
        this.documentBatch = new ArrayList<>();
    }

    public void insertData(String data) {
        try {
            Document document = Document.parse(data);

            documentBatch.add(document);
            if( BATCH_SIZE == documentBatch.size() ) {
                System.out.println("Inserting batch into MongoDB.");
                mongoDBCollection.insertMany(documentBatch);
                documentBatch.clear();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public int getLastInsertedDocumentId() {
        try {
            FindIterable<Document> iterable = mongoDBCollection.find().sort(Sorts.descending("MongoDBId")).limit(1);
            Document lastDocument = iterable.first();
            int result;

            if( null == lastDocument ) {
                return 1;
            }

            JSONObject jsonObject = new JSONObject(lastDocument.toJson());
            result = jsonObject.getInt("MongoDBId");
            return result;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    private void connect() {
        System.out.println("Attempting connection to MongoDB client.");
        try {
            C2MPropertyHandler instance = C2MPropertyHandler.getInstance();
            MongoClient mongoClient = MongoClients.create(getMongoCompleteUri());

            mongoDB = mongoClient.getDatabase(instance.getPropertyValue(MONGODB_DATABASE));
            mongoDBCollection = mongoDB.getCollection(instance.getPropertyValue( (!isLab) ? MONGODB_COLLECTION_TEMP : MONGODB_COLLECTION_LAB));
            System.out.println("Connection successful. MongoDB is now available to use");
        } catch (Exception e) {
            System.out.println("Exception caught: "+e.getMessage());
        }
    }

    private String getMongoCompleteUri() {
        C2MPropertyHandler instance = C2MPropertyHandler.getInstance();
        String mongoAddress = instance.getPropertyValue(MONGODB_ADDRESS) + "/";
        String replicaSet = "?replicaSet="+ C2MPropertyHandler.getInstance().getPropertyValue(MONGODB_REPLICA);
        String uri = "mongodb://"+mongoAddress+replicaSet;
        return uri;
    }
}
