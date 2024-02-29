package com.mediaportal.faces_api.application.services;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TrainingService {

    private final String TRAIN_MPAI_FOLDER = "QUALIFICADOS";
    //  private final String MPAI_BRIDGE_FILES_URL = BRAHMA_URL + "mpaibridge/files";
    private final String MPAI_BRIDGE_FILES_URL = "http://localhost:3001/files";
    Gson gson = new Gson();
    RestTemplate restTemplate = new RestTemplate();
    String nameFolder = null;
    String nameFile = null;
    @Value("${paths.brahma}")
    private String BRAHMA_URL;
    @Value("${paths.mpai}")
    private String MPAI_URL;
    @Value("${SHARED_FOLDER}")
    private String WORK_FOLDER;

    private String errorMessage = "Ocorreu um erro ao iniciar o treinamento";

    public TrainingService() {
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

    public ApiResponseDTO trainingMPAI(Boolean isComplete) {

        try {
            String trainingFolder = isComplete ? "CompleteTrainingFolder" : "ExpressTrainingFolder";

            generateTrainingFolder(trainingFolder);

            ClientActivateJobDTO responseMPAI = requestTrainingToMpai(isComplete);

            persistEventInDatabase(responseMPAI);

            return new ApiResponseDTO(201, responseMPAI, "O treinamento foi iniciado com sucesso!");

        } catch (Exception e) {
            return new ApiResponseDTO(500, null, errorMessage);
        }

    }

    private void generateTrainingFolder(String nameTrainingFolder) {
        try {
            createMainTrainingFolder(nameTrainingFolder);
            copyFilesToTrainingFolder(nameTrainingFolder);
        } catch (IOException e) {
            errorMessage = "Erro ao criar a pasta de treinamento: " + e.getMessage();
            throw new RuntimeException("Erro ao criar a pasta de treinamento: " + e.getMessage(), e);
        }
    }

    private void createMainTrainingFolder(String nameTrainingFolder) throws IOException {
        createFolder(WORK_FOLDER, nameTrainingFolder);
    }

    private void copyFilesToTrainingFolder(String nameTrainingFolder) throws IOException {
        List<String> fileNames = getFileNamesFromJson();
        for (String nameFolderPlusNameFile : fileNames) {
            String nameFolder = nameFolderPlusNameFile.split("/")[0];
            String nameFile = nameFolderPlusNameFile.split("/")[1];
            createFolder(WORK_FOLDER + nameTrainingFolder, nameFolder);
            Path origin = Paths.get(WORK_FOLDER + TRAIN_MPAI_FOLDER + "/" + nameFolder, nameFile);
            Path destiny = Paths.get(WORK_FOLDER + nameTrainingFolder, nameFolder);
            try {
                Files.copy(origin, destiny.resolve(nameFile));
            } catch (IOException e) {
                System.out.println("\nNão foi possível copiar o arquivo. " + e.getMessage() + "\nERRO: " + e.toString());
            }
        }
    }

    private String completeTrainingParams() {
        PostTrainingMPAIDTO postTrainingMPAIDTO = new PostTrainingMPAIDTO(TRAIN_MPAI_FOLDER, "mpCompleteModel.pkl");

        postTrainingMPAIDTO.setExtract(false);
        postTrainingMPAIDTO.setData_augmentation(false);
        postTrainingMPAIDTO.setTest_split(0.2);
        postTrainingMPAIDTO.setC(new ArrayList<>(Collections.singletonList(0.0003f)));
        postTrainingMPAIDTO.setKernel(new ArrayList<>(Collections.singletonList("linear")));
        postTrainingMPAIDTO.setThreshold_min(0.4);

        return gson.toJson(postTrainingMPAIDTO);
    }

    private String expressTrainingParams() {
        PostTrainingMPAIDTO postTrainingMPAIDTO = new PostTrainingMPAIDTO(TRAIN_MPAI_FOLDER, "mpExpressModel.pkl");

        postTrainingMPAIDTO.setExtract(false);
        postTrainingMPAIDTO.setData_augmentation(false);
        postTrainingMPAIDTO.setTest_split(0.2);
        postTrainingMPAIDTO.setC(new ArrayList<>(Collections.singletonList(0.0003f)));
        postTrainingMPAIDTO.setKernel(new ArrayList<>(Collections.singletonList("linear")));
        postTrainingMPAIDTO.setThreshold_min(0.4);

        return gson.toJson(postTrainingMPAIDTO);
    }

    private List<String> getSchemaFilesFromDatabase() {
        try {
            ResponseEntity<String[]> response = restTemplate.exchange(MPAI_BRIDGE_FILES_URL, HttpMethod.GET, null, String[].class);
            String[] data = response.getBody();

            if (data != null) {
                return Arrays.asList(data);
            } else {
                return Collections.emptyList();
            }
        } catch (RestClientException e) {
            errorMessage = "Erro ao obter a lista de pastas: " + e.getMessage();
            System.err.println("Erro ao obter a lista de pastas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private void createFolder(String directory, String nameFolder) {
        File newFolder = new File(directory, nameFolder);

        if (newFolder.exists()) {
            return;
        }

        if (!newFolder.mkdirs()) {
            System.out.println("Erro ao criar a pasta: " + directory + nameFolder);
        }
    }

    private List<String> getFileNamesFromJson() {
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
            errorMessage = "Erro ao obter a lista de pastas do servidor";
            System.err.println("Erro ao obter a lista de pastas: " + e.getMessage());
            return null;
        }
    }

    private ClientActivateJobDTO requestTrainingToMpai(Boolean isComplete) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = isComplete ? completeTrainingParams() : expressTrainingParams();

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        ClientActivateJobDTO response = null;

        try {
            response = restTemplate.postForObject(MPAI_URL + "train", request, ClientActivateJobDTO.class);
        } catch (Exception e) {
            errorMessage = "Não conseguimos iniciar o treinamento. Talvez o servidor MPAI esteja fora do ar.";
            System.out.println("ERRO: Não foi possível requisitar o serviço do MPAI\n" + e.getMessage());
        }


        return response;
    }

    private void persistEventInDatabase(ClientActivateJobDTO responseMPAI) {
        TrainDTO trainDTO = new TrainDTO();
        String jobID = responseMPAI.getId();
        trainDTO.setJob_id(jobID);
        trainDTO.setType(1);
        try {
            restTemplate.postForEntity(BRAHMA_URL + "mpaibridge/newevent", trainDTO, Void.class);
        } catch(Exception e) {
            errorMessage = "Erro ao gravar o evento no banco de dados!" + e.getMessage();
        }

    }

}
