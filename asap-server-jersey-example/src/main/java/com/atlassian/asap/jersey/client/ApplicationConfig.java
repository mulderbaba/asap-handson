package com.atlassian.asap.jersey.client;

import com.atlassian.asap.core.server.jersey.AsapValidator;
import com.atlassian.asap.core.server.jersey.AuthenticationRequestFilter;
import com.atlassian.asap.core.server.jersey.AuthorizationRequestFilter;
import com.atlassian.asap.core.server.jersey.EmptyBodyFailureHandler;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;

@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

    private static final String PUBLIC_KEY_REPOSITORY_URL = "https://javasummit.blob.core.windows.net/publickeys/";
    private static final String AUDIENCE = "jira-user-service";
    private static final String AUTHORIZED_ISSUERS = "opsgenie-user-service";

    public ApplicationConfig() {
        AuthenticationRequestFilter authenticationRequestFilter = AuthenticationRequestFilter.newInstance(AUDIENCE, PUBLIC_KEY_REPOSITORY_URL);
        AuthorizationRequestFilter authorizationRequestFilter = new AuthorizationRequestFilter(new EmptyBodyFailureHandler(), asapValidator(AUTHORIZED_ISSUERS));

        register(authenticationRequestFilter);
        register(authorizationRequestFilter);

        register(SecuredResource.class);
    }

    private AsapValidator asapValidator(String authorizedIssuers) {
        if (authorizedIssuers == null) {
            throw new IllegalArgumentException("Issuers must be provided.");
        }

        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (String s : authorizedIssuers.split(",")) {
            s = StringUtils.stripToNull(s);
            if (s == null) {
                throw new IllegalArgumentException(String.format("Issuers '%s' contains an empty item.", authorizedIssuers));
            }
            builder.add(s);
        }

        ImmutableSet<String> issuers = builder.build();

        if (issuers.isEmpty()) {
            throw new IllegalArgumentException("Issuers contains no elements.");
        }

        return AsapValidator.newAnnotationWithConfigValidator(new HashSet<>(), issuers);
    }
}