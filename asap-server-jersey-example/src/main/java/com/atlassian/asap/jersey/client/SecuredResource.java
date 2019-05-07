package com.atlassian.asap.jersey.client;

import com.atlassian.asap.core.server.jersey.Asap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Asap
@Path("rest")
public class SecuredResource {

    @GET
    @Path("/jersey-secured-resource")
    public String sayHello() {
        return "hello there!";
    }
}
