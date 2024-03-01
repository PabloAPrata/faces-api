package com.mediaportal.faces_api.application.utils;

import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import com.mediaportal.faces_api.application.dto.TrainDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiUtils {

    private static final String MPAI_BRIDGE_FILES_URL = "http://localhost:3001/files";
    //  private static final String MPAI_BRIDGE_FILES_URL = BRAHMA_URL + "mpaibridge/files";

    @Value("${paths.brahma}")
    private String brahmaUrl;

    private final RestTemplate restTemplate;

    public ApiUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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

    public void createFolder(String directory, String nameFolder) {
        File newFolder = new File(directory, nameFolder);
        if (!newFolder.exists() && !newFolder.mkdirs()) {
            System.out.println("Failed to create directory: " + directory + nameFolder);
        }
    }

    public void persistEventInDatabase(ClientActivateJobDTO responseMPAI) {
        TrainDTO trainDTO = new TrainDTO();
        trainDTO.setJob_id(responseMPAI.getId());
        trainDTO.setType(1);

        restTemplate.postForEntity(brahmaUrl + "mpaibridge/newevent", trainDTO, Void.class);
    }
}
