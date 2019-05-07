package com.atlassian.asap.client;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AsapRequest<T> {

    private T body;
    private Map<String, String> headers = new LinkedHashMap<>();
    private Map<String, String> customClaims = new LinkedHashMap<>();
    private Map<String, String> pathParams = new LinkedHashMap<>();
    private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    private Map<String, JwtClaimsHolder> jwtHeaders = new LinkedHashMap<>();
    private String url;
    private String path;

    public AsapRequest body(T requestBody) {
        this.body = requestBody;
        return this;
    }

    public AsapRequest<T> withUrl(String url) {
        this.url = url;
        return this;
    }

    public AsapRequest<T> withPath(String path) {
        this.path = path;
        return this;
    }

    public AsapRequest<T> withQueryParams(MultiValueMap<String, String> queryParams) {
        this.queryParams = new LinkedMultiValueMap<>();
        this.queryParams.putAll(queryParams);
        return this;
    }

    public AsapRequest<T> withPathParams(Map<String, String> pathParams) {
        this.pathParams = new LinkedHashMap<>();
        this.pathParams.putAll(pathParams);
        return this;
    }

    public AsapRequest<T> withHeaders(Map<String, String> headers) {
        this.headers = new LinkedHashMap<>();
        this.headers.putAll(headers);
        return this;
    }


    public AsapRequest<T> withCustomClaims(Map<String, String> claims) {
        this.customClaims = new LinkedHashMap<>();
        this.customClaims.putAll(claims);
        return this;
    }

    public AsapRequest<T> addQueryParam(String key, String value) {
        queryParams.add(key, value);
        return this;
    }

    public AsapRequest<T> addPathParam(String key, String value) {
        pathParams.put(key, value);
        return this;
    }

    public AsapRequest<T> addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public AsapRequest<T> addCustomClaim(String key, String value) {
        customClaims.put(key, value);
        return this;
    }

    public AsapRequest<T> addJwtHeader(String headerName, JwtClaimsHolder jwt) {
        jwtHeaders.put(headerName, jwt);
        return this;
    }

    public Map<String, JwtClaimsHolder> getJwtHeaders() {
        return jwtHeaders;
    }

    public AsapRequest<T> setJwtHeaders(Map<String, JwtClaimsHolder> jwtHeaders) {
        this.jwtHeaders = jwtHeaders;
        return this;
    }

    public T getBody() {
        return body;
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    public String getPath() {
        return path;
    }


    public MultiValueMap<String, String> getQueryParams() {
        return new LinkedMultiValueMap<>(this.queryParams);
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public Map<String, String> getHeaders() {
        return new LinkedHashMap<>(this.headers);
    }

    public Map<String, String> getCustomClaims() {
        return new LinkedHashMap<>(this.customClaims);
    }
}
