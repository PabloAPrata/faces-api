package com.mediaportal.faces_api.application.controllers;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.services.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponseDTO> expressTraining() {
        ApiResponseDTO response = trainingService.trainingMPAI(true);
        return new ResponseEntity<ApiResponseDTO>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @PostMapping("/training/complete")
    public ResponseEntity<ApiResponseDTO> completeTraining() {
        ApiResponseDTO response = trainingService.trainingMPAI(true);
        return new ResponseEntity<ApiResponseDTO>(response, HttpStatus.valueOf(response.getStatus()));
    }

//    @PostMapping("/regroup")
//    public ResponseEntity<?> regroup() {
//        try {
//            return ResponseEntity.ok(trainingService.trainingMPAI(true));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado: " + e.getMessage());
//        }
//    }

    //    @PostMapping("/express_recognition")
//    public ResponseEntity<?> regroup() {
//        try {
//            return ResponseEntity.ok(trainingService.trainingMPAI(true));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado: " + e.getMessage());
//        }
//    }

    //    @PostMapping("/express_recognition")
//    public ResponseEntity<?> regroup() {
//        try {
//            return ResponseEntity.ok(trainingService.trainingMPAI(true));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado: " + e.getMessage());
//        }
//    }


}

