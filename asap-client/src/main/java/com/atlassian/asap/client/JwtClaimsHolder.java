package com.atlassian.asap.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JwtClaimsHolder {

    private String issuer;

    private List<String> audience;

    private String subject;

    private Map<String, String> customClaims = new LinkedHashMap<>();

    public String getIssuer() {
        return issuer;
    }

    public JwtClaimsHolder setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public List<String> getAudience() {
        return audience;
    }

    public JwtClaimsHolder setAudience(List<String> audience) {
        this.audience = audience;
        return this;
    }

    public JwtClaimsHolder addAudience(String audience) {
        this.audience.add(audience);
        return this;
    }

    public Optional<String> getSubject() {
        return Optional.ofNullable(subject);
    }

    public JwtClaimsHolder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Map<String, String> getCustomClaims() {
        return customClaims;
    }

    public JwtClaimsHolder setCustomClaims(Map<String, String> customClaims) {
        this.customClaims = customClaims;
        return this;
    }

    public JwtClaimsHolder addCustomClaim(String key, String value) {
        this.customClaims.put(key, value);
        return this;
    }
}
