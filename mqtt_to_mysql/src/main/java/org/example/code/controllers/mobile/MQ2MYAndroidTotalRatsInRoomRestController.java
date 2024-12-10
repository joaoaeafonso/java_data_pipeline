package org.example.code.controllers.mobile;

import org.example.code.dto.mobile.MQ2MYAndroidRatsInRoomPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
public class MQ2MYAndroidTotalRatsInRoomRestController {

    public MQ2MYAndroidTotalRatsInRoomRestController() {
        System.out.println("Created an instance of MQ2MYAndroidTotalRatsInRoomRestController. Ready to use.");
    }

    @PostMapping("/api/total_rats_android")
    public List<MQ2MYAndroidRatsInRoomPayload> receiveData() {
        System.out.println("Preparing all rats in rooms for Mobile Request.");
        return MQ2MYMySQLDBHandler.getInstance().getAllRatsInRooms();
    }
}
