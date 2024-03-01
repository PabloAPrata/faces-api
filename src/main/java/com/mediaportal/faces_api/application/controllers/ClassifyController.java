package com.mediaportal.faces_api.application.controllers;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classify")
public class ClassifyController {

    @PostMapping("/group")
    public ResponseEntity<String> agroupUnknownFolder() {
//        ApiResponseDTO response = trainingService.initiateTraining(true);
//        return ResponseEntity.status(response.getStatus()).body(response);
        return ResponseEntity.status(200).body("Olá");
    }

    @PostMapping("/recognition")
    public ResponseEntity<String> fastRecognitionUnknownFolder() {
//        ApiResponseDTO response = trainingService.initiateTraining(true);
//        return ResponseEntity.status(response.getStatus()).body(response);
        return ResponseEntity.status(200).body("Olá");
    }
}
