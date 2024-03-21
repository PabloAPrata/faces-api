package com.mediaportal.faces_api.application.dto;

import java.util.List;


public class UpdateFoldersDTO {
    private List<String> groupsList;
    private String jobId;

    public UpdateFoldersDTO(List<String> groupsList, String jobId) {
        this.groupsList = groupsList;
        this.jobId = jobId;
    }

    public List<String> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(List<String> groupsList) {
        this.groupsList = groupsList;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
