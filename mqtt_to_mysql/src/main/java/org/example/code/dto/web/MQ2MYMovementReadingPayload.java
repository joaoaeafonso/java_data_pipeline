package org.example.code.dto.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MQ2MYMovementReadingPayload {

    @JsonProperty("idMedicao")
    int idMedicao;

    @JsonProperty("idMongo")
    int idMongo;

    @JsonProperty("experienceID")
    int experienceID;

    @JsonProperty("timestamp")
    String timestamp;

    @JsonProperty("salaOrigem")
    int salaOrigem;

    @JsonProperty("salaDestino")
    int salaDestino;

    public MQ2MYMovementReadingPayload(int idMongo, int idMedicao, int experienceID, String timestamp, int salaOrigem, int salaDestino) {
        this.idMedicao = idMedicao;
        this.idMongo = idMongo;
        this.experienceID = experienceID;
        this.timestamp = timestamp;
        this.salaOrigem = salaOrigem;
        this.salaDestino = salaDestino;
    }
}
