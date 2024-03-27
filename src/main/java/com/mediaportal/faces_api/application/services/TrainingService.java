package com.mediaportal.faces_api.application.services;
// DEPOIS DE PRONTO. DEVE SER TROCADA A CHAMADA DA FUNÇÃO getFileNamesFromJson POR getSchemaFilesFromDatabase()

import com.google.gson.Gson;
import com.mediaportal.faces_api.Main;
import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.dto.ErrorMpaiDetailsDTO;
import com.mediaportal.faces_api.application.dto.PostTrainingMPAIDTO;
import com.mediaportal.faces_api.application.utils.ApiUtilsInterface;
import com.mediaportal.faces_api.application.utils.LoopRequests;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Service
public class TrainingService implements TrainingServiceInterface {


    private static final Logger logger = LogManager.getLogger(TrainingService.class);
    private final RestTemplate restTemplate;
    private final Gson gson;
    @Autowired
    public ApiUtilsInterface apiUtils;
    @Autowired
    private LoopRequests loopRequests;
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

    public TrainingService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public ApiResponseDTO initiateTraining(Boolean isComplete) {
        logger.debug("Iniciando sequência de treinamento. Completo? " + isComplete);
        try {
            String trainingFolder = isComplete ? completeTrainingFolder : expressTrainingFolder;
            apiUtils.generateAuxiliaryFolder(trainingFolder, false);
            ClientActivateJobDTO responseMPAI = requestTrainingToMpai(isComplete);

            int type = isComplete ? 2 : 1;

            String jobId = responseMPAI.getId();

            apiUtils.persistEventInDatabase(responseMPAI, type);

            loopRequests.startLoop(jobId, type);

            return new ApiResponseDTO(HttpStatus.CREATED.value(), responseMPAI, "Training initiated successfully!");
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ApiResponseDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), null, e.getMessage());
        } catch (RestClientResponseException io) {
            logger.error(io.getMessage());
            if (io.getRawStatusCode() == 400) {
                ErrorMpaiDetailsDTO errorDetails = gson.fromJson(io.getResponseBodyAsString(), ErrorMpaiDetailsDTO.class);
                return new ApiResponseDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), errorDetails , "Não foi possível iniciar o Reconhecimento." );
            }
            return new ApiResponseDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), null , io.getMessage() );
        }
    }

    public ClientActivateJobDTO requestTrainingToMpai(Boolean isComplete) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = isComplete ? completeTrainingParams() : expressTrainingParams();
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        return restTemplate.postForObject(mpaiUrl + "train", request, ClientActivateJobDTO.class);
    }

    public String completeTrainingParams() {
        PostTrainingMPAIDTO postTrainingMPAIDTO = new PostTrainingMPAIDTO(mainQualifyFolder, "mpCompleteModel.pkl");

        postTrainingMPAIDTO.setExtract(false);
        postTrainingMPAIDTO.setData_augmentation(true);
        postTrainingMPAIDTO.setTest_split(0.2);
        postTrainingMPAIDTO.setC(Arrays.asList(0.0003f, 0.00035f, 0.0004f, 0.00045f, 0.0005f));
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

