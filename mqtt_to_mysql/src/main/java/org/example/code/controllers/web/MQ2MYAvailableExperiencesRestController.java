package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYExperienceInformationPayload;
import org.example.code.dto.web.MQ2MYExperienceInformationRequestPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
public class MQ2MYAvailableExperiencesRestController {

    public MQ2MYAvailableExperiencesRestController() {
        System.out.println("Created an instance of MQ2MYAvailableExperiencesRestController. Ready to use.");
    }

    @PostMapping("/api/available_experiences")
    public List<MQ2MYExperienceInformationPayload> receiveData(@RequestBody MQ2MYExperienceInformationRequestPayload payload) {
        System.out.println("Getting all available experiences to Start.");
        return MQ2MYMySQLDBHandler.getInstance().getAllExperiencesAvailableToStart(payload.getUser());
    }

}
