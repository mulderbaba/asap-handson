package com.atlassian.asap.client;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AsapClientException extends Exception {

    private int statusCode;
    private String body;
    private MultiValueMap<String, String> headers;

    public AsapClientException(String message) {
        super(message);
    }

    AsapClientException(Exception e) {
        super(e);
    }

    public AsapClientException(Throwable cause, int statusCode, String body, MultiValueMap<String, String> headers) {
        super(cause);
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public MultiValueMap<String, String> getHeaders() {
        return new LinkedMultiValueMap<>(headers);
    }
}
