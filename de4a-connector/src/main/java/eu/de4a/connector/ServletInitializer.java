package eu.de4a.connector;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import eu.de4a.connector.as4.servlet.TCWebAppListener;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DE4AConnector.class, TCWebAppListener.class);
    }
}
