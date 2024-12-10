package org.example.code.dto.mobile;

import java.time.LocalDateTime;

public class MQ2MYAndroidTemperatureSensorXPayload {

    private LocalDateTime dataHora;
    private double leitura;

    public double getLeitura() {
        return leitura;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public void setLeitura(double leitura) {
        this.leitura = leitura;
    }

}
