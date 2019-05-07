package com.atlassian.asap.client;

import org.springframework.util.MultiValueMap;

public class AsapResponse<T> {

    private T body;
    private int statusCode;
    private MultiValueMap<String, String> responseHeaders;

    public T getBody() {
        return body;
    }

    AsapResponse<T> setBody(T body) {
        this.body = body;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public AsapResponse<T> setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public MultiValueMap<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public AsapResponse<T> setResponseHeaders(MultiValueMap<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
        return this;
    }
}
