package org.example.code.dto.mobile;

import java.time.LocalDateTime;

public class MQ2MYAndroidAlertPayload {
    private String Descricao;
    private double TemperaturaMedida;
    private String SalaOrigem;
    private String SalaDestino;
    private String Sensor;
    private String TipoAlerta;
    private LocalDateTime Hora;

    // Getters and setters for all fields
    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public double getTemperaturaMedida() {
        return TemperaturaMedida;
    }

    public void setTemperaturaMedida(double temperaturaMedida) {
        TemperaturaMedida = temperaturaMedida;
    }

    public String getSalaOrigem() {
        return SalaOrigem;
    }

    public void setSalaOrigem(String salaOrigem) {
        SalaOrigem = salaOrigem;
    }

    public String getSalaDestino() {
        return SalaDestino;
    }

    public void setSalaDestino(String salaDestino) {
        SalaDestino = salaDestino;
    }

    public String getSensor() {
        return Sensor;
    }

    public void setSensor(String sensor) {
        Sensor = sensor;
    }

    public String getTipoAlerta() {
        return TipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        TipoAlerta = tipoAlerta;
    }

    public LocalDateTime getHora() {
        return Hora;
    }

    public void setHora(LocalDateTime hora) {
        Hora = hora;
    }

    @Override
    public String toString() {
        return "Alerta{" +
                "Descricao='" + Descricao + '\'' +
                ", TemperaturaMedida=" + TemperaturaMedida +
                ", SalaOrigem='" + SalaOrigem + '\'' +
                ", SalaDestino='" + SalaDestino + '\'' +
                ", Sensor='" + Sensor + '\'' +
                ", TipoAlerta='" + TipoAlerta + '\'' +
                ", Hora=" + Hora +
                '}';
    }
}