package com.mediaportal.faces_api.application.utils;

import com.google.gson.Gson;
import com.mediaportal.faces_api.application.dto.StatusMpaiDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

public class LoopRequests extends Thread {

    private static final String mpaiUrl = "http://192.168.15.30:8000/";
    private final String jobId;

    public LoopRequests(String jobId) {
        this.jobId = jobId;
    }

    public void run() {
        boolean isJobFinished = false;

        while (!isJobFinished) {

            StatusMpaiDTO statusResponse = requestStatusJob(jobId);

            if (statusResponse.getStatus().equals("done")) {
                isJobFinished = true;
                System.out.println("Resposta esperada recebida. Saindo do loop. PABLO");
            } else {

                System.out.println("JOB está: " + statusResponse.getStatus());
                // Aguarde 1 minuto antes da próxima requisição
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    System.err.println("Erro ao aguardar.");
                    e.printStackTrace();
                }

            }

        }
    }

    public StatusMpaiDTO requestStatusJob(String job_id) throws HttpClientErrorException {
        Gson gson = new Gson();
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(mpaiUrl + "jobs/" + job_id, String.class);

        return gson.fromJson(response.getBody(), StatusMpaiDTO.class);
    }
}
