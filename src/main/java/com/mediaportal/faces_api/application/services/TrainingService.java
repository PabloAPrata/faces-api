package com.mediaportal.faces_api.application.services;
// DEPOIS DE PRONTO. DEVE SER TROCADA A CHAMADA DA FUNÇÃO getFileNamesFromJson POR getSchemaFilesFromDatabase()

import com.google.gson.Gson;
import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.dto.PostTrainingMPAIDTO;
import com.mediaportal.faces_api.application.dto.TrainDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TrainingService implements TrainingServiceInterface {

    private static final String TRAIN_MPAI_FOLDER = "QUALIFICADOS";
    private static final String COMPLETE_TRAINING_FOLDER = "CompleteTrainingFolder";
    private static final String EXPRESS_TRAINING_FOLDER = "ExpressTrainingFolder";
    private static final String MPAI_BRIDGE_FILES_URL = "http://localhost:3001/files";
    //  private static final String MPAI_BRIDGE_FILES_URL = BRAHMA_URL + "mpaibridge/files";

    @Value("${paths.brahma}")
    private String brahmaUrl;

    @Value("${paths.mpai}")
    private String mpaiUrl;

    @Value("${SHARED_FOLDER}")
    private String workFolder;

    private final RestTemplate restTemplate;
    private final Gson gson;

    public TrainingService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public ApiResponseDTO initiateTraining(Boolean isComplete) {
        try {
            String trainingFolder = isComplete ? COMPLETE_TRAINING_FOLDER : EXPRESS_TRAINING_FOLDER;
            generateTrainingFolder(trainingFolder);
            ClientActivateJobDTO responseMPAI = requestTrainingToMpai(isComplete);
            persistEventInDatabase(responseMPAI);
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
        createFolder(workFolder, nameTrainingFolder);
    }

    public void copyFilesToTrainingFolder(String nameTrainingFolder) throws IOException {
        List<String> fileNames = getFileNamesFromJson();
        for (String nameFolderPlusNameFile : fileNames) {
            String nameFolder = nameFolderPlusNameFile.split("/")[0];
            String nameFile = nameFolderPlusNameFile.split("/")[1];
            createFolder(workFolder + nameTrainingFolder, nameFolder);
            Path origin = Paths.get(workFolder + COMPLETE_TRAINING_FOLDER + "/" + nameFolder, nameFile);
            Path destination = Paths.get(workFolder + nameTrainingFolder, nameFolder);

            Files.copy(origin, destination.resolve(nameFile), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public List<String> getFileNamesFromJson() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        List<String> fileNames = new ArrayList<>();

        // Construindo a URL com parâmetros, se necessário
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(MPAI_BRIDGE_FILES_URL);

        try {
            // Fazendo a requisição GET e recebendo a resposta como uma string JSON
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Extraindo a string JSON do corpo da resposta
                String json = responseEntity.getBody();

                // Chamando o método para extrair os fileNames do JSON
                fileNames = extractFileNamesFromJson(json);
            } else {
                System.out.println("A requisição não foi bem-sucedida. Status code: " + responseEntity.getStatusCodeValue());
            }

            return fileNames;
        } catch (Exception e) {
            System.err.println("Erro ao obter a lista de pastas: " + e.getMessage());
            throw new IOException("Erro ao obter a lista de pastas do servidor.");
        }
    }

    private static List<String> extractFileNamesFromJson(String json) {
        List<String> fileNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"fileName\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            fileNames.add(matcher.group(1));
        }
        return fileNames;
    }

    public ClientActivateJobDTO requestTrainingToMpai(Boolean isComplete) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = isComplete ? completeTrainingParams() : expressTrainingParams();
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        System.out.println(request);

        return restTemplate.postForObject(mpaiUrl + "train", request, ClientActivateJobDTO.class);
    }

    public void persistEventInDatabase(ClientActivateJobDTO responseMPAI) {
        TrainDTO trainDTO = new TrainDTO();
        trainDTO.setJob_id(responseMPAI.getId());
        trainDTO.setType(1);

        restTemplate.postForEntity(brahmaUrl + "mpaibridge/newevent", trainDTO, Void.class);
    }

    public void createFolder(String directory, String nameFolder) {
        File newFolder = new File(directory, nameFolder);
        if (!newFolder.exists() && !newFolder.mkdirs()) {
            System.out.println("Failed to create directory: " + directory + nameFolder);
        }
    }

    public String completeTrainingParams() {
        PostTrainingMPAIDTO postTrainingMPAIDTO = new PostTrainingMPAIDTO(TRAIN_MPAI_FOLDER, "mpCompleteModel.pkl");

        postTrainingMPAIDTO.setExtract(false);
        postTrainingMPAIDTO.setData_augmentation(false);
        postTrainingMPAIDTO.setTest_split(0.2);
        postTrainingMPAIDTO.setC(new ArrayList<>(Collections.singletonList(0.0003f)));
        postTrainingMPAIDTO.setKernel(new ArrayList<>(Collections.singletonList("linear")));
        postTrainingMPAIDTO.setThreshold_min(0.4);

        return gson.toJson(postTrainingMPAIDTO);
    }

    public String expressTrainingParams() {
        PostTrainingMPAIDTO postTrainingMPAIDTO = new PostTrainingMPAIDTO(TRAIN_MPAI_FOLDER, "mpExpressModel.pkl");

        postTrainingMPAIDTO.setExtract(false);
        postTrainingMPAIDTO.setData_augmentation(false);
        postTrainingMPAIDTO.setTest_split(0.2);
        postTrainingMPAIDTO.setC(new ArrayList<>(Collections.singletonList(0.0003f)));
        postTrainingMPAIDTO.setKernel(new ArrayList<>(Collections.singletonList("linear")));
        postTrainingMPAIDTO.setThreshold_min(0.4);

        return gson.toJson(postTrainingMPAIDTO);
    }

    public List<String> getSchemaFilesFromDatabase() {
        try {
            ResponseEntity<String[]> response = restTemplate.exchange(MPAI_BRIDGE_FILES_URL, HttpMethod.GET, null, String[].class);
            String[] data = response.getBody();

            if (data != null) {
                return Arrays.asList(data);
            } else {
                return Collections.emptyList();
            }
        } catch (RestClientException e) {
            System.err.println("Erro ao obter a lista de pastas: " + e.getMessage());
            throw new RestClientException("Erro ao obter a lista de pastas e arquivos.");
        }
    }

}

