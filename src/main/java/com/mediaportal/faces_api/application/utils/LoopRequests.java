package com.mediaportal.faces_api.application.utils;

import com.mediaportal.faces_api.application.dto.StatusMpaiDTO;
import com.mediaportal.faces_api.application.services.StatusServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class LoopRequests {

    @Value("${paths.mpai}")
    private String mpaiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StatusServiceInterface statusService;

    public void startLoop(String jobId) {
        new Thread(() -> loopForStatus(jobId)).start();
    }

    private void loopForStatus(String jobId) {
        boolean isJobFinished = false;

        while (!isJobFinished) {
            StatusMpaiDTO statusResponse = statusService.requestStatusJob(jobId);

            if (statusResponse.getStatus().equals("done")) {
                isJobFinished = true;
            } else {
                System.out.println("JOB está: " + statusResponse.getStatus());
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    System.err.println("Erro ao aguardar.");
                    e.printStackTrace();
                }
            }
        }
    }
}
