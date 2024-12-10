package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYRegisterUserPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MQ2MYRegisterUserRestController {

    public MQ2MYRegisterUserRestController() {
        System.out.println("Created an instance of MQ2MYRegisterUserRestController. Ready to use.");
    }

    @PostMapping("/api/register_user")
    public boolean receiveData(@RequestBody MQ2MYRegisterUserPayload payload) {
        System.out.println("Trying to register User.");

        return MQ2MYMySQLDBHandler.getInstance().registerUser(
                payload.getUsername(),
                payload.getName(),
                payload.getTelefone(),
                payload.getPassword(),
                payload.getUserType()
        );
    }
}
