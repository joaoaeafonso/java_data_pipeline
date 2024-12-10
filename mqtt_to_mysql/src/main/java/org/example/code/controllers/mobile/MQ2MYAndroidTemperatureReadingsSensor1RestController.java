package org.example.code.controllers.mobile;

import org.example.code.dto.mobile.MQ2MYAndroidTemperatureSensorXPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class MQ2MYAndroidTemperatureReadingsSensor1RestController {

    public MQ2MYAndroidTemperatureReadingsSensor1RestController() {
        System.out.println("Created an instance of MQ2MYAndroidTemperatureReadingsSensor1RestController. Ready to use.");
    }

    @PostMapping("/api/temp_sensor1_android")
    public List<MQ2MYAndroidTemperatureSensorXPayload> receiveData() {
        System.out.println("Preparing all Temperature readings in Sensor 1 for Mobile Request.");
        return MQ2MYMySQLDBHandler.getInstance().getAllTemperatureReadingsSensor1(1);
    }

}
