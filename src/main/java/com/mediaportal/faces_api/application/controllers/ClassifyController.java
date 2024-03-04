package com.mediaportal.faces_api.application.controllers;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classify")
public class ClassifyController {

    private final GroupService classifyService;

    @Autowired
    public ClassifyController(GroupService classifyService) {this. classifyService = classifyService;}

    @PostMapping("/group")
    public ResponseEntity<ApiResponseDTO> agroupUnknownFolder() {
        ApiResponseDTO response = classifyService.initiateClassification();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/recognition")
    public ResponseEntity<String> fastRecognitionUnknownFolder() {
//        ApiResponseDTO response = trainingService.initiateTraining(true);
//        return ResponseEntity.status(response.getStatus()).body(response);
        return ResponseEntity.status(200).body("Olá");
    }
}
