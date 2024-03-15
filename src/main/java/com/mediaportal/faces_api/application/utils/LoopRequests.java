package com.mediaportal.faces_api.application.utils;

import com.mediaportal.faces_api.application.dto.StatusMpaiDTO;
import com.mediaportal.faces_api.application.services.StatusServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class LoopRequests {

    @Autowired
    public ApiUtilsInterface apiUtils;
    @Value("${paths.mpai}")
    private String mpaiUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StatusServiceInterface statusService;

    public void startLoop(String jobId, int type) {
        new Thread(() -> loopForStatus(jobId, type)).start();
    }

    private Boolean isStatusChanged(int previousStatus, int currentStatus) {
        if (previousStatus != currentStatus) {
            System.out.println("O status mudou de " + previousStatus + "para" + currentStatus);
            return true;
        } else return false;
    }

    private void loopForStatus(String jobId, int type) throws RestClientException {

        boolean isJobFinished = false;
        int previousStatus = 0;
        int currentStatus;
        String statusName;

        while (!isJobFinished) {
            StatusMpaiDTO response = statusService.requestStatusJob(jobId);
            statusName = response.getStatus();
            currentStatus = apiUtils.getStatusNumberByName(statusName);

            // Verifica se houve mudança de status
            if (isStatusChanged(previousStatus, currentStatus)) apiUtils.changeDatabaseStatus(jobId, currentStatus);

            previousStatus = currentStatus;

            if (statusName.equals("done")) {
                isJobFinished = true;
            }
            else {
                System.out.println("JOB está: " + statusName);
                waitOneMinute();
            }
        }
    }

    private void waitOneMinute(){
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            System.err.println("Erro ao aguardar. " + e.getMessage());
//            e.printStackTrace();
        }
    }

}

