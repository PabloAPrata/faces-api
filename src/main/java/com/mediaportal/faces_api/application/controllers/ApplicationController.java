package com.mediaportal.faces_api.application.controllers;

import com.mediaportal.faces_api.application.services.TrainingService;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;

@RestController
public class ApplicationController {

    @GetMapping("/status/{id}")
    public String getStatusJob(@PathVariable String id) {
        return "Olá, " + id + "!";
    }

    @PostMapping("/training/express")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientActivateJobDTO preTraining() {
        TrainingService trainingService = new TrainingService();
//        return trainingService.trainMPAI(false);
        return trainingService.getFromMPAI();
    }




}

