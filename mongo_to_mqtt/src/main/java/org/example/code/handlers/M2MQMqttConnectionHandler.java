package org.example.code.handlers;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.code.common.M2MQProperty;
import org.example.code.common.M2MQTaskResult;

import org.bson.Document;

import java.util.concurrent.*;

import static org.example.code.common.M2MQProperty.*;

public class M2MQMqttConnectionHandler {

    private MqttClient client = null;
    private final BlockingQueue<Document> tempMessageQueue;
    private final BlockingQueue<Document> moveMessageQueue;
    private final ScheduledExecutorService schedulerTemperature;
    private final ScheduledExecutorService schedulerMovements;
    private final boolean isLab;

    M2MQMqttConnectionHandler(Boolean isLab) {
        System.out.println("Created an instance of M2MQMqttConnectionHandler. Ready to use.");
        this.isLab = isLab;
        this.tempMessageQueue = new LinkedBlockingQueue<>();
        this.moveMessageQueue = new LinkedBlockingQueue<>();
        this.schedulerTemperature = Executors.newSingleThreadScheduledExecutor();
        this.schedulerMovements = Executors.newSingleThreadScheduledExecutor();
    }

    public M2MQTaskResult startClient() {
        System.out.println("Starting MQTT Client in M2MQ Application.");
        if( M2MQTaskResult.OK != createMqttClients() ) {
            System.out.println("Error starting MqttHandler. Aborting execution");
            return M2MQTaskResult.ERROR_MQTT_START_UP;
        }

        if( M2MQTaskResult.OK != connectClientWithConnOptions(this.client)) {
            System.out.println("Error starting connections to mqtt clients. Aborting execution");
            return M2MQTaskResult.ERROR_MQTT_CONNECTION;
        }

        setMqttCallback(this.client);
        return M2MQTaskResult.OK;
    }

    public void addDocumentToTempQueue(Document document) {
        boolean msgAddedToQueue = this.tempMessageQueue.offer(document);
        System.out.println("Message added to the queue with result = "+msgAddedToQueue);
    }

    public void addDocumentToMoveQueue(Document document) {
        boolean msgAddedToQueue = this.moveMessageQueue.offer(document);
        System.out.println("Message added to the queue with result = "+msgAddedToQueue);
    }

    public void startPostingTempMessagesToMqttTopic() {
        System.out.println("Starting post message scheduler");
        this.schedulerTemperature.scheduleAtFixedRate( () -> {
            Document item = tempMessageQueue.poll();
            if( null != item) {
                try {
                    client.publish(
                            M2MQPropertyHandler.getInstance().getPropertyValue(MQTT_TOPIC_SENDER_TEMP_G16),
                            convertDocumentToMqttMessage(item)
                    );
                } catch (MqttException e) {
                    System.out.println("Exception occurred: "+e.getCause());
                }
            }
        }, 0, 750, TimeUnit.MILLISECONDS);
    }

    public void startPostingMoveMessagesToMqttTopic() {
        System.out.println("Starting post message scheduler");
        this.schedulerMovements.scheduleAtFixedRate( () -> {
            Document item = moveMessageQueue.poll();
            if( null != item) {
                try {
                    client.publish(
                            M2MQPropertyHandler.getInstance().getPropertyValue(MQTT_TOPIC_SENDER_LAB_G16),
                            convertDocumentToMqttMessage(item)
                    );
                } catch (MqttException e) {
                    System.out.println("Exception occurred: "+e.getCause());
                }
            }
        }, 0, 750, TimeUnit.MILLISECONDS);
    }

    private MqttMessage convertDocumentToMqttMessage(Document document) {
        MqttMessage mqtt_message = new MqttMessage();

        mqtt_message.setPayload(document.toJson().getBytes());
        mqtt_message.setQos(1);

        return mqtt_message;
    }

    private void setMqttCallback(MqttClient client) {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("JOAO CARALHO");
                System.out.println("Connection lost: " + throwable.getMessage());
                System.out.println("FODASSE = "+throwable.getCause());
                System.out.println("Automatic Reconnect is true. Reconnecting.");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception { }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { }
        });
    }

    private M2MQTaskResult connectClientWithConnOptions(MqttClient client) {
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);

            client.connect(connOpts);
            return M2MQTaskResult.OK;
        } catch (MqttException e) {
            System.out.println("Exception caught: "+e.getMessage());
            return M2MQTaskResult.ERROR_MQTT_CONNECTION;
        }
    }

    private M2MQTaskResult createMqttClients() {
        this.client = getMqttClient();

        if( null == this.client) {
            System.out.println("Error creating MQTT client.");
            return M2MQTaskResult.ERROR_CREATING_MQTT_CLIENT;
        }
        return M2MQTaskResult.OK;
    }

    private MqttClient getMqttClient() {
        try {
            return new MqttClient(getBroker(), getClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            System.out.println("Exception caught: "+e.getMessage());
            return null;
        }
    }

    private String getBroker() {
        return M2MQPropertyHandler.getInstance().getPropertyValue(M2MQProperty.MQTT_CLOUD_SENDER);
    }

    private String getClientId() {
        return "clientId_"+ M2MQPropertyHandler.getInstance().getPropertyValue(M2MQProperty.RUNNER);
    }
}
