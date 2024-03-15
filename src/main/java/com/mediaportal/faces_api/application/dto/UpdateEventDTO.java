package com.mediaportal.faces_api.application.dto;

public class UpdateEventDTO {
    private String jobId;
    private int status;

    public UpdateEventDTO(String jobId, int status){
        this.jobId = jobId;
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
