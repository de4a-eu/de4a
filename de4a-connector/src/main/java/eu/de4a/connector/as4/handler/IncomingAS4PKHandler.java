package eu.de4a.connector.as4.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.helger.dcng.api.me.incoming.IMEIncomingHandler;
import com.helger.dcng.api.me.incoming.MEIncomingException;
import com.helger.dcng.api.me.model.MEMessage;

import eu.de4a.connector.api.service.model.MessageExchangeWrapper;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.utils.KafkaClientWrapper;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class IncomingAS4PKHandler implements IMEIncomingHandler {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private MessageEventPublisher publisher;

    @Override
    public void handleIncomingRequest(MEMessage aMessage) throws MEIncomingException {
        log.debug("Incoming AS4 message...");
        
        KafkaClientWrapper.sendInfo(LogMessages.LOG_AS4_REQ_RECEIPT);

        MessageExchangeWrapper messageWrapper = new MessageExchangeWrapper(context);
        messageWrapper.setMeMessage(aMessage);
        publisher.publishCustomEvent(messageWrapper);
    }

}
