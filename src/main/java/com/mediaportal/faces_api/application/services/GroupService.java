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
public class GroupService {

    private final RestTemplate restTemplate;
    private final Gson gson;
    @Autowired
    public ApiUtilsInterface apiUtils;
    @Value("${paths.mpai}")
    private String mpaiUrl;
    @Value("${SHARED_FOLDER}")
    private String workFolder;
    @Value("${MAIN_FILES_FOLDER}")
    private String mainQualifyFolder;
    @Value("${GROUP_NAME_FOLDER}")
    private String GROUP_NAME_FOLDER;
    @Value("${RECOGNITION_NAME_FOLDER}")
    private String RECOGNITION_NAME_FOLDER;

    public GroupService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public ApiResponseDTO initiateRecognition(){
        try {
            apiUtils.generateAuxiliaryFolder(RECOGNITION_NAME_FOLDER, true);
            ClientActivateJobDTO responseMPAI = requestRecognizeToMpai();
            apiUtils.persistEventInDatabase(responseMPAI, 4);
            return new ApiResponseDTO(HttpStatus.CREATED.value(), responseMPAI, "Recognition started successfully");

        } catch (IOException | RestClientException e) {
            System.out.println(e.toString());
            return new ApiResponseDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), null, e.getMessage());
        }
    }

    public ApiResponseDTO initiateClassification() {
        try {

            apiUtils.generateAuxiliaryFolder(GROUP_NAME_FOLDER, true);

            ClientActivateJobDTO responseMPAI = requestGroupsToMpai();

            System.out.println("Tudo certo até aqui!");

            apiUtils.persistEventInDatabase(responseMPAI, 3);

            return new ApiResponseDTO(HttpStatus.CREATED.value(), responseMPAI, "Cluster started successfully");

        } catch (IOException | RestClientException e) {
            System.out.println(e.toString());
            return new ApiResponseDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), null, e.getMessage());
        }
    }

    public ClientActivateJobDTO requestGroupsToMpai() throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = groupMpaiParams();
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        return restTemplate.postForObject(mpaiUrl + "group", request, ClientActivateJobDTO.class);
    }

    public ClientActivateJobDTO requestRecognizeToMpai() throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = recognizeMpaiParams();
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        return restTemplate.postForObject(mpaiUrl + "recognize", request, ClientActivateJobDTO.class);
    }

    public String groupMpaiParams() {
        PostGroupDTO postGroupDTO = new PostGroupDTO(GROUP_NAME_FOLDER + "/unknown", 3);
        return gson.toJson(postGroupDTO);
    }

    public String recognizeMpaiParams() {
        PostRecognizeDTO postRecognizeDTO = new PostRecognizeDTO(RECOGNITION_NAME_FOLDER + "/unknown", "mpExpressModel.pkl");
        return gson.toJson(postRecognizeDTO);
    }

}
