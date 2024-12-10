package org.example.code.controllers.mobile;

import org.example.code.dto.mobile.MQ2MYAndroidTemperatureSensorXPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class MQ2MYAndroidTemperatureReadingsSensor2RestController {

    public MQ2MYAndroidTemperatureReadingsSensor2RestController() {
        System.out.println("Created an instance of MQ2MYAndroidTemperatureReadingsSensor2RestController. Ready to use.");
    }

    @PostMapping("/api/temp_sensor2_android")
    public List<MQ2MYAndroidTemperatureSensorXPayload> receiveData() {
        System.out.println("Preparing all Temperature readings in Sensor 2 for Mobile Request.");
        return MQ2MYMySQLDBHandler.getInstance().getAllTemperatureReadingsSensor1(2);
    }

}