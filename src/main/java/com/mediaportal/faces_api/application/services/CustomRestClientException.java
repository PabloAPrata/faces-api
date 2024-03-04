package com.mediaportal.faces_api.application.services;

public class CustomRestClientException extends Throwable {
    public CustomRestClientException(int rawStatusCode, String statusText, String responseBodyAsString) {
    }
}
