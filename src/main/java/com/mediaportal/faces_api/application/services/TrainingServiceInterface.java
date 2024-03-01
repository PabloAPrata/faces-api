package com.mediaportal.faces_api.application.services;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

public interface TrainingServiceInterface {
    ApiResponseDTO initiateTraining(Boolean isComplete);

    void generateTrainingFolder(String nameTrainingFolder) throws IOException;

    void createMainTrainingFolder(String nameTrainingFolder);

    void copyFilesToTrainingFolder(String nameTrainingFolder) throws IOException;

    List<String> getFileNamesFromJson() throws IOException;

    ClientActivateJobDTO requestTrainingToMpai(Boolean isComplete) throws RestClientException;

    void persistEventInDatabase(ClientActivateJobDTO responseMPAI);

    void createFolder(String directory, String nameFolder);

    String completeTrainingParams();

    String expressTrainingParams();

    List<String> getSchemaFilesFromDatabase();

}
