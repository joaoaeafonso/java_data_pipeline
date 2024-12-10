package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYAlertInfoPayload;
import org.example.code.dto.web.MQ2MYAlertInfoRequestPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
public class MQ2MYAlertInfoRestController {

    public MQ2MYAlertInfoRestController() {
        System.out.println("Created an instance of MQ2MYAlertInfoRestController. Ready to use.");
    }

    @PostMapping("/api/alert_information")
    public List<MQ2MYAlertInfoPayload> receiveData(@RequestBody MQ2MYAlertInfoRequestPayload payload) {
        int experienceID = payload.getExperienceID();
        String priority = payload.getPriority();
        String readingType = payload.getReadingType();

        return MQ2MYMySQLDBHandler.getInstance().getAllAlertsInformation(experienceID, priority, readingType);
    }
}
