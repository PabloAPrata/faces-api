package com.mediaportal.faces_api.application.services;

import com.google.gson.Gson;
import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.dto.PostGroupDTO;
import com.mediaportal.faces_api.application.dto.PostRecognizeDTO;
import com.mediaportal.faces_api.application.utils.ApiUtilsInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
@Service
public class FolderService {

    private final RestTemplate restTemplate;

    private final Gson gson;

    public FolderService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }


}
