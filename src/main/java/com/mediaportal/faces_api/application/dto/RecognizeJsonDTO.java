package com.mediaportal.faces_api.application.dto;

import com.google.gson.annotations.SerializedName;

public class RecognizeJsonDTO {

    @SerializedName("class")
    private String classe;

    private double score;
    private String folder;
    private String file;

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
