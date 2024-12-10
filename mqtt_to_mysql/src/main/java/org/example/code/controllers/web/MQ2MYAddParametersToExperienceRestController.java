package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYExperienceParametersDataPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MQ2MYAddParametersToExperienceRestController {

    public MQ2MYAddParametersToExperienceRestController() {
        System.out.println("Created an instance of MQ2MYAddParametersToExperienceRestController. Ready to use.");
    }

    @PostMapping("/api/add_parameters")
    public void receiveData(@RequestBody MQ2MYExperienceParametersDataPayload payload) {
        System.out.println("Creating Experience.");

        String description = payload.getDescription();
        String user = payload.getUser();

        int numRats = payload.getTotalNumberOfRats();
        int ratsPerRoom = payload.getMaxNumberOfRatsPerRoom();
        int secsWithoutMove = payload.getNumberOfSecondsWithoutMovement();

        double idealTemp = payload.getIdealTemperature();
        double maxTemp = payload.getTemperatureMax();
        double minTemp = payload.getTemperatureMin();
        double delta = payload.getTemperatureDelta();

        String experienceState = "Por iniciar";

        int maxTempErrors = payload.getMaxNumberOfAllowedTemperatureErrors();
        int maxMoveErrors = payload.getMaxNumberOfAllowedMovementErrors();
        int maxErrors = payload.getMaxNumberOfAllowedErrors();

        MQ2MYMySQLDBHandler.getInstance().createExperience(
                description,
                user,
                numRats,
                ratsPerRoom,
                secsWithoutMove,
                idealTemp,
                maxTemp,
                minTemp,
                delta,
                experienceState,
                maxTempErrors,
                maxMoveErrors,
                maxErrors
        );
    }

}
