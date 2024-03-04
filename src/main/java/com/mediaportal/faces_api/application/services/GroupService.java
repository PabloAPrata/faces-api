package com.mediaportal.faces_api.application.services;

import com.google.gson.Gson;
import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
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
import com.mediaportal.faces_api.application.dto.PostGroupDTO;

import java.io.IOException;
import java.util.List;

@Service
public class GroupService  {

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

    private final RestTemplate restTemplate;

    private final Gson gson;

    public GroupService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public ApiResponseDTO initiateClassification() {
        try {


            // 1 - Usar método de utils para criar a nova pasta para agrupamento.
            apiUtils.generateAuxiliaryFolder(GROUP_NAME_FOLDER, true);
            // 2 - Criar um método que chama o MPAI para que começe o agrupamento
             ClientActivateJobDTO responseMPAI = requestGroupsToMpai();
            apiUtils.persistEventInDatabase(responseMPAI, 3);
            return new ApiResponseDTO(HttpStatus.CREATED.value(), responseMPAI, "Cluster started successfully");
            // 3 - Criar um método em utils que verifica se um JOB terminou.
            // 4 - Criar um método em utils que apaga alguma...

        } catch (IOException | RestClientException e) {
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

    public String groupMpaiParams() {
        PostGroupDTO postGroupDTO = new PostGroupDTO(GROUP_NAME_FOLDER + "/unknown", 3);
        return gson.toJson(postGroupDTO);
    }

}
