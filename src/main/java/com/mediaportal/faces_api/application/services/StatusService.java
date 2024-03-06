package com.mediaportal.faces_api.application.services;

import com.google.gson.Gson;
import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.StatusMpaiDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StatusService {

    private final RestTemplate restTemplate;
    private final Gson gson;
    @Value("${paths.mpai}")
    private String mpaiUrl;

    public StatusService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public ApiResponseDTO checkStatusJob(String job_id) {


        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(mpaiUrl + "/jobs/" + job_id, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {

            StatusMpaiDTO statusMpaiDTO = gson.fromJson(response.getBody(), StatusMpaiDTO.class);

            return new ApiResponseDTO(response.getStatusCodeValue(), statusMpaiDTO, "");
        } else {
            // Erro
            System.out.println("Erro: " + response.getStatusCodeValue());
            return new ApiResponseDTO(response.getStatusCodeValue(), response.getBody(), response.toString());
        }

    }


}
