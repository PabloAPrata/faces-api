package com.mediaportal.faces_api.application.utils;

import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.dto.TrainDTO;
import com.mediaportal.faces_api.application.dto.UpdateEventDTO;
import com.mediaportal.faces_api.application.services.StatusServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
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
public class ApiUtils implements ApiUtilsInterface {

    private static final String MPAI_BRIDGE_FILES_URL = "http://192.168.15.176:3001/";
    //    private final String MPAI_BRIDGE_FILES_URL = brahmaUrl + "repository/jobs/latest";
    private final RestTemplate restTemplate;
    private final StatusServiceInterface statusService;
    @Value("${paths.brahma}")
    private String brahmaUrl;
    @Value("${SHARED_FOLDER}")
    private String workFolder;
    @Value("${MAIN_FILES_FOLDER}")
    private String mainQualifyFolder;

    @Value("${COMPLETE_TRAINING_FOLDER}")
    private String completeTrainingFolder;

    @Value("${EXPRESS_TRAINING_FOLDER}")
    private String expressTrainingFolder;

    @Value("${GROUP_NAME_FOLDER}")
    private String groupNameFolder;

    @Value("${RECOGNITION_NAME_FOLDER}")
    private String recognitionNameFolder;

    public ApiUtils(RestTemplate restTemplate, StatusServiceInterface statusService) {
        this.restTemplate = restTemplate;
        this.statusService = statusService;
    }

    public static boolean deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Se for um arquivo, deleta
                    if (file.isFile()) {
                        if (!file.delete()) {
                            System.out.println("Falha ao deletar: " + file);
                            return false;
                        }
                    }
                    // Se for uma pasta, chama recursivamente para deletar
                    else if (file.isDirectory()) {
                        if (!deleteFolder(file)) {
                            return false;
                        }
                    }
                }
            }
        }

        // Deleta a própria pasta após deletar todo o seu conteúdo
        return folder.delete();
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

    public List<String> getFileNamesFromJson(String endpoint) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        List<String> fileNames = new ArrayList<>();


        // Construindo a URL com parâmetros, se necessário
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(MPAI_BRIDGE_FILES_URL + endpoint);

        try {
            // Fazendo a requisição GET e recebendo a resposta como uma string JSON
            System.out.println("Requisitando JSON de grupos e rostos..");
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Extraindo a string JSON do corpo da resposta
                String json = responseEntity.getBody();

                // Chamando o método para extrair os fileNames do JSON
                fileNames = extractFileNamesFromJson(json);
                System.out.println("Grupos e rostos pegados com sucesso!");
            } else {
                System.out.println("A requisição não foi bem-sucedida. Status code: " + responseEntity.getStatusCodeValue());
            }

            return fileNames;
        } catch (Exception e) {
            System.err.println("Erro ao obter a lista de pastas: " + e.getMessage());
            throw new IOException("Erro ao obter a lista de pastas do servidor.");
        }
    }

    public List<String> extractFileNamesFromJson(String json) {
        List<String> fileNames = new ArrayList<>();
//        System.out.println(json);
        Pattern pattern = Pattern.compile("\"fileName\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            fileNames.add(matcher.group(1));
        }
        return fileNames;
    }

    public void createAuxiliaryFolder(String directory, String nameFolder) {
        File newFolder = new File(directory, "/" + nameFolder);
        if (!newFolder.exists() && !newFolder.mkdirs()) {
            System.out.println("Failed to create directory: " + directory + nameFolder);
        }
    }

    public void persistEventInDatabase(ClientActivateJobDTO responseMPAI, int type) throws RestClientResponseException {
        TrainDTO trainDTO = new TrainDTO();
        trainDTO.setJobId(responseMPAI.getId());

        trainDTO.setType(type);


        System.out.println("Solicitando ao brahma que persista os dados no banco. Job_id:" + trainDTO.getJobId() + " Type: " + trainDTO.getType());
        restTemplate.postForEntity(brahmaUrl + "repository/event/new", trainDTO, Void.class);


    }

    public void generateAuxiliaryFolder(String nameTrainingFolder, Boolean bringUnknown) throws IOException {
        createAuxiliaryFolder(workFolder, nameTrainingFolder);
        copyFilesToAuxiliaryFolder(nameTrainingFolder, bringUnknown);
    }

    public void copyFilesToAuxiliaryFolder(String nameTrainingFolder, Boolean bringOnlyUnknown) throws IOException {
        List<String> fileNames = getFileNamesFromJson("files");
        for (String nameFolderPlusNameFile : fileNames) {
            String nameFolder = nameFolderPlusNameFile.split("/")[0];
            String nameFile = nameFolderPlusNameFile.split("/")[1];

            Path origin = Paths.get(workFolder + mainQualifyFolder + "/" + nameFolder, nameFile);
            Path destination = Paths.get(workFolder + nameTrainingFolder, nameFolder);

            if (bringOnlyUnknown && nameFolder.equals("unknown")) {
                createAuxiliaryFolder(workFolder + nameTrainingFolder, nameFolder);
                Files.copy(origin, destination.resolve(nameFile), StandardCopyOption.REPLACE_EXISTING);
            } else if (!bringOnlyUnknown && !nameFolder.equals("unknown")) {
                createAuxiliaryFolder(workFolder + nameTrainingFolder, nameFolder);
                Files.copy(origin, destination.resolve(nameFile), StandardCopyOption.REPLACE_EXISTING);
            }

        }
    }

    public void changeDatabaseStatus(String jobId, int status) throws RestClientException {

        try {
            UpdateEventDTO updateEventDTO = new UpdateEventDTO(jobId, status);

            System.out.println("Solicitando ao brahma que persista os dados no banco. Job_id:" + updateEventDTO.getJobId() + " Status: " + updateEventDTO.getStatus());

            restTemplate.put(brahmaUrl + "repository/event/update", updateEventDTO, Void.class);
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            throw new RestClientException("Erro persistir as informações no banco de dados." + e.toString());
        }
    }

    // Fazer posteriormente com que este método busque tais informações no banco de dados e guarde em memória.
    // Isso facilitará a manutenção do código e evita o custo de pesquisar no banco toda vez que a função for chamada.
    public int getStatusNumberByName(String statusName) {
        switch (statusName) {
            case "queued":
                return 1;
            case "starting":
                return 2;
            case "running":
                return 3;
            case "done":
                return 4;
            case "error":
                return 5;
            default:
                return 0;
        }
    }

    public void deleteAuxiliaryFolder(int jobType) {

        String folderPath = "";

        System.out.println(jobType);

        switch (jobType) {
            case 1:
                folderPath = workFolder + expressTrainingFolder;
                break;
            case 2:
                folderPath = workFolder + completeTrainingFolder;
                break;
            case 3:
                folderPath = workFolder + groupNameFolder;
                break;
            case 4:
                folderPath = workFolder + recognitionNameFolder;
                break;
        }

        File folder = new File(folderPath);


        if (folder.isDirectory() && folder.exists()) {
            boolean success = deleteFolder(folder);
            if (success) {
                System.out.println("Pasta deletada com sucesso!");
            } else {
                System.out.println("Nem todos os arquivos foram deletados.");
            }
        } else {
            System.out.println("A pasta não existe ou não é uma pasta válida.");
        }
    }

}



