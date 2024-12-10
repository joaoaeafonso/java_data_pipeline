package org.example.code.common;

public enum C2MTaskResult {
    OK,
    ERROR_LOADING_PROPERTIES,
    ERROR_CREATING_MQTT_CLIENT,
    ERROR_MQTT_START_UP,
    ERROR_MQTT_CONNECTION,
    ERROR_MQTT_SUBSCRIPTION,
    ERROR_MQTT_THREAD_STARTER,
    ERROR_MISSING_VALUES_IN_TEMP_MESSAGE,
    ERROR_TEMP_WRONG_FORMAT,
    ERROR_TEMP_WRONG_VALUE_TYPE,
    ERROR_TIMESTAMP_WRONG_FORMAT,
    ERROR_TIMESTAMP_WRONG_VALUE_TYPE,
    ERROR_SENSOR_WRONG_FORMAT,
    ERROR_SENSOR_WRONG_VALUE_TYPE,
    ERROR_MISSING_VALUES_IN_LAB_MESSAGE,
    ERROR_ORIGIN_ROOM_WRONG_FORMAT,
    ERROR_ORIGIN_ROOM_WRONG_VALUE_TYPE,
    ERROR_DESTINATION_ROOM_WRONG_FORMAT,
    ERROR_DESTINATION_ROOM_WRONG_VALUE_TYPE;
}