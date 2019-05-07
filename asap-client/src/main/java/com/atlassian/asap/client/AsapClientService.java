package com.atlassian.asap.client;

import com.atlassian.asap.api.Jwt;
import com.atlassian.asap.api.JwtBuilder;
import com.atlassian.asap.api.client.http.AuthorizationHeaderGenerator;
import com.atlassian.asap.api.exception.CannotRetrieveKeyException;
import com.atlassian.asap.api.exception.InvalidTokenException;
import com.atlassian.asap.client.configuration.AsapClientHttpClientProperties;
import com.atlassian.asap.client.configuration.AsapClientProperties;
import com.atlassian.asap.core.client.http.AuthorizationHeaderGeneratorImpl;
import com.atlassian.asap.core.exception.InvalidHeaderException;
import com.atlassian.asap.core.exception.UnsupportedAlgorithmException;
import com.atlassian.asap.core.keys.KeyProvider;
import com.atlassian.asap.core.keys.privatekey.PrivateKeyProviderFactory;
import com.atlassian.asap.core.serializer.Json;
import com.atlassian.asap.core.validator.ValidatedKeyId;
import com.atlassian.asap.nimbus.serializer.NimbusJwtSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.json.JsonObjectBuilder;
import java.net.URI;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class AsapClientService {

    private AuthorizationHeaderGenerator authorizationHeaderGenerator;

    @Autowired
    @Qualifier("asapRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    AsapClientHttpClientProperties httpClientProperties;

    private AsapClientProperties clientProperties;
    private KeyProvider<PrivateKey> privateKeyProvider;
    private NimbusJwtSerializer jwtSerializer;

    protected AsapClientService(AsapClientProperties asapClientProperties) {
        this.clientProperties = asapClientProperties;
        setAuthorizationHeaderGenerator();
    }

    protected AsapClientService(AsapClientProperties asapClientProperties, AsapClientHttpClientProperties httpClientProperties) {
        this.clientProperties = asapClientProperties;
        this.httpClientProperties = httpClientProperties;
        setAuthorizationHeaderGenerator();
    }

    protected AsapClientService(AsapClientProperties asapClientProperties, RestTemplate restTemplate) {
        this.clientProperties = asapClientProperties;
        this.restTemplate = restTemplate;
        setAuthorizationHeaderGenerator();
    }

    private void setAuthorizationHeaderGenerator() {
        String privateKey = clientProperties.getPrivateKeyDataUri();
        if (privateKey != null) {
            URI privateKeyDataUri = URI.create(privateKey);
            if (StringUtils.isNotBlank(privateKeyDataUri.toString())) {
                this.privateKeyProvider = PrivateKeyProviderFactory.createPrivateKeyProvider(privateKeyDataUri);
                this.jwtSerializer = new NimbusJwtSerializer();
                this.authorizationHeaderGenerator = new AuthorizationHeaderGeneratorImpl(jwtSerializer, privateKeyProvider);
            }
        }
    }

    private <T> AsapResponse<T> doCall(AsapRequest<?> request, HttpMethod method, ParameterizedTypeReference<T> typeReference, Class<T> responseBodyType) throws AsapClientException {
        HttpHeaders headers = populateHttpHeaders(request);
        HttpEntity<?> entity = populateHttpEntity(request, method, headers);
        UriComponents uriComponents = buildUri(request);

        ResponseEntity<T> response;
        try {
            if (typeReference != null) {
                response = restTemplate.exchange(uriComponents.toString(), method, entity, typeReference);
            } else {
                response = restTemplate.exchange(uriComponents.toString(), method, entity, responseBodyType);
            }
        } catch (RestClientException ex) {

            if (ex instanceof HttpStatusCodeException) {
                final HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException) ex;
                int statusCode = httpStatusCodeException.getStatusCode().value();

                throw new AsapClientException(ex,
                        statusCode,
                        httpStatusCodeException.getResponseBodyAsString(),
                        httpStatusCodeException.getResponseHeaders());
            }

            throw new AsapClientException(ex);
        }

        return new AsapResponse<T>()
                .setBody(response.getBody())
                .setStatusCode(response.getStatusCode().value())
                .setResponseHeaders(response.getHeaders());
    }

    private HttpEntity<?> populateHttpEntity(AsapRequest<?> request, HttpMethod method, HttpHeaders headers) {
        HttpEntity<?> entity;
        if (hasBody(method)) {
            entity = new HttpEntity<Object>(request.getBody(), headers);
        } else {
            entity = new HttpEntity<>(headers);
        }
        return entity;
    }

    private UriComponents buildUri(AsapRequest<?> request) {
        UriComponents uriComponents;
        String urlTemplate = request.getUrl().orElse(clientProperties.getBaseUrl() + request.getPath());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlTemplate);

        if (!request.getQueryParams().isEmpty()) {
            builder.queryParams(request.getQueryParams());
        }

        if (!request.getPathParams().isEmpty()) {
            uriComponents = builder.buildAndExpand(request.getPathParams());
        } else {
            uriComponents = builder.build();
        }
        return uriComponents;
    }

    private HttpHeaders populateHttpHeaders(AsapRequest<?> request) throws AsapClientException {
        String authorizationToken;
        final Jwt jwt = newJwtToken(request.getCustomClaims());
        authorizationToken = convertJwtToString(jwt);

        HttpHeaders headers = new HttpHeaders();
        request.getHeaders().forEach(headers::add);
        headers.set(HttpHeaders.AUTHORIZATION, authorizationToken);

        if (!request.getJwtHeaders().isEmpty()) {
            for (Map.Entry<String, JwtClaimsHolder> entry : request.getJwtHeaders().entrySet()) {
                String k = entry.getKey();
                JwtClaimsHolder v = entry.getValue();
                headers.add(k, convertJwtHeader(v, clientProperties));
            }
        }
        return headers;
    }

    private String convertJwtToString(Jwt jwt) throws AsapClientException {
        String authorizationToken;
        try {
            authorizationToken = getAuthorizationHeaderGenerator().generateAuthorizationHeader(jwt);
        } catch (InvalidTokenException | CannotRetrieveKeyException e) {
            throw new AsapClientException(e);
        }
        return authorizationToken;
    }

    private AuthorizationHeaderGenerator getAuthorizationHeaderGenerator() throws AsapClientException {
        if (authorizationHeaderGenerator == null) {
            throw new AsapClientException("Authorization header cannot be generated!. Probably private key cannot be read as dataUri.");
        }
        return authorizationHeaderGenerator;
    }

    private KeyProvider<PrivateKey> getPrivateKeyProvider() throws AsapClientException {
        if (privateKeyProvider == null) {
            throw new AsapClientException("Authorization header cannot be generated!. Probably private key cannot be read as dataUri.");
        }
        return privateKeyProvider;
    }

    private NimbusJwtSerializer getJwtSerializer() throws AsapClientException {
        if (jwtSerializer == null) {
            throw new AsapClientException("Authorization header cannot be generated!. Probably private key cannot be read as dataUri.");
        }
        return jwtSerializer;
    }

    private String convertJwtHeader(JwtClaimsHolder asapJwt, AsapClientProperties clientProperties) throws AsapClientException {
        Instant now = Instant.now();

        final JsonObjectBuilder claimsBuilder = Json.provider().createObjectBuilder();
        asapJwt.getCustomClaims().forEach(claimsBuilder::add);

        final Jwt jwt = JwtBuilder.newJwt()
                .keyId(clientProperties.getKeyId())
                .issuer(asapJwt.getIssuer())
                .audience(asapJwt.getAudience())
                .subject(asapJwt.getSubject())
                .customClaims(claimsBuilder.build())
                .jwtId(UUID.randomUUID().toString())
                .notBefore(Optional.of(now))
                .issuedAt(now)
                .expirationTime(now.plus(JwtBuilder.DEFAULT_LIFETIME))
                .build();
        return convertCustomClaimsToJwtString(jwt);
    }

    private String convertCustomClaimsToJwtString(Jwt jwt) throws AsapClientException {
        try {
            ValidatedKeyId validatedKeyId = ValidatedKeyId.validate(jwt.getHeader().getKeyId());
            PrivateKey privateKey = getPrivateKeyProvider().getKey(validatedKeyId);

            return getJwtSerializer().serialize(jwt, privateKey);
        }
        catch (InvalidHeaderException | CannotRetrieveKeyException | UnsupportedAlgorithmException e) {
            throw new AsapClientException(e);
        }
    }

    private boolean hasBody(HttpMethod method) {
        if (method.equals(HttpMethod.GET)
                || method.equals(HttpMethod.DELETE)
                || method.equals(HttpMethod.OPTIONS)
                || method.equals(HttpMethod.HEAD)) {
            return false;
        }
        return true;
    }

    private Jwt baseJwt() {
        return JwtBuilder.newJwt()
                .keyId(clientProperties.getKeyId())
                .subject(clientProperties.getSubject())
                .audience(clientProperties.getAudience())
                .issuer(clientProperties.getIssuer())
                .build();
    }

    private Jwt newJwtToken(Map<String, String> claims) {
        Instant now = Instant.now();

        final JsonObjectBuilder claimsBuilder = Json.provider().createObjectBuilder();
        claims.forEach(claimsBuilder::add);

        return JwtBuilder.copyJwt(baseJwt())
                .notBefore(Optional.of(now))
                .issuedAt(now)
                .customClaims(claimsBuilder.build())
                .expirationTime(now.plus(JwtBuilder.DEFAULT_LIFETIME))
                .jwtId(UUID.randomUUID().toString())
                .build();
    }

    protected <T> AsapResponse<T> doGet(AsapRequest request, Class<T> responseBodyType) throws AsapClientException {
        return doCall(request, HttpMethod.GET, null, responseBodyType);
    }

    protected <T> AsapResponse<T> doGet(AsapRequest request, ParameterizedTypeReference<T> typeReference) throws AsapClientException {
        return doCall(request, HttpMethod.GET, typeReference, null);
    }

    protected <T> AsapResponse<T> doPost(AsapRequest<?> request, Class<T> responseBodyType) throws AsapClientException {
        return doCall(request, HttpMethod.POST, null, responseBodyType);
    }

    protected <T> AsapResponse<T> doPost(AsapRequest<?> request, ParameterizedTypeReference<T> typeReference) throws AsapClientException {
        return doCall(request, HttpMethod.POST, typeReference, null);
    }

    protected <T> AsapResponse<T> doDelete(AsapRequest<?> request, Class<T> responseBodyType) throws AsapClientException {
        return doCall(request, HttpMethod.DELETE, null, responseBodyType);
    }

    protected <T> AsapResponse<T> doDelete(AsapRequest<?> request, ParameterizedTypeReference<T> typeReference) throws AsapClientException {
        return doCall(request, HttpMethod.DELETE, typeReference, null);
    }

    protected <T> AsapResponse<T> doPut(AsapRequest<?> request, Class<T> responseBodyType) throws AsapClientException {
        return doCall(request, HttpMethod.PUT, null, responseBodyType);
    }

    protected <T> AsapResponse<T> doPut(AsapRequest<?> request, ParameterizedTypeReference<T> typeReference) throws AsapClientException {
        return doCall(request, HttpMethod.PUT, typeReference, null);
    }

    protected <T> AsapResponse<T> doPatch(AsapRequest<?> request, Class<T> responseBodyType) throws AsapClientException {
        return doCall(request, HttpMethod.PATCH, null, responseBodyType);
    }

    protected <T> AsapResponse<T> doPatch(AsapRequest<?> request, ParameterizedTypeReference<T> typeReference) throws AsapClientException {
        return doCall(request, HttpMethod.PATCH, typeReference, null);
    }

    protected <T> AsapResponse<T> doHead(AsapRequest<?> request, Class<T> responseBodyType) throws AsapClientException {
        return doCall(request, HttpMethod.HEAD, null, responseBodyType);
    }

    protected <T> AsapResponse<T> doHead(AsapRequest<?> request, ParameterizedTypeReference<T> typeReference) throws AsapClientException {
        return doCall(request, HttpMethod.HEAD, typeReference, null);
    }
}
