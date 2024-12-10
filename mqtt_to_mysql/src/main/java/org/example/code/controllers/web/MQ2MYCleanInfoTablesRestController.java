package org.example.code.controllers.web;

import org.example.code.handlers.MQ2MYMySQLDBHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MQ2MYCleanInfoTablesRestController {

    public MQ2MYCleanInfoTablesRestController() {
        System.out.println("Created an instance of MQ2MYCleanInfoTablesRestController. Ready to use.");
    }

    @PostMapping("/api/clean_tables_soft")
    public void receiveData() {
        System.out.println("Cleaning all reading tables - Soft Delete.");
        MQ2MYMySQLDBHandler.getInstance().softCleanAllReadingTables();
    }
}
