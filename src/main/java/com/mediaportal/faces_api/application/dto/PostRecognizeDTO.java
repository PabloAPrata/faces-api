package com.mediaportal.faces_api.application.dto;

public class PostRecognizeDTO {
    private String folder;
    private String model;

    public PostRecognizeDTO(String folder, String model) {
        this.folder = folder;
        this.model = model;
    }
}
