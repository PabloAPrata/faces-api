package com.mediaportal.faces_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@SpringBootApplication
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        logger.info("Aplicação iniciada!");

    }
}