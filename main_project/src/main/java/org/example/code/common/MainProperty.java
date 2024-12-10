package org.example.code.common;

public enum MainProperty {
    RUNNER;

    public String getValue(){
        return this.name().toLowerCase();
    }
}