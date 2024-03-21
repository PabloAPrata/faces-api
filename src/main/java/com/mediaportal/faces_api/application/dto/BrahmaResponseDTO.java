package com.mediaportal.faces_api.application.dto;

import java.util.List;

public class BrahmaResponseDTO {
    private int status;
    private List<String> data;
    private String message;

    public BrahmaResponseDTO() {}

    public BrahmaResponseDTO(int status, List<String> data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public List<String> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
