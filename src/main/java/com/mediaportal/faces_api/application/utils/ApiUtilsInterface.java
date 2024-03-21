package com.mediaportal.faces_api.application.utils;

import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

public interface ApiUtilsInterface {

    List<String> getFileNamesFromJson(String endpoint) throws IOException;

    void createAuxiliaryFolder(String directory, String nameFolder);

    void persistEventInDatabase(ClientActivateJobDTO responseMPAI, int type) throws IOException;

    void generateAuxiliaryFolder(String nameTrainingFolder, Boolean bringOnlyUnknown) throws IOException;

    void copyFilesToAuxiliaryFolder(String nameTrainingFolder, Boolean bringOnlyUnknown) throws IOException;

    List<String> extractFileNamesFromJson(String json);

    List<String> getSchemaFilesFromDatabase() throws Exception;

    void changeDatabaseStatus(String jobId, int status) throws RestClientException;

    int getStatusNumberByName(String statusName);

    void deleteAuxiliaryFolder(int jobType);
}
