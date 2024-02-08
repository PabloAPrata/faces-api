package com.mediaportal.faces_api.application.dto;

public class PostTrainingMPAIDTO {
    private String folder;
    private String model;

    public PostTrainingMPAIDTO(String folder, String model) {
        this.folder = folder;
        this.model = model;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
