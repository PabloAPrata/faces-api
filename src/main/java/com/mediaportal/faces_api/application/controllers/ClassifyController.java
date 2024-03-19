package com.mediaportal.faces_api.application.controllers;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.services.ClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classify")
public class ClassifyController {

    private final ClassifyService classifyService;

    @Autowired
    public ClassifyController(ClassifyService classifyService) {
        this.classifyService = classifyService;
    }

    @PostMapping("/group")
    public ResponseEntity<ApiResponseDTO> agroupUnknownFolder() {
        ApiResponseDTO response = classifyService.initiateClassification();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/recognition")
    public ResponseEntity<ApiResponseDTO> fastRecognitionUnknownFolder() {
        ApiResponseDTO response = classifyService.initiateRecognition();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
