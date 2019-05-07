package com.atlassian.asap.server.configuration;

import com.atlassian.asap.api.server.http.RequestAuthenticator;
import com.atlassian.asap.core.keys.KeyProvider;
import com.atlassian.asap.core.keys.PemReader;
import com.atlassian.asap.core.keys.publickey.PublicKeyProviderFactory;
import com.atlassian.asap.core.keys.publickey.S3ServiceUnavailableRetryStrategy;
import com.atlassian.asap.core.parser.JwtParser;
import com.atlassian.asap.core.server.http.RequestAuthenticatorImpl;
import com.atlassian.asap.core.server.interceptor.AsapValidator;
import com.atlassian.asap.core.validator.JwtClaimsValidator;
import com.atlassian.asap.core.validator.JwtValidator;
import com.atlassian.asap.core.validator.JwtValidatorImpl;
import com.atlassian.asap.nimbus.parser.NimbusJwtParser;
import com.atlassian.asap.server.AsapAuthenticationInterceptor;
import com.atlassian.asap.server.AsapServerProperties;
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

import java.security.PublicKey;
import java.time.Clock;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsapServerAutoConfiguration {

    @Autowired
    private AsapServerProperties asapServerProperties;

    @Autowired
    private AsapServerHttpClientProperties clientProperties;

    @Bean
    @ConditionalOnMissingBean(AsapValidator.class)
    public AsapValidator asapValidator(AsapServerProperties asapServerProperties) {
        Set<String> authorizedSubjects = asapServerProperties.getAuthorizedSubjects();
        Set<String> authorizedIssuers = asapServerProperties.getAuthorizedIssuers();
        return AsapValidator.newAnnotationWithConfigValidator(
                authorizedSubjects != null ? authorizedSubjects : Collections.emptySet(),
                authorizedIssuers != null ? authorizedIssuers : Collections.emptySet());
    }

    @Bean
    @ConditionalOnMissingBean(JwtValidator.class)
    public JwtValidator jwtValidator(KeyProvider<PublicKey> publicKeyProvider,
                                     JwtParser jwtParser,
                                     JwtClaimsValidator jwtClaimsValidator) {
        return new JwtValidatorImpl(publicKeyProvider, jwtParser, jwtClaimsValidator, getAllAudiences());
    }

    private Set<String> getAllAudiences() {
        return Collections.singleton(asapServerProperties.getAudience());
    }

    @Bean
    @ConditionalOnMissingBean(JwtClaimsValidator.class)
    public JwtClaimsValidator jwtClaimsValidator() {
        return new JwtClaimsValidator(Clock.systemUTC());
    }

    @Bean
    @ConditionalOnMissingBean(JwtParser.class)
    public JwtParser jwtParser() {
        return new NimbusJwtParser();
    }

    @Bean
    @ConditionalOnMissingBean(KeyProvider.class)
    public KeyProvider<PublicKey> publicKeyProvider(AsapServerProperties asapServerProperties) {
        return new PublicKeyProviderFactory(asapServerHttpClient(), new PemReader()).createPublicKeyProvider(asapServerProperties.getPublicKeyRepositoryUrl());
    }

    @Bean
    @ConditionalOnMissingBean(RequestAuthenticator.class)
    public RequestAuthenticator requestAuthenticator(JwtValidator jwtValidator) {
        return new RequestAuthenticatorImpl(jwtValidator);
    }

    @Bean
    @ConditionalOnMissingBean(AsapAuthenticationInterceptor.class)
    public AsapAuthenticationInterceptor authorizationInterceptor() {
        return new AsapAuthenticationInterceptor();
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean(name = "asapServerHttpClient")
    public HttpClient asapServerHttpClient() {
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
}
