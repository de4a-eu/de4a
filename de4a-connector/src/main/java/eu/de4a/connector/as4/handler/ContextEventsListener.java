package eu.de4a.connector.as4.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import eu.de4a.connector.api.manager.MessageExchangeManager;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;
import lombok.extern.log4j.Log4j2;

/**
 * Asynchronous listener for spring events. It manage requests
 * from an external monitored service
 *
 */
@Component
@Log4j2
public class ContextEventsListener implements ApplicationListener<ContextRefreshedEvent> {
    
    @Autowired
    private MessageExchangeManager messageExchangeManager;

    public void onApplicationEvent(ContextRefreshedEvent cse) {
        log.info("Processing event received: " + cse.getClass().getName());
        
        if(MessageExchangeWrapper.class.isAssignableFrom(cse.getClass())) {
                MessageExchangeWrapper message = (MessageExchangeWrapper) cse;
                this.messageExchangeManager.processMessageExchange(message);
        } else {
                log.warn("Event received is not an instance of MessageExchangeWrapper, "
                        + "it won´t be processed. " + cse.getClass().getName());
        }
    }
}
