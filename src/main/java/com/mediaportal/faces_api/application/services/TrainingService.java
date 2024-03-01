package com.mediaportal.faces_api.application.services;
// DEPOIS DE PRONTO. DEVE SER TROCADA A CHAMADA DA FUNÇÃO getFileNamesFromJson POR getSchemaFilesFromDatabase()

import com.google.gson.Gson;
import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.dto.PostTrainingMPAIDTO;
import com.mediaportal.faces_api.application.utils.ApiUtilsInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TrainingService implements TrainingServiceInterface {

    @Autowired
    public ApiUtilsInterface apiUtils;

    @Value("${paths.mpai}")
    private String mpaiUrl;

    @Value("${SHARED_FOLDER}")
    private String workFolder;

    @Value("${MAIN_FILES_FOLDER}")
    private String mainQualifyFolder;

    @Value("${COMPLETE_TRAINING_FOLDER}")
    private String completeTrainingFolder;

    @Value("${EXPRESS_TRAINING_FOLDER}")
    private String expressTrainingFolder;

    private final RestTemplate restTemplate;
    private final Gson gson;

    public TrainingService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public ApiResponseDTO initiateTraining(Boolean isComplete) {
        try {
            String trainingFolder = isComplete ? completeTrainingFolder : expressTrainingFolder;
            generateTrainingFolder(trainingFolder);
            ClientActivateJobDTO responseMPAI = requestTrainingToMpai(isComplete);
            apiUtils.persistEventInDatabase(responseMPAI);
            return new ApiResponseDTO(HttpStatus.CREATED.value(), responseMPAI, "Training initiated successfully!");
        } catch (IOException | RestClientException e) {
            return new ApiResponseDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), null, e.getMessage());
        }
    }

    public void generateTrainingFolder(String nameTrainingFolder) throws IOException {
        createMainTrainingFolder(nameTrainingFolder);
        copyFilesToTrainingFolder(nameTrainingFolder);
    }

    public void createMainTrainingFolder(String nameTrainingFolder) {
        apiUtils.createFolder(workFolder, nameTrainingFolder);
    }

    public void copyFilesToTrainingFolder(String nameTrainingFolder) throws IOException {
        List<String> fileNames = apiUtils.getFileNamesFromJson();
        for (String nameFolderPlusNameFile : fileNames) {
            String nameFolder = nameFolderPlusNameFile.split("/")[0];
            String nameFile = nameFolderPlusNameFile.split("/")[1];
            apiUtils.createFolder(workFolder + nameTrainingFolder, nameFolder);
            Path origin = Paths.get(workFolder + completeTrainingFolder + "/" + nameFolder, nameFile);
            Path destination = Paths.get(workFolder + nameTrainingFolder, nameFolder);

            Files.copy(origin, destination.resolve(nameFile), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public ClientActivateJobDTO requestTrainingToMpai(Boolean isComplete) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = isComplete ? completeTrainingParams() : expressTrainingParams();
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        System.out.println(request);

        return restTemplate.postForObject(mpaiUrl + "train", request, ClientActivateJobDTO.class);
    }

    public String completeTrainingParams() {
        PostTrainingMPAIDTO postTrainingMPAIDTO = new PostTrainingMPAIDTO(mainQualifyFolder, "mpCompleteModel.pkl");

        postTrainingMPAIDTO.setExtract(false);
        postTrainingMPAIDTO.setData_augmentation(false);
        postTrainingMPAIDTO.setTest_split(0.2);
        postTrainingMPAIDTO.setC(new ArrayList<>(Collections.singletonList(0.0003f)));
        postTrainingMPAIDTO.setKernel(new ArrayList<>(Collections.singletonList("linear")));
        postTrainingMPAIDTO.setThreshold_min(0.4);

        return gson.toJson(postTrainingMPAIDTO);
    }

    public String expressTrainingParams() {
        PostTrainingMPAIDTO postTrainingMPAIDTO = new PostTrainingMPAIDTO(mainQualifyFolder, "mpExpressModel.pkl");

        postTrainingMPAIDTO.setExtract(false);
        postTrainingMPAIDTO.setData_augmentation(false);
        postTrainingMPAIDTO.setTest_split(0.2);
        postTrainingMPAIDTO.setC(new ArrayList<>(Collections.singletonList(0.0003f)));
        postTrainingMPAIDTO.setKernel(new ArrayList<>(Collections.singletonList("linear")));
        postTrainingMPAIDTO.setThreshold_min(0.4);

        return gson.toJson(postTrainingMPAIDTO);
    }

}

