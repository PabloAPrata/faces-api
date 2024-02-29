package com.mediaportal.faces_api.application.controllers;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.services.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/training")
public class TrainingController {

    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/express")
    public ResponseEntity<ApiResponseDTO> expressTraining() {
        ApiResponseDTO response = trainingService.initiateTraining(true);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<ApiResponseDTO> completeTraining() {
        ApiResponseDTO response = trainingService.initiateTraining(true);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
