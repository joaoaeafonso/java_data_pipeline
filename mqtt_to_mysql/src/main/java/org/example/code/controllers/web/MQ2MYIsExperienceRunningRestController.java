package org.example.code.controllers.web;

import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MQ2MYIsExperienceRunningRestController {

    public MQ2MYIsExperienceRunningRestController() {
        System.out.println("Created an instance of MQ2MYIsExperienceRunningRestController. Ready to use.");
    }

    @PostMapping("/api/is_experience_running")
    public boolean receiveData() {
        System.out.println("Checking if any experience is running.");
        return MQ2MYMySQLDBHandler.getInstance().isExperienceRunning();
    }
}
