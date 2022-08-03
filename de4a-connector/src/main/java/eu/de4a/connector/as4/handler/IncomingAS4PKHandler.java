package eu.de4a.connector.as4.handler;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.helger.dcng.api.me.incoming.IMEIncomingHandler;
import com.helger.dcng.api.me.incoming.MEIncomingException;
import com.helger.dcng.api.me.model.MEMessage;

import eu.de4a.connector.api.service.model.MessageExchangeWrapper;

/**
 * This is the handler for incoming AS4 messages. It is registered on startup
 * and spreads the information.
 */
@Component
public class IncomingAS4PKHandler implements IMEIncomingHandler
{
  private static final Logger LOGGER = LoggerFactory.getLogger (IncomingAS4PKHandler.class);

  @Autowired
  private ApplicationContext context;
  @Autowired
  private MessageEventPublisher publisher;

  @Override
  public void handleIncomingRequest (@Nonnull final MEMessage aMessage) throws MEIncomingException
  {
    if (context == null)
      throw new MEIncomingException ("IncomingAS4PKHandler/ApplicationContext wasn't initialized properly");
    if (publisher == null)
      throw new MEIncomingException ("IncomingAS4PKHandler/MessageEventPublisher wasn't initialized properly");

    LOGGER.info ("[DR-DT] Start handling incoming AS4 message");

    //KafkaClientWrapper.sendInfo (ELogMessage.LOG_AS4_REQ_RECEIPT);

    final MessageExchangeWrapper messageWrapper = new MessageExchangeWrapper (context, aMessage);
    publisher.publishCustomEvent (messageWrapper);

    LOGGER.info ("[DR-DT] Finished handling incoming AS4 message");
  }
}
