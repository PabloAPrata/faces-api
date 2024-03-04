package com.mediaportal.faces_api.application.services;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.dto.TrainDTO;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

public interface TrainingServiceInterface {
    ApiResponseDTO initiateTraining(Boolean isComplete);

    ClientActivateJobDTO requestTrainingToMpai(Boolean isComplete) throws RestClientException;

    String completeTrainingParams();

    String expressTrainingParams();

}
