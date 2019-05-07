package com.atlassian.asap.server;

public class Payload {

    private long timestamp;
    private String name;

    public Payload(String name) {
        this.timestamp = System.currentTimeMillis();
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }
}
