package com.atlassian.asap.server;

import com.atlassian.asap.core.server.interceptor.Asap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuredEndpoint {

    @Asap
    @PostMapping("/hello")
    public Payload sayHello(@RequestBody String name) {
        return new Payload(name);
    }
}
