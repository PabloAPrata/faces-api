package com.mediaportal.faces_api.application.utils;


import com.mediaportal.faces_api.application.dto.ClientActivateJobDTO;

import java.io.IOException;
import java.util.List;

public interface ApiUtilsInterface {

    List<String> getFileNamesFromJson() throws IOException;

    void createFolder(String directory, String nameFolder);

    void persistEventInDatabase(ClientActivateJobDTO responseMPAI);

    List<String> getSchemaFilesFromDatabase();
}
