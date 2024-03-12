package com.mediaportal.faces_api.application.dto;

public class TrainDTO {
    private String jobId;
    private int type;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String job_id) {
        this.jobId = job_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
