package com.atlassian.asap.client;


import com.atlassian.asap.client.configuration.AsapClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "sample.asap.client")
public class SampleAsapClientProperties extends AsapClientProperties {
}
