package com.atlassian.asap.server.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("asap.server.http.client")
public class AsapServerHttpClientProperties {

    private boolean requestSentRetryEnabled = false;

    private int poolMaxConnectionCount = 20;

    private int connectionRequestTimeoutSeconds = 5;

    private int connectTimeoutSeconds = 5;

    private int socketTimeoutSeconds = 10;

    private int maxCacheEntries = 128;

    private boolean sharedCache = false;

    private int maxAsyncWorkers = 2;

    private int retryCount = 3;

    private boolean proxyEnabled = false;

    private String proxyHost;

    private String proxySchema = "http";

    private int proxyPort = 8080;

    public int getPoolMaxConnectionCount() {
        return poolMaxConnectionCount;
    }

    public void setPoolMaxConnectionCount(int poolMaxConnectionCount) {
        this.poolMaxConnectionCount = poolMaxConnectionCount;
    }

    public int getConnectionRequestTimeoutSeconds() {
        return connectionRequestTimeoutSeconds;
    }

    public void setConnectionRequestTimeoutSeconds(int connectionRequestTimeoutSeconds) {
        this.connectionRequestTimeoutSeconds = connectionRequestTimeoutSeconds;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public int getSocketTimeoutSeconds() {
        return socketTimeoutSeconds;
    }

    public void setSocketTimeoutSeconds(int socketTimeoutSeconds) {
        this.socketTimeoutSeconds = socketTimeoutSeconds;
    }

    public int getMaxCacheEntries() {
        return maxCacheEntries;
    }

    public void setMaxCacheEntries(int maxCacheEntries) {
        this.maxCacheEntries = maxCacheEntries;
    }

    public boolean isSharedCache() {
        return sharedCache;
    }

    public void setSharedCache(boolean sharedCache) {
        this.sharedCache = sharedCache;
    }

    public int getMaxAsyncWorkers() {
        return maxAsyncWorkers;
    }

    public void setMaxAsyncWorkers(int maxAsyncWorkers) {
        this.maxAsyncWorkers = maxAsyncWorkers;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isRequestSentRetryEnabled() {
        return requestSentRetryEnabled;
    }

    public void setRequestSentRetryEnabled(boolean requestSentRetryEnabled) {
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxySchema() {
        return proxySchema;
    }

    public void setProxySchema(String proxySchema) {
        this.proxySchema = proxySchema;
    }

    public static class Builder {

        private boolean requestSentRetryEnabled = false;

        private int poolMaxConnectionCount = 20;

        private int connectionRequestTimeoutSeconds = 5;

        private int connectTimeoutSeconds = 5;

        private int socketTimeoutSeconds = 10;

        private int maxCacheEntries = 128;

        private boolean sharedCache = false;

        private int maxAsyncWorkers = 2;

        private int retryCount = 3;

        private boolean proxyEnabled = false;

        private String proxyHost;

        private String proxySchema = "http";

        private int proxyPort = 8080;


        public Builder requestSentRetryEnabled(boolean requestSentRetryEnabled) {
            this.requestSentRetryEnabled = requestSentRetryEnabled;
            return this;
        }

        public Builder poolMaxConnectionCount(int poolMaxConnectionCount) {
            this.poolMaxConnectionCount = poolMaxConnectionCount;
            return this;
        }

        public Builder connectionRequestTimeoutSeconds(int connectionRequestTimeoutSeconds) {
            this.connectionRequestTimeoutSeconds = connectionRequestTimeoutSeconds;
            return this;
        }

        public Builder connectTimeoutSeconds(int connectTimeoutSeconds) {
            this.connectTimeoutSeconds = connectTimeoutSeconds;
            return this;
        }

        public Builder socketTimeoutSeconds(int socketTimeoutSeconds) {
            this.socketTimeoutSeconds = socketTimeoutSeconds;
            return this;
        }

        public Builder maxCacheEntries(int maxCacheEntries) {
            this.maxCacheEntries = maxCacheEntries;
            return this;
        }

        public Builder sharedCache(boolean sharedCache) {
            this.sharedCache = sharedCache;
            return this;
        }

        public Builder maxAsyncWorkers(int maxAsyncWorkers) {
            this.maxAsyncWorkers = maxAsyncWorkers;
            return this;
        }

        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder proxyEnabled(boolean proxyEnabled) {
            this.proxyEnabled = proxyEnabled;
            return this;
        }

        public Builder proxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
            return this;
        }

        public Builder proxySchema(String proxySchema) {
            this.proxySchema = proxySchema;
            return this;
        }

        public Builder proxyPort(int proxyPort) {
            this.proxyPort = proxyPort;
            return this;
        }

        public AsapServerHttpClientProperties build() {
            AsapServerHttpClientProperties httpClientProperties = new AsapServerHttpClientProperties();

            httpClientProperties.setRequestSentRetryEnabled(requestSentRetryEnabled);
            httpClientProperties.setPoolMaxConnectionCount(poolMaxConnectionCount);
            httpClientProperties.setConnectionRequestTimeoutSeconds(connectionRequestTimeoutSeconds);
            httpClientProperties.setConnectTimeoutSeconds(connectTimeoutSeconds);
            httpClientProperties.setSocketTimeoutSeconds(socketTimeoutSeconds);
            httpClientProperties.setMaxCacheEntries(maxCacheEntries);
            httpClientProperties.setSharedCache(sharedCache);
            httpClientProperties.setMaxAsyncWorkers(maxAsyncWorkers);
            httpClientProperties.setRetryCount(retryCount);
            httpClientProperties.setProxyEnabled(proxyEnabled);
            httpClientProperties.setProxyHost(proxyHost);
            httpClientProperties.setProxySchema(proxySchema);
            httpClientProperties.setProxyPort(proxyPort);

            return httpClientProperties;
        }
    }
}
