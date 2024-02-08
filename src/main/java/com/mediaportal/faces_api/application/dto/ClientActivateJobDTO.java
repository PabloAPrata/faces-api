package com.mediaportal.faces_api.application.dto;

public class ClientActivateJobDTO {
    private String success;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
