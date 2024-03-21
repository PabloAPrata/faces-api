package com.mediaportal.faces_api.application.services;

import com.google.gson.Gson;
import com.mediaportal.faces_api.Main;
import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.StatusMpaiDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class StatusService implements StatusServiceInterface {

    private static final Logger logger = LogManager.getLogger(StatusService.class);
    private RestTemplate restTemplate;
    private Gson gson;
    @Value("${paths.mpai}")
    private String mpaiUrl;

    public StatusService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public ApiResponseDTO checkStatusJob(String job_id) {

        logger.debug("Verificando status do job: " + job_id);
        try {
            return new ApiResponseDTO(200, requestStatusJob(job_id), "");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            return new ApiResponseDTO(e.getStatusCode().value(), e.getResponseBodyAsString(), e.toString());
        }

    }

    public StatusMpaiDTO requestStatusJob(String job_id) throws HttpClientErrorException {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(mpaiUrl + "/jobs/" + job_id, String.class);

        return gson.fromJson(response.getBody(), StatusMpaiDTO.class);

    }


}
