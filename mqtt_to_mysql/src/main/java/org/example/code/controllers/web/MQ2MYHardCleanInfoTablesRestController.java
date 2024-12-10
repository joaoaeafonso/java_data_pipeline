package org.example.code.controllers.web;

import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MQ2MYHardCleanInfoTablesRestController {

    public MQ2MYHardCleanInfoTablesRestController() {
        System.out.println("Created an instance of MQ2MYHardCleanInfoTablesRestController. Ready to use.");
    }

    @PostMapping("/api/clean_tables_hard")
    public void receiveData() {
        System.out.println("Cleaning all reading tables - Hard Delete.");
        MQ2MYMySQLDBHandler.getInstance().hardCleanAllReadingTables();
    }
}
