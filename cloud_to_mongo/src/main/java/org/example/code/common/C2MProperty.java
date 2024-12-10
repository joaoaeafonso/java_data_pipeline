package org.example.code.common;

public enum C2MProperty {
    MQTT_CLOUD_RECEIVER,
    MQTT_TOPIC_RECEIVER_TEMP_G16,
    MQTT_TOPIC_RECEIVER_LAB_G16,
    MONGODB_ADDRESS,
    MONGODB_USER,
    MONGODB_USER_PASSWORD,
    MONGODB_AUTHENTICATION_ENABLED,
    MONGODB_REPLICA,
    MONGODB_DATABASE,
    MONGODB_COLLECTION_TEMP,
    MONGODB_COLLECTION_LAB,
    RUNNER;

    public String getValue(){
        return this.name().toLowerCase();
    }
}