package org.example.code.dto.web;


public class MQ2MYExperienceStartDataPayload {
    private int experienceId;
    private double temperatureDelta;
    private double temperatureMax;
    private double temperatureMin;
    private int maxNumberOfRatsPerRoom;
    private int totalNumberOfRats;
    private int maxNumberOfSecondsNoMovement;
    private int maxNumberOfAllowedErrors;
    private int maxNumberOfAllowedTemperatureErrors;
    private int maxNumberOfAllowedMovementErrors;

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public void setTemperatureDelta(double temperatureDelta) {
        this.temperatureDelta = temperatureDelta;
    }

    public int getMaxNumberOfSecondsNoMovement() {
        return maxNumberOfSecondsNoMovement;
    }

    public void setMaxNumberOfSecondsNoMovement(int maxNumberOfSecondsNoMovement) {
        this.maxNumberOfSecondsNoMovement = maxNumberOfSecondsNoMovement;
    }

    public int getMaxNumberOfAllowedErrors() {
        return maxNumberOfAllowedErrors;
    }

    public void setMaxNumberOfAllowedErrors(int maxNumberOfAllowedErrors) {
        this.maxNumberOfAllowedErrors = maxNumberOfAllowedErrors;
    }

    public int getMaxNumberOfAllowedTemperatureErrors() {
        return maxNumberOfAllowedTemperatureErrors;
    }

    public void setMaxNumberOfAllowedTemperatureErrors(int maxNumberOfAllowedTemperatureErrors) {
        this.maxNumberOfAllowedTemperatureErrors = maxNumberOfAllowedTemperatureErrors;
    }

    public int getMaxNumberOfAllowedMovementErrors() {
        return maxNumberOfAllowedMovementErrors;
    }

    public void setMaxNumberOfAllowedMovementErrors(int maxNumberOfAllowedMovementErrors) {
        this.maxNumberOfAllowedMovementErrors = maxNumberOfAllowedMovementErrors;
    }

    public int getMaxNumberOfRatsPerRoom() {
        return maxNumberOfRatsPerRoom;
    }

    public void setMaxNumberOfRatsPerRoom(int maxNumberOfRatsPerRoom) {
        this.maxNumberOfRatsPerRoom = maxNumberOfRatsPerRoom;
    }

    public int getTotalNumberOfRats() {
        return totalNumberOfRats;
    }

    public void setTotalNumberOfRats(int totalNumberOfRats) {
        this.totalNumberOfRats = totalNumberOfRats;
    }

    public int getExperienceId() {
        return experienceId;
    }

    public double getTemperatureDelta() {
        return temperatureDelta;
    }


    public double getTemperatureMax() {
        return temperatureMax;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setExperienceId(int experienceId) {
        this.experienceId = experienceId;
    }
}
