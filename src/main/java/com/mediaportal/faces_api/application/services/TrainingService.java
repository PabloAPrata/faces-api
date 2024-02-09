package com.mediaportal.faces_api.application.services;

import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.dto.PostTrainingMPAIDTO;
import com.mediaportal.faces_api.application.repository.JobRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Service
public class TrainingService {

    private static final String TRAIN_URL = "http://localhost:3001/train";
    private static final String TRAIN_FOLDER = "storenext/complete/o/caminho/aqui";

    public TrainingService(){}

    public ClientActivateJobDTO trainMPAI(Boolean isComplete) {
        RestTemplate restTemplate = new RestTemplate();

        // Define o modelo com base no tipo de treinamento
        String model = isComplete ? "mpCompleteModel.pkl" : "mpExpressModel.pkl";

        PostTrainingMPAIDTO postTrainingMPAIDTO = new PostTrainingMPAIDTO(TRAIN_FOLDER, model);

        ResponseEntity<ClientActivateJobDTO> responseEntity = restTemplate.postForEntity(TRAIN_URL, postTrainingMPAIDTO, ClientActivateJobDTO.class);
        ClientActivateJobDTO responseBody = responseEntity.getBody();

        System.out.println(responseBody);
        return responseBody;
    }

    public ClientActivateJobDTO getFromMPAI() {
        RestTemplate restTemplate = new RestTemplate();

        ClientActivateJobDTO responseMPAI = restTemplate.getForObject(TRAIN_URL, ClientActivateJobDTO.class);

        LocalDateTime currentDateTime = LocalDateTime.now();
        JobRepository jobRepository = new JobRepository();

        jobRepository.RecordJobRepository(responseMPAI.getId(), 1, currentDateTime.toString());

        return responseMPAI;
    }

    private void SynchronizeDatabaseWithFolders() {

    }

}
