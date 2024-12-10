package org.example.code.handlers;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.code.common.MQ2MYTaskCompleteListener;
import org.example.code.common.MQ2MYTaskResult;

import static org.example.code.common.MQ2MYProperty.*;

public class MQ2MYMqttConnectionHandlerThreadRunner extends Thread {

    private MqttClient client = null;
    private final MQ2MYTaskCompleteListener listener;
    private final MQ2MYMessageDataHandler messageDataHandler;
    private final Boolean isLab;

    MQ2MYMqttConnectionHandlerThreadRunner(MQ2MYTaskCompleteListener listener, Boolean isLab) {
        System.out.println("Created an instance of MQ2MYMqttConnectionHandlerThreadRunner. Ready to use.");
        this.listener = listener;
        this.isLab = isLab;
        this.messageDataHandler = new MQ2MYMessageDataHandler();
    }

    @Override
    public void run() {
        MQ2MYTaskResult result = startRunner();
        listener.onTaskComplete(result);
    }

    private MQ2MYTaskResult startRunner() {

        if( MQ2MYTaskResult.OK != createMqttClients() ) {
            System.out.println("Error starting MqttHandler. Aborting execution");
            return MQ2MYTaskResult.ERROR_MQTT_START_UP;
        }

        if( MQ2MYTaskResult.OK != connectClientWithConnOptions()) {
            System.out.println("Error starting connections to mqtt clients. Aborting execution");
            return MQ2MYTaskResult.ERROR_MQTT_CONNECTION;
        }

        setMqttCallback();
        if(MQ2MYTaskResult.OK != subscribe()) {
            System.out.println("Error subscribing to topic. Aborting execution");
            return MQ2MYTaskResult.ERROR_MQTT_SUBSCRIPTION;
        }

        return MQ2MYTaskResult.OK;
    }

    private MQ2MYTaskResult createMqttClients() {
        this.client = getMqttClient();

        if( null == this.client) {
            System.out.println("Error creating MQTT client.");
            return MQ2MYTaskResult.ERROR_CREATING_MQTT_CLIENT;
        }
        return MQ2MYTaskResult.OK;
    }

    private MqttClient getMqttClient() {
        try {
            return new MqttClient(getBroker(), getClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            System.out.println("Exception caught: "+e.getMessage());
            return null;
        }
    }

    private MQ2MYTaskResult connectClientWithConnOptions() {
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);

            this.client.connect(connOpts);
            return MQ2MYTaskResult.OK;
        } catch (MqttException e) {
            System.out.println("Exception caught: "+e.getMessage());
            return MQ2MYTaskResult.ERROR_MQTT_CONNECTION;
        }
    }

    private void setMqttCallback() {
        this.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Connection lost: " + throwable.getMessage());
                System.out.println("CARALHO PA ---- "+throwable.getCause());
                System.out.println("Automatic Reconnect is true. Reconnecting.");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                System.out.println("message received: topic: "+s+", message: "+mqttMessage.toString()+".");
                messageDataHandler.handleMessage(s, mqttMessage);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { }
        });
    }

    private MQ2MYTaskResult subscribe() {
        try {
            this.client.subscribe(getTopicReceiver());
            System.out.println("Subscribed to client "+((this.isLab) ? "Lab" : "Temp"));

            return MQ2MYTaskResult.OK;
        } catch (MqttException e) {
            System.out.println("Exception caught: "+e.getMessage());
            return MQ2MYTaskResult.ERROR_MQTT_SUBSCRIPTION;
        }
    }

    private String getBroker() {
        return MQ2MYPropertyHandler.getInstance().getPropertyValue(MQTT_CLOUD_RECEIVER);
    }

    private String getClientId() {
        return "clientId_"+ MQ2MYPropertyHandler.getInstance().getPropertyValue(RUNNER)+getClientType();
    }

    private String getTopicReceiver() {
        return MQ2MYPropertyHandler.getInstance().getPropertyValue( (!this.isLab) ? MQTT_TOPIC_RECEIVER_TEMP_G16 : MQTT_TOPIC_RECEIVER_LAB_G16 );
    }

    public String getClientType() {
        return ((this.isLab) ? "Lab" : "Temp");
    }
}
