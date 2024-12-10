package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYTemperatureReadingPayload;
import org.example.code.dto.web.MQ2MYTemperatureReadingRequestPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class MQ2MYTemperatureReadingRestController {

    public MQ2MYTemperatureReadingRestController() {
        System.out.println("Created an instance of MQ2MYTemperatureReadingRestController. Ready to use.");
    }

    @PostMapping("/api/temperature")
    public List<MQ2MYTemperatureReadingPayload> receiveData(@RequestBody MQ2MYTemperatureReadingRequestPayload payload) {
        System.out.println("Getting all available temperature readings.");

        int experienceID = payload.getExperienceID();
        return MQ2MYMySQLDBHandler.getInstance().getAllTemperatureReadingForExperience(experienceID);
    }
}
