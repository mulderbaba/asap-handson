package com.atlassian.asap.server;

import com.atlassian.asap.server.configuration.AsapServerAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AsapServerAutoConfiguration.class)
public class AsapServerExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsapServerExampleApplication.class, args);
	}

}
