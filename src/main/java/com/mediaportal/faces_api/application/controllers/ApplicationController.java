package com.mediaportal.faces_api.application.controllers;

import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.services.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApplicationController {

    private final TrainingService trainingService;

    @Autowired
    public ApplicationController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/status/{id}")
    public String getStatusJob(@PathVariable String id) {
        return "Olá, " + id + "!";
    }

    @PostMapping("/training/express")
    public ResponseEntity<?> trainingExpress() {
        try {
            return ResponseEntity.ok(trainingService.trainingMPAI(false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado: " + e.getMessage());
        }

    }

//    @PostMapping("/training/complete")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ClientActivateJobDTO TrainingComplete() {
//        return trainingService.trainMPAI(true);
//    }

}

