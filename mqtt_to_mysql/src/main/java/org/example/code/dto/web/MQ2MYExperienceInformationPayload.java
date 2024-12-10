package org.example.code.dto.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MQ2MYExperienceInformationPayload {

    @JsonProperty("experienceID")
    int experienceID;

    @JsonProperty("description")
    String description;

    @JsonProperty("user")
    String user;

    @JsonProperty("numRats")
    int numRats;

    @JsonProperty("numRatsPerRoom")
    int numRatsPerRoom;

    @JsonProperty("secsWithNoMove")
    int secsWithNoMove;

    @JsonProperty("idealTemp")
    double idealTemp;

    @JsonProperty("maxTemp")
    double maxTemp;

    @JsonProperty("minTemp")
    double minTemp;

    @JsonProperty("deltaTemp")
    double deltaTemp;

    @JsonProperty("createTime")
    String createTime;

    @JsonProperty("startTime")
    String startTime;

    @JsonProperty("endTime")
    String endTime;

    @JsonProperty("state")
    String state;

    @JsonProperty("maxTempErrors")
    int maxTempErrors;

    @JsonProperty("maxMoveErrors")
    int maxMoveErrors;

    @JsonProperty("totalErrors")
    int totalErrors;

    public MQ2MYExperienceInformationPayload(
            int id,
            String desc,
            String user,
            int nr,
            int nrpr,
            int swnm,
            double it,
            double mt,
            double mint,
            double delta,
            String createTime,
            int mte,
            int mme,
            int te,
            String state)
    {
        this.experienceID = id;
        this.description = desc;
        this.user = user;
        this.numRats = nr;
        this.numRatsPerRoom = nrpr;
        this.secsWithNoMove = swnm;
        this.idealTemp = it;
        this.maxTemp = mt;
        this.minTemp = mint;
        this.deltaTemp = delta;
        this.createTime = createTime;
        this.maxMoveErrors = mme;
        this.maxTempErrors = mte;
        this.totalErrors = te;
        this.startTime = "";
        this.endTime = "";
        this.state = state;
    }

}
