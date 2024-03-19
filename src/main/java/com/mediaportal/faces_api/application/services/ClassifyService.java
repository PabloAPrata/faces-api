package com.mediaportal.faces_api.application.services;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mediaportal.faces_api.application.dto.*;
import com.mediaportal.faces_api.application.utils.ApiUtilsInterface;
import com.mediaportal.faces_api.application.utils.LoopRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClassifyService {

    private final RestTemplate restTemplate;
    private final Gson gson;
    @Autowired
    public ApiUtilsInterface apiUtils;
    @Value("${paths.brahma}")
    private String brahmaUrl;
    @Autowired
    private LoopRequests loopRequests;
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

    public ClassifyService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    private static String extractFileName(String originalString) {
        Path path = Paths.get(originalString.replace("\"", ""));
        return path.getFileName().toString();
    }

    public void setBrahmaUrl(String brahmaUrl) {
        this.brahmaUrl = brahmaUrl;
    }

    public ApiResponseDTO initiateRecognition() {
        try {
            apiUtils.generateAuxiliaryFolder(RECOGNITION_NAME_FOLDER, true);

            ClientActivateJobDTO responseMPAI = requestRecognizeToMpai();

            String jobId = responseMPAI.getId();

            loopRequests.startLoop(jobId, 4);

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

            String jobId = responseMPAI.getId();

            loopRequests.startLoop(jobId, 3);

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

    public void readGroupJSON() {
        try {

            JsonObject jsonObject = JsonParser.parseReader(new FileReader(workFolder + GROUP_NAME_FOLDER + "/" + "unknown.json")).getAsJsonObject();
            JsonObject groupsObject = jsonObject.getAsJsonObject("groups");
            List<String> groupsList = new ArrayList<>();

            for (Map.Entry<String, JsonElement> entry : groupsObject.entrySet()) {
                for (Object item : (JsonArray) entry.getValue()) {
                    groupsList.add(entry.getKey() + "/" + extractFileName(item.toString()));
                }
            }

            requestInsertGroup(groupsList);

            apiUtils.deleteAuxiliaryFolder(3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestInsertGroup(List<String> groupsList) {
        try {
            restTemplate.put(brahmaUrl + "repository/data/group/update", groupsList, Void.class);
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            throw new RestClientException("Erro persistir as informações no banco de dados." + e.toString());
        }
    }

    public void readRecognizeJSON() {
        String arquivoJSON = workFolder + RECOGNITION_NAME_FOLDER + "/" + "unknown.json";

        List<String> listaFiles = new ArrayList<>();

        try {

            Gson gson = new Gson();

            FileReader fileReader = new FileReader(arquivoJSON);
            Type listaTipo = new TypeToken<List<RecognizeJsonDTO>>() {
            }.getType();
            List<RecognizeJsonDTO> listaArquivos = gson.fromJson(fileReader, listaTipo);

            for (RecognizeJsonDTO arquivo : listaArquivos) {
                listaFiles.add(arquivo.getClasse() + "/" + extractFileName(arquivo.getFile()));
            }

            requestInsertGroup(listaFiles);

            apiUtils.deleteAuxiliaryFolder(4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

