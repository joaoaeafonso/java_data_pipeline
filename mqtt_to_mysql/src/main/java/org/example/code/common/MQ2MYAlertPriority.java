package org.example.code.common;

public enum MQ2MYAlertPriority {
    UNKNOWN,
    Baixo,
    Medio,
    Alto,
    Critico;

    public String getValue() {
        return this.name();
    }
}
