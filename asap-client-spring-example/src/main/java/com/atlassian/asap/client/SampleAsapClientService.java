package com.atlassian.asap.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleAsapClientService extends AsapClientService {

    @Autowired
    protected SampleAsapClientService(SampleAsapClientProperties asapClientProperties) {
        super(asapClientProperties);
    }

    @GetMapping("/request")
    public Ticket doAsapCall() {
        final AsapRequest asapRequest = new SampleAsapRequest()
                .body("Hi Bucharest!")
                .withUrl("http://localhost:8080/hello");
        try {
            AsapResponse<Ticket> response = doPost(asapRequest, Ticket.class);
            return response.getBody();
        } catch (AsapClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
