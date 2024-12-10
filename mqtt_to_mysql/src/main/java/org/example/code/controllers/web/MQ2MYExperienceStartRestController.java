package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYExperienceStartDataPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.example.code.managers.MQ2MYExperienceManager;
import org.springframework.web.bind.annotation.*;


@RestController
public class MQ2MYExperienceStartRestController {

    public MQ2MYExperienceStartRestController() {
        System.out.println("Created an instance of MQ2MYExperienceStartRestController. Ready to use.");
    }

    @PostMapping("/api/send_start")
    public boolean receiveData(@RequestBody MQ2MYExperienceStartDataPayload payload) {
        int experienceId = payload.getExperienceId();
        double temperatureDelta = payload.getTemperatureDelta();
        double temperatureMax = payload.getTemperatureMax();
        double temperatureMin = payload.getTemperatureMin();
        int totalRats =  payload.getTotalNumberOfRats();
        int totalRatsPerRoom = payload.getMaxNumberOfRatsPerRoom();
        int totalErrors = payload.getMaxNumberOfAllowedErrors();
        int totalErrorsTemp = payload.getMaxNumberOfAllowedTemperatureErrors();
        int totalErrorsMove = payload.getMaxNumberOfAllowedMovementErrors();
        int numOfSecsNoRatMovement = payload.getMaxNumberOfSecondsNoMovement();

        System.out.println("Starting Experience with ID = "+experienceId);

        MQ2MYExperienceManager.getInstance().setExperienceId(experienceId);
        MQ2MYExperienceManager.getInstance().toggleExperienceRunning(true);
        MQ2MYExperienceManager.getInstance().setTemperatureValuesForExperience(temperatureDelta, temperatureMax, temperatureMin);
        MQ2MYExperienceManager.getInstance().setRatNumbersForExperience(totalRats, totalRatsPerRoom, numOfSecsNoRatMovement);
        MQ2MYExperienceManager.getInstance().setAllMaxErrorValues(totalErrors, totalErrorsTemp, totalErrorsMove);

        MQ2MYMySQLDBHandler.getInstance().startExperience(experienceId);

        return true;
    }

}
