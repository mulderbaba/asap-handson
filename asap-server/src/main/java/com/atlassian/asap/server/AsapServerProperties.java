package com.atlassian.asap.server;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Validated
@Component
@ConfigurationProperties("asap.server")
public class AsapServerProperties {

    @NotNull
    private String audience;

    @NotNull
    private String publicKeyRepositoryUrl;

    @NotNull
    private String publicKeyFallbackRepositoryUrl;

    private Set<String> authorizedIssuers;

    private Set<String> authorizedSubjects;

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getPublicKeyServerUrl() {
        return Stream.of(getPublicKeyRepositoryUrl(), getPublicKeyFallbackRepositoryUrl())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" | "));
    }

    public String getPublicKeyRepositoryUrl() {
        return publicKeyRepositoryUrl;
    }

    public void setPublicKeyRepositoryUrl(String publicKeyRepositoryUrl) {
        this.publicKeyRepositoryUrl = publicKeyRepositoryUrl;
    }

    public String getPublicKeyFallbackRepositoryUrl() {
        return publicKeyFallbackRepositoryUrl;
    }

    public void setPublicKeyFallbackRepositoryUrl(String publicKeyFallbackRepositoryUrl) {
        this.publicKeyFallbackRepositoryUrl = publicKeyFallbackRepositoryUrl;
    }

    public Set<String> getAuthorizedIssuers() {
        return authorizedIssuers;
    }

    public void setAuthorizedIssuers(Set<String> authorizedIssuers) {
        this.authorizedIssuers = authorizedIssuers;
    }

    public Set<String> getAuthorizedSubjects() {
        return authorizedSubjects;
    }

    public void setAuthorizedSubjects(Set<String> authorizedSubjects) {
        this.authorizedSubjects = authorizedSubjects;
    }
}
