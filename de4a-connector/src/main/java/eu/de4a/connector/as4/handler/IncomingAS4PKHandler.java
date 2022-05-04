package eu.de4a.connector.as4.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import com.helger.dcng.api.me.incoming.IMEIncomingHandler;
import com.helger.dcng.api.me.incoming.MEIncomingException;
import com.helger.dcng.api.me.model.MEMessage;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.utils.KafkaClientWrapper;

@Component
public class IncomingAS4PKHandler implements IMEIncomingHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IncomingAS4PKHandler.class);

    @Autowired
    private ApplicationContext context;
    @Autowired
    private MessageEventPublisher publisher;

    @Override
    public void handleIncomingRequest(final MEMessage aMessage) throws MEIncomingException {
      LOGGER.debug("Incoming AS4 message...");

        KafkaClientWrapper.sendInfo(LogMessages.LOG_AS4_REQ_RECEIPT);

        final MessageExchangeWrapper messageWrapper = new MessageExchangeWrapper(context);
        messageWrapper.setMeMessage(aMessage);
        publisher.publishCustomEvent(messageWrapper);
    }

}
