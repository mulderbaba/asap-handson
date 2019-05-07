package com.atlassian.asap.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AsapServerPropertiesTest.class})
@TestPropertySource(properties = {
        "asap.server.audience=audience1",
        "asap.server.publicKeyRepositoryUrl=https://publicKeyRepositoryUrl",
        "asap.server.publicKeyFallbackRepositoryUrl=https://publicKeyFallbackRepositoryUrl",
        "asap.server.authorizedIssuers=issuer1,issuer2",
        "asap.server.authorizedSubjects=subject1,subject2"
})
@EnableConfigurationProperties(AsapServerProperties.class)
public class AsapServerPropertiesTest {

    @Autowired
    private AsapServerProperties asapServerProperties;

    @Test
    public void asapServerProperties() {
        assertEquals("audience1", asapServerProperties.getAudience());
        assertEquals("https://publicKeyRepositoryUrl", asapServerProperties.getPublicKeyRepositoryUrl());
        assertEquals("https://publicKeyFallbackRepositoryUrl", asapServerProperties.getPublicKeyFallbackRepositoryUrl());
        assertThat(asapServerProperties.getAuthorizedIssuers(), containsInAnyOrder("issuer1", "issuer2"));
        assertThat(asapServerProperties.getAuthorizedSubjects(), containsInAnyOrder("subject1", "subject2"));
    }
}