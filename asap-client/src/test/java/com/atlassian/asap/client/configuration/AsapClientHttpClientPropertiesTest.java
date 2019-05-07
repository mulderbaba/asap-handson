package com.atlassian.asap.client.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AsapClientHttpClientPropertiesTest.class})
@TestPropertySource(properties = {
        "asap.client.http.client.requestSentRetryEnabled=true",
        "asap.client.http.client.poolMaxConnectionCount=100",
        "asap.client.http.client.connectionRequestTimeoutSeconds=100",
        "asap.client.http.client.connectTimeoutSeconds=100",
        "asap.client.http.client.socketTimeoutSeconds=100",
        "asap.client.http.client.maxCacheEntries=100",
        "asap.client.http.client.sharedCache=true",
        "asap.client.http.client.maxAsyncWorkers=100",
        "asap.client.http.client.retryCount=100",
        "asap.client.http.client.proxyEnabled=true",
        "asap.client.http.client.proxyHost=proxy.atlassian"
})
@EnableConfigurationProperties(AsapClientHttpClientProperties.class)
public class AsapClientHttpClientPropertiesTest {

    @Autowired
    private AsapClientHttpClientProperties httpClientProperties;

    @Test
    public void httpClientPropertiesSetSuccessfully() {
        assertTrue(httpClientProperties.isRequestSentRetryEnabled());
        assertEquals(100, httpClientProperties.getPoolMaxConnectionCount());
        assertEquals(100, httpClientProperties.getConnectionRequestTimeoutSeconds());
        assertEquals(100, httpClientProperties.getConnectTimeoutSeconds());
        assertEquals(100, httpClientProperties.getSocketTimeoutSeconds());
        assertEquals(100, httpClientProperties.getMaxCacheEntries());
        assertTrue(httpClientProperties.isSharedCache());
        assertEquals(100, httpClientProperties.getMaxAsyncWorkers());
        assertEquals(100, httpClientProperties.getRetryCount());
        assertTrue(httpClientProperties.isProxyEnabled());
        assertEquals("proxy.atlassian", httpClientProperties.getProxyHost());
    }
}