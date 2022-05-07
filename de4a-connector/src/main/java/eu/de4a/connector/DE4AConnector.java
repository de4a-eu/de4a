package eu.de4a.connector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.ws.config.annotation.EnableWs;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableWs
public class DE4AConnector {
    
    public static void main(String[] args) {
        SpringApplication.run(DE4AConnector.class, args);
    }
}