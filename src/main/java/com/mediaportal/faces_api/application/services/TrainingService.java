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

    public TrainingService() {
    }

    public ApiResponseDTO trainingMPAI(Boolean isComplete) {

        ClientActivateJobDTO responseMPAI = null;
        String trainingFolder = isComplete ? "CompleteTrainingFolder" : "ExpressTrainingFolder";

        try {
            generateTrainingFolder(trainingFolder);
        } catch (IOException e) {
            return new ApiResponseDTO(503, null, e.getMessage());
        }

        try {
            responseMPAI = requestTrainingToMpai(isComplete);
        } catch (RestClientException re) {
            return new ApiResponseDTO(503, null, re.getMessage());
        }

        try {
            persistEventInDatabase(responseMPAI);
        } catch (RestClientException re) {
            return new ApiResponseDTO(503, null, re.getMessage());
        }

        return new ApiResponseDTO(201, responseMPAI, "O treinamento foi iniciado com sucesso!");

    }

    private void generateTrainingFolder(String nameTrainingFolder) throws IOException {
        createMainTrainingFolder(nameTrainingFolder);
        copyFilesToTrainingFolder(nameTrainingFolder);
    }

    private void createMainTrainingFolder(String nameTrainingFolder) {
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
                System.out.println("Não foi possível copiar o arquivo: " + e.getMessage());
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
            System.err.println("Erro ao obter a lista de pastas: " + e.getMessage());
            throw new RestClientException("Erro ao obter a lista de pastas e arquivos.");
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
    private static List<String> extractFileNamesFromJson(String json) {
        List<String> fileNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"fileName\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            fileNames.add(matcher.group(1));
        }
        return fileNames;
    }
    private List<String> getFileNamesFromJson() throws IOException {
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

    private ClientActivateJobDTO requestTrainingToMpai(Boolean isComplete) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = isComplete ? completeTrainingParams() : expressTrainingParams();

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        ClientActivateJobDTO response = null;

        try {
            response = restTemplate.postForObject(MPAI_URL + "train", request, ClientActivateJobDTO.class);
        } catch (RestClientException e) {
            System.out.println("ERRO: Não foi possível requisitar o serviço do MPAI\n" + e.getMessage());
            throw new RestClientException("Não conseguimos iniciar o treinamento. Talvez o servidor MPAI esteja fora do ar." + e.getMessage());
        }

        return response;
    }

    private void persistEventInDatabase(ClientActivateJobDTO responseMPAI) throws RestClientException {
        TrainDTO trainDTO = new TrainDTO();
        String jobID = responseMPAI.getId();
        trainDTO.setJob_id(jobID);
        trainDTO.setType(1);
        try {
            restTemplate.postForEntity(BRAHMA_URL + "mpaibridge/newevent", trainDTO, Void.class);
        } catch (RestClientException e) {
            throw new RestClientException("Erro ao persistir evento no banco de dados. " + e.getMessage());
        }

    }

}
