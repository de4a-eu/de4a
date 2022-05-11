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
import eu.de4a.connector.error.model.ELogMessages;
import eu.de4a.connector.utils.KafkaClientWrapper;

/**
 * This is the handler for incoming AS4 messages. It is registered on startup and spreads the
 * information.
 */
@Component
public class IncomingAS4PKHandler implements IMEIncomingHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(IncomingAS4PKHandler.class);

  @Autowired
  private ApplicationContext context;
  @Autowired
  private MessageEventPublisher publisher;

  public IncomingAS4PKHandler() {}

  @Override
  public void handleIncomingRequest(final MEMessage aMessage) throws MEIncomingException {
    if (context == null)
      throw new IllegalStateException("ApplicationContext wasn't initialized properly");
    if (publisher == null)
      throw new IllegalStateException("MessageEventPublisher wasn't initialized properly");

    LOGGER.info("Incoming AS4 message...");

    KafkaClientWrapper.sendInfo(ELogMessages.LOG_AS4_REQ_RECEIPT);

    final MessageExchangeWrapper messageWrapper = new MessageExchangeWrapper(context);
    messageWrapper.setMeMessage(aMessage);
    publisher.publishCustomEvent(messageWrapper);
  }
}
