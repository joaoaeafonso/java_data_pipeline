package org.example.code.dto.web;

public class MQ2MYExperienceParametersDataPayload {

    private double temperatureDelta;
    private double temperatureMax;
    private double temperatureMin;
    private double idealTemperature;
    private int maxNumberOfRatsPerRoom;
    private int totalNumberOfRats;
    private int numberOfSecondsWithoutMovement;
    private int maxNumberOfAllowedErrors;
    private int maxNumberOfAllowedTemperatureErrors;
    private int maxNumberOfAllowedMovementErrors;
    private String description;
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMaxNumberOfAllowedMovementErrors(int maxNumberOfAllowedMovementErrors) {
        this.maxNumberOfAllowedMovementErrors = maxNumberOfAllowedMovementErrors;
    }

    public void setMaxNumberOfAllowedTemperatureErrors(int maxNumberOfAllowedTemperatureErrors) {
        this.maxNumberOfAllowedTemperatureErrors = maxNumberOfAllowedTemperatureErrors;
    }

    public void setMaxNumberOfAllowedErrors(int maxNumberOfAllowedErrors) {
        this.maxNumberOfAllowedErrors = maxNumberOfAllowedErrors;
    }

    public void setTotalNumberOfRats(int totalNumberOfRats) {
        this.totalNumberOfRats = totalNumberOfRats;
    }

    public void setMaxNumberOfRatsPerRoom(int maxNumberOfRatsPerRoom) {
        this.maxNumberOfRatsPerRoom = maxNumberOfRatsPerRoom;
    }

    public double getIdealTemperature() {
        return idealTemperature;
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

    public int getMaxNumberOfAllowedErrors() {
        return maxNumberOfAllowedErrors;
    }

    public int getMaxNumberOfAllowedMovementErrors() {
        return maxNumberOfAllowedMovementErrors;
    }

    public int getMaxNumberOfAllowedTemperatureErrors() {
        return maxNumberOfAllowedTemperatureErrors;
    }

    public int getMaxNumberOfRatsPerRoom() {
        return maxNumberOfRatsPerRoom;
    }

    public int getNumberOfSecondsWithoutMovement() {
        return numberOfSecondsWithoutMovement;
    }

    public int getTotalNumberOfRats() {
        return totalNumberOfRats;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdealTemperature(double idealTemperature) {
        this.idealTemperature = idealTemperature;
    }

    public void setNumberOfSecondsWithoutMovement(int numberOfSecondsWithoutMovement) {
        this.numberOfSecondsWithoutMovement = numberOfSecondsWithoutMovement;
    }

    public void setTemperatureDelta(double temperatureDelta) {
        this.temperatureDelta = temperatureDelta;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }
}
