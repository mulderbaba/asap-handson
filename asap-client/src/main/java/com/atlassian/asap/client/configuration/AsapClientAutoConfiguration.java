package com.atlassian.asap.client.configuration;

import com.atlassian.asap.core.keys.publickey.S3ServiceUnavailableRetryStrategy;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class AsapClientAutoConfiguration {

    @Autowired
    private AsapClientHttpClientProperties clientProperties;

    @Bean
    @Lazy
    @ConditionalOnMissingBean(name = "asapClientHttpClient")
    public HttpClient asapClientHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(clientProperties.getPoolMaxConnectionCount());
        connectionManager.setMaxTotal(clientProperties.getPoolMaxConnectionCount());

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(((int) TimeUnit.SECONDS.toMillis(clientProperties.getConnectionRequestTimeoutSeconds())))
                .setConnectTimeout(((int) TimeUnit.SECONDS.toMillis(clientProperties.getConnectTimeoutSeconds())))
                .setSocketTimeout((int) TimeUnit.SECONDS.toMillis(clientProperties.getSocketTimeoutSeconds()));

        CacheConfig cacheConfig = CacheConfig.custom()
                .setMaxCacheEntries(clientProperties.getMaxCacheEntries())
                .setMaxObjectSize(2048)
                .setHeuristicCachingEnabled(false)
                .setSharedCache(clientProperties.isSharedCache())
                .setAsynchronousWorkersMax(clientProperties.getMaxAsyncWorkers())
                .build();

        CachingHttpClientBuilder clientBuilder = (CachingHttpClientBuilder) CachingHttpClients.custom()
                .setRetryHandler(new StandardHttpRequestRetryHandler(clientProperties.getRetryCount(), clientProperties.isRequestSentRetryEnabled()));


        clientBuilder = (CachingHttpClientBuilder) clientBuilder
                .setCacheConfig(cacheConfig)
                .setDefaultRequestConfig(requestConfigBuilder.build())
                .setConnectionManager(connectionManager)
                .useSystemProperties()
                .setRedirectStrategy(DefaultRedirectStrategy.INSTANCE)
                .setServiceUnavailableRetryStrategy(new S3ServiceUnavailableRetryStrategy(2, 100));

        if (clientProperties.isProxyEnabled()) {
            HttpHost proxyHost = new HttpHost(clientProperties.getProxyHost(), clientProperties.getProxyPort(), clientProperties.getProxySchema());
            clientBuilder.setProxy(proxyHost)
                    .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        }

        return clientBuilder.build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "asapRestTemplate")
    public RestTemplate asapRestTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(asapClientHttpClient()));
    }
}