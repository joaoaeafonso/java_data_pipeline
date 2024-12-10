package org.example.code.controllers.mobile;

import org.example.code.dto.mobile.MQ2MYAndroidAlertPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
public class MQ2MYAndroid60MinAlertsRestController {

    public MQ2MYAndroid60MinAlertsRestController() {
        System.out.println("Created an instance of MQ2MYAndroid60MinAlertsRestController. Ready to use.");
    }

    @PostMapping("/api/alert_android")
    public List<MQ2MYAndroidAlertPayload> receiveData() {
        System.out.println("Preparing all alerts for Mobile Request.");
        return MQ2MYMySQLDBHandler.getInstance().getAllMobileRequestAlerts();
    }

}
