package com.atlassian.asap.client.configuration;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class AsapClientProperties {

    private String issuer;

    private String keyId;

    private String audience;

    private String subject;

    private String privateKeyDataUri;

    private String baseUrl;


    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Optional<String> getSubject() {
        if (StringUtils.isBlank(this.subject)) {
            return Optional.empty();
        }
        return Optional.of(subject);
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPrivateKeyDataUri() {
        return privateKeyDataUri;
    }

    public void setPrivateKeyDataUri(String privateKeyDataUri) {
        this.privateKeyDataUri = privateKeyDataUri;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static class Builder {

        private String issuer;

        private String keyId;

        private String audience;

        private String subject;

        private String privateKeyDataUri;

        private String baseUrl;


        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }

        public Builder audience(String audience) {
            this.audience = audience;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder privateKeyDataUri(String privateKeyDataUri) {
            this.privateKeyDataUri = privateKeyDataUri;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public AsapClientProperties build() {
            AsapClientProperties asapClientProperties = new AsapClientProperties();
            asapClientProperties.setSubject(this.subject);
            asapClientProperties.setKeyId(this.keyId);
            asapClientProperties.setIssuer(this.issuer);
            asapClientProperties.setBaseUrl(this.baseUrl);
            asapClientProperties.setAudience(this.audience);
            asapClientProperties.setPrivateKeyDataUri(this.privateKeyDataUri);

            return asapClientProperties;
        }
    }
}