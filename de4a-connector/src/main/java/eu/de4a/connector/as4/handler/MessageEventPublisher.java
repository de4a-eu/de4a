package eu.de4a.connector.as4.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class MessageEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEventPublisher.class);

    @Autowired
    private ApplicationEventMulticaster applicationEventMulticaster;

    public void publishCustomEvent(final ContextRefreshedEvent event) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Publishing AS4 message event");
        applicationEventMulticaster.multicastEvent(event);
    }
}