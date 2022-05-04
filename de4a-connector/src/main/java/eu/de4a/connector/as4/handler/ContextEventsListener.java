package eu.de4a.connector.as4.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import eu.de4a.connector.api.manager.MessageExchangeManager;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;

/**
 * Asynchronous listener for spring events. It manage requests
 * from an external monitored service
 *
 */
@Component
public class ContextEventsListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextEventsListener.class);

    @Autowired
    private MessageExchangeManager messageExchangeManager;

    public void onApplicationEvent(final ContextRefreshedEvent cse) {
        LOGGER.info("Processing event received: " + cse.getClass().getName());

        if(MessageExchangeWrapper.class.isAssignableFrom(cse.getClass())) {
                final MessageExchangeWrapper message = (MessageExchangeWrapper) cse;
                this.messageExchangeManager.processMessageExchange(message);
        } else {
                LOGGER.warn("Event received is not an instance of MessageExchangeWrapper, "
                        + "it won´t be processed. " + cse.getClass().getName());
        }
    }
}
