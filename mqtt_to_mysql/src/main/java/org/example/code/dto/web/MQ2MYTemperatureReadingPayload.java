package org.example.code.dto.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MQ2MYTemperatureReadingPayload {

    @JsonProperty("idReading")
    int idReading;

    @JsonProperty("idMongo")
    int idMongo;

    @JsonProperty("idExperiencia")
    int idExperiencia;

    @JsonProperty("timestamp")
    String timestamp;

    @JsonProperty("reading")
    double reading;

    @JsonProperty("sensorId")
    int sensorId;

    public MQ2MYTemperatureReadingPayload(int idReading, int idMongo, int idExperiencia, String timestamp, double reading, int sensorId) {
        this.idReading = idReading;
        this.idMongo = idMongo;
        this.idExperiencia = idExperiencia;
        this.timestamp = timestamp;
        this.reading = reading;
        this.sensorId = sensorId;
    }
}
