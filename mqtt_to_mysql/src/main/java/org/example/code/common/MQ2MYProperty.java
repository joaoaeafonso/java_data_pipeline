package org.example.code.common;

public enum MQ2MYProperty {
    MQTT_CLOUD_RECEIVER,
    MQTT_TOPIC_RECEIVER_TEMP_G16,
    MQTT_TOPIC_RECEIVER_LAB_G16,
    MYSQL_CLASS_DRIVER,
    MYSQL_CLOUD_DB_HOST,
    MYSQL_CLOUD_DB_PORT,
    MYSQL_CLOUD_DB_USER,
    MYSQL_CLOUD_DB_PASSWORD,
    MYSQL_CLOUD_DATABASE,
    MYSQL_DB_HOST,
    MYSQL_DB_PORT,
    MYSQL_DB_USER,
    MYSQL_DB_PASSWORD,
    MYSQL_DATABASE,
    RUNNER;

    public String getValue(){
        return this.name().toLowerCase();
    }
}