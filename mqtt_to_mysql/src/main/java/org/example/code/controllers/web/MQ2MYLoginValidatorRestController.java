package org.example.code.controllers.web;

import org.example.code.dto.web.MQ2MYLoginDataPayload;
import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MQ2MYLoginValidatorRestController {

    public MQ2MYLoginValidatorRestController() {
        System.out.println("Created an instance of MQ2MYLoginValidatorRestController. Ready to use.");
    }

    @PostMapping("/api/login_validate")
    public boolean receiveData(@RequestBody MQ2MYLoginDataPayload payload) {

        String username = payload.getUsername();
        String password = payload.getPassword();

        System.out.println("Going to try to validate login for user "+username);

        boolean isValid = MQ2MYMySQLDBHandler.getInstance().validateLogin(username, password);
        System.out.println("User login validation with result = "+isValid);

        return isValid;
    }

}
