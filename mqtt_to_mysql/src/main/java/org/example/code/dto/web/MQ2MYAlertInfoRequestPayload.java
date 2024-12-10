package org.example.code.dto.web;

public class MQ2MYAlertInfoRequestPayload {

    int experienceID;

    String priority;

    String readingType;

    public void setExperienceID(int experienceID) {
        this.experienceID = experienceID;
    }

    public int getExperienceID() {
        return experienceID;
    }

    public String getPriority() {
        return priority;
    }

    public String getReadingType() {
        return readingType;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setReadingType(String readingType) {
        this.readingType = readingType;
    }
}
