package com.mediaportal.faces_api.application.dto;

public class PostGroupDTO {
    private String folder;
    private int minsize;

    public PostGroupDTO(String folder, int minsize) {
        this.folder = folder;
        this.minsize = minsize;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getMinsize() {
        return minsize;
    }

    public void setMinsize(int minsize) {
        this.minsize = minsize;
    }
}
