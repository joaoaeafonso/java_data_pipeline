package org.example.code.handlers;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.code.common.C2MProperty;
import org.example.code.common.C2MTaskCompleteListener;
import org.example.code.common.C2MTaskResult;

import static org.example.code.common.C2MProperty.MQTT_TOPIC_RECEIVER_LAB_G16;
import static org.example.code.common.C2MProperty.MQTT_TOPIC_RECEIVER_TEMP_G16;

public class C2MMqttConnectionHandlerThreadRunner extends Thread {

    private MqttClient client = null;
    private final C2MTaskCompleteListener listener;
    private final C2MMessageDataHandler c2MMessageDataHandler;
    private final Boolean isLab;

    C2MMqttConnectionHandlerThreadRunner(C2MTaskCompleteListener listener, Boolean isLab) {
        System.out.println("Created an instance of C2MMqttConnectionHandlerThreadRunner. Ready to use.");
        this.listener = listener;
        this.isLab = isLab;
        this.c2MMessageDataHandler = new C2MMessageDataHandler(isLab);
    }

    @Override
    public void run() {
        C2MTaskResult result = startRunner();
        listener.onTaskComplete(result);
    }

    private C2MTaskResult startRunner() {
        if( C2MTaskResult.OK != createMqttClients() ) {
            System.out.println("Error starting MqttHandler. Aborting execution");
            return C2MTaskResult.ERROR_MQTT_START_UP;
        }

        if( C2MTaskResult.OK != connectClientWithConnOptions(this.client)) {
            System.out.println("Error starting connections to mqtt clients. Aborting execution");
            return C2MTaskResult.ERROR_MQTT_CONNECTION;
        }

        setMqttCallback(this.client);
        if(C2MTaskResult.OK != subscribe(this.client)) {
            System.out.println("Error subscribing to topic. Aborting execution");
            return C2MTaskResult.ERROR_MQTT_SUBSCRIPTION;
        }
        return C2MTaskResult.OK;
    }

    private C2MTaskResult createMqttClients() {
        this.client = getMqttClient();

        if( null == this.client) {
            System.out.println("Error creating MQTT client.");
            return C2MTaskResult.ERROR_CREATING_MQTT_CLIENT;
        }
        return C2MTaskResult.OK;
    }

    private C2MTaskResult subscribe(MqttClient client) {
        try {
            client.subscribe(getTopicReceiver(this.isLab));
            System.out.println("Subscribed to client "+((this.isLab) ? "Lab" : "Temp"));
            return C2MTaskResult.OK;
        } catch (MqttException e) {
            System.out.println("Exception caught: "+e.getMessage());
            return C2MTaskResult.ERROR_MQTT_SUBSCRIPTION;
        }
    }

    private C2MTaskResult connectClientWithConnOptions(MqttClient client) {
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);

            client.connect(connOpts);
            return C2MTaskResult.OK;
        } catch (MqttException e) {
            System.out.println("Exception caught: "+e.getMessage());
            return C2MTaskResult.ERROR_MQTT_CONNECTION;
        }
    }

    private void setMqttCallback(MqttClient client) {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Connection lost: " + throwable.getMessage());
                System.out.println("Automatic Reconnect is true. Reconnecting.");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                System.out.println("message received: topic: "+s+", message: "+mqttMessage.toString()+".");
                c2MMessageDataHandler.handleMessage(s, mqttMessage);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { }
        });
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
        return C2MPropertyHandler.getInstance().getPropertyValue(C2MProperty.MQTT_CLOUD_RECEIVER);
    }

    private String getClientId() {
        return "clientId_"+ C2MPropertyHandler.getInstance().getPropertyValue(C2MProperty.RUNNER)+getClientType();
    }

    private String getTopicReceiver(Boolean isLab) {
        return C2MPropertyHandler.getInstance().getPropertyValue( (!isLab) ? MQTT_TOPIC_RECEIVER_TEMP_G16 : MQTT_TOPIC_RECEIVER_LAB_G16 );
    }

    public String getClientType() {
        return ((this.isLab) ? "Lab" : "Temp");
    }
}
