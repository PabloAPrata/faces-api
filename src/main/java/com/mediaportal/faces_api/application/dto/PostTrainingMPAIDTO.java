package com.mediaportal.faces_api.application.dto;

import java.util.ArrayList;
import java.util.List;

public class PostTrainingMPAIDTO {
    private String folder;
    private String model;
    private Boolean extract;
    private Boolean data_augmentation;
    private Double test_split;
    private List<Float> C;
    private List<String> kernel;
    private Double threshold_min;

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

    public Boolean getExtract() {
        return extract;
    }

    public void setExtract(Boolean extract) {
        this.extract = extract;
    }

    public Boolean getData_augmentation() {
        return data_augmentation;
    }

    public void setData_augmentation(Boolean data_augmentation) {
        this.data_augmentation = data_augmentation;
    }

    public Double getTest_split() {
        return test_split;
    }

    public void setTest_split(Double test_split) {
        this.test_split = test_split;
    }

    public List<Float> getC() {
        return C;
    }

    public void setC(List<Float> c) {
        C = c;
    }

    public List<String> getKernel() {
        return kernel;
    }

    public void setKernel(List<String> kernel) {
        this.kernel = kernel;
    }

    public Double getThreshold_min() {
        return threshold_min;
    }

    public void setThreshold_min(Double threshold_min) {
        this.threshold_min = threshold_min;
    }
}
