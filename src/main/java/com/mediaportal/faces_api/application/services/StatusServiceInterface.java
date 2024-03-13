package com.mediaportal.faces_api.application.services;

import com.mediaportal.faces_api.application.dto.ApiResponseDTO;
import com.mediaportal.faces_api.application.dto.StatusMpaiDTO;
import org.springframework.web.client.HttpClientErrorException;

public interface StatusServiceInterface {
    ApiResponseDTO checkStatusJob(String job_id);

    StatusMpaiDTO requestStatusJob(String job_id) throws HttpClientErrorException;

}
