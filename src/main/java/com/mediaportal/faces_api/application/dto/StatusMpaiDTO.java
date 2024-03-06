package com.mediaportal.faces_api.application.dto;

public class StatusMpaiDTO {
    private Boolean success;
    private String status;

    public StatusMpaiDTO(Boolean success, String status) {
        this.success = success;
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
