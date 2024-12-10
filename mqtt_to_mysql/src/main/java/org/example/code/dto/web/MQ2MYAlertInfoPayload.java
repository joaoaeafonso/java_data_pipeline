package org.example.code.dto.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MQ2MYAlertInfoPayload {

    @JsonProperty("alertId")
    int alertId;

    @JsonProperty("experienceId")
    int experienceId;

    @JsonProperty("timestamp")
    String timestamp;

    @JsonProperty("roomId")
    int roomId;

    @JsonProperty("sensorId")
    int sensorId;

    @JsonProperty("tempReading")
    double tempReading;

    @JsonProperty("priority")
    String priority;

    @JsonProperty("alertType")
    int alertType;

    @JsonProperty("readingType")
    String readingType;

    public MQ2MYAlertInfoPayload(
            int alertId,
            int experienceId,
            String timestamp,
            int roomId,
            int sensorId,
            double tempReading,
            String priority,
            int alertType,
            String readingType)
    {
        this.alertId = alertId;
        this.experienceId = experienceId;
        this.timestamp = timestamp;
        this.roomId = roomId;
        this.sensorId = sensorId;
        this.tempReading = tempReading;
        this.priority = priority;
        this.alertType = alertType;
        this.readingType = readingType;
    }
}
