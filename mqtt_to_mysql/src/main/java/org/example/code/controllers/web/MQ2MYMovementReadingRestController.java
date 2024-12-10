package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYMovementReadingPayload;
import org.example.code.dto.web.MQ2MYMovementReadingRequestPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class MQ2MYMovementReadingRestController {

    public MQ2MYMovementReadingRestController() {
        System.out.println("Created an instance of MQ2MYMovementReadingRestController. Ready to use.");
    }

    @PostMapping("/api/movement")
    public List<MQ2MYMovementReadingPayload> receiveData(@RequestBody MQ2MYMovementReadingRequestPayload payload) {
        System.out.println("Getting all available movement readings.");

        int experienceID = payload.getExperienceID();
        return MQ2MYMySQLDBHandler.getInstance().getAllMovementReadingForExperience(experienceID);
    }
}
