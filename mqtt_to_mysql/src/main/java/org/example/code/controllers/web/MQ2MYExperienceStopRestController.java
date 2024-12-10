package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYExperienceStopDataPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.example.code.managers.MQ2MYExperienceManager;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
public class MQ2MYExperienceStopRestController {

    public MQ2MYExperienceStopRestController() {
        System.out.println("Created an instance of MQ2MYExperienceStopRestController. Ready to use.");
    }

    @PostMapping("/api/send_stop")
    public boolean receiveData(@RequestBody MQ2MYExperienceStopDataPayload payload) {
        int experienceId = payload.getExperienceId();

        System.out.println("Stopping experience with number = "+experienceId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ExperienceID", experienceId);
        jsonObject.put("ExperienceStatus", "Terminada");

        MQ2MYMySQLDBHandler.getInstance().updateFinalNumberOfRatsPerRoom(jsonObject);
        MQ2MYMySQLDBHandler.getInstance().stopExperience(experienceId);

        MQ2MYExperienceManager.getInstance().toggleExperienceRunning(false);
        MQ2MYExperienceManager.getInstance().clearAllExperienceData();

        return true;
    }

}
