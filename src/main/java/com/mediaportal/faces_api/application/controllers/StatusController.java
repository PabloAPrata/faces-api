package com.mediaportal.faces_api.application.controllers;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.services.StatusService;
import com.mediaportal.faces_api.application.services.StatusServiceInterface;
import com.mediaportal.faces_api.application.services.TrainingServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private final StatusServiceInterface statusService;

    public StatusController(StatusServiceInterface statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<ApiResponseDTO> groupUnknownFolder(@PathVariable String id) {
        ApiResponseDTO response = statusService.checkStatusJob(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
