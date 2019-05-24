package com.atlassian.asap.client;

import com.atlassian.asap.api.JwtBuilder;
import com.atlassian.asap.core.client.jersey.AsapAuthenticationFilter;
import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;

public class Main {

    private static final String ASAP_JERSEY_ENDPOINT = "http://localhost:9090/rest/jersey-secured-resource";

    public static void main(String[] args) throws URISyntaxException {

        ClientConfig config = new ClientConfig();
        config.register(new AsapAuthenticationFilter(JwtBuilder
                .newJwt()
                .keyId("opsgenie-user-service/1557334891")
                .issuer("opsgenie-user-service")
                .audience("jira-user-service")
                .build(), new URIBuilder().setPath("classpath:///").build()));
    
        Client client = JerseyClientBuilder.createClient(config);

        WebTarget target = client.target(ASAP_JERSEY_ENDPOINT);
        Response response = target.request().get();

        System.out.println("Response is : " + response.readEntity(String.class));
    }
}
