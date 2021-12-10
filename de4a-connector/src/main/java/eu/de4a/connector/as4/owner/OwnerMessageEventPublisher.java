package eu.de4a.connector.as4.owner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class OwnerMessageEventPublisher {
    private static final Log LOG = LogFactory.getLog(OwnerMessageEventPublisher.class);
    
    @Autowired
    private ApplicationEventMulticaster applicationEventMulticaster;

    public void publishCustomEvent(ContextRefreshedEvent event) {
        if (LOG.isDebugEnabled())
            LOG.debug("Publishing AS4 message event");
        applicationEventMulticaster.multicastEvent(event);
    }
}