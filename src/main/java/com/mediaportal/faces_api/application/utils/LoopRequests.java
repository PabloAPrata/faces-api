package com.mediaportal.faces_api.application.utils;

import com.mediaportal.faces_api.application.dto.StatusMpaiDTO;
import com.mediaportal.faces_api.application.services.ClassifyService;
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

    @Autowired
    private ClassifyService classifyService;

    public void startLoop(String jobId, int type) {
        new Thread(() -> loopForStatus(jobId, type)).start();
    }

    private Boolean isStatusChanged(int previousStatus, int currentStatus) {
        if (previousStatus != currentStatus) {
            System.out.println("O status mudou de " + previousStatus + " para " + currentStatus);
            return true;
        } else return false;
    }

    private void loopForStatus(String jobId, int type) throws RestClientException {

        boolean isJobFinished = false;
        int previousStatus = 1;
        int currentStatus;
        String statusName;

        while (!isJobFinished) {
            StatusMpaiDTO response = statusService.requestStatusJob(jobId);
            statusName = response.getStatus();
            currentStatus = apiUtils.getStatusNumberByName(statusName);

            if (isStatusChanged(previousStatus, currentStatus)) apiUtils.changeDatabaseStatus(jobId, currentStatus);

            previousStatus = currentStatus;

            if (statusName.equals("done")) {
                isJobFinished = true;

                if (type == 1) {
                    apiUtils.deleteAuxiliaryFolder(1);
                    return;
                }
                if (type == 2) {
                    apiUtils.deleteAuxiliaryFolder(2);
                    return;
                }
                if (type == 3) {
                    classifyService.readGroupJSON(jobId);
                    return;
                }
                if (type == 4) {
                    classifyService.readRecognizeJSON(jobId);
                    return;
                }

            } else {
                System.out.println("JOB está: " + statusName);
                waitOneMinute();
            }
        }
    }

    private void waitOneMinute() {
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            System.err.println("Erro ao aguardar. " + e.getMessage());
//            e.printStackTrace();
        }
    }


}

