package eu.de4a.connector.as4.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class MessageEventPublisher {
    
    @Autowired
    private ApplicationEventMulticaster applicationEventMulticaster;

    public void publishCustomEvent(ContextRefreshedEvent event) {
        if (log.isDebugEnabled())
            log.debug("Publishing AS4 message event");
        applicationEventMulticaster.multicastEvent(event);
    }
}