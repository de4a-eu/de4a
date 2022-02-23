package eu.de4a.connector.as4.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.phase4.attachment.WSS4JAttachment;

import eu.de4a.connector.as4.client.Phase4GatewayClient;
import eu.de4a.connector.as4.owner.MessageRequestOwner;
import eu.de4a.connector.as4.owner.OwnerMessageEventPublisher;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.error.utils.KafkaClientWrapper;
import eu.de4a.connector.mem.phase4.servlet.EdmRequestWrapper;
import eu.de4a.connector.model.utils.MessageUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.toop.connector.api.me.incoming.IMEIncomingHandler;
import eu.toop.connector.api.me.incoming.IncomingEDMErrorResponse;
import eu.toop.connector.api.me.incoming.IncomingEDMRequest;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.incoming.MEIncomingException;


@Component
public class IncomingAS4PKHandler implements IMEIncomingHandler{
	private static final Logger logger = LoggerFactory.getLogger (IncomingAS4PKHandler.class);

	@Autowired
	private Phase4GatewayClient phase4GatewayClient;	
	@Autowired
    private ApplicationContext context;
	@Autowired
    private OwnerMessageEventPublisher publisher;

	@Override
    public void handleIncomingRequest(IncomingEDMRequest aRequest) throws MEIncomingException {
        logger.debug("Incoming request...");
        
        EdmRequestWrapper edmwrapper = (EdmRequestWrapper) aRequest;
        MessageRequestOwner messageOwner = new MessageRequestOwner(context);
        
        WSS4JAttachment attached = edmwrapper.getAttacheds().stream()
                .filter(e -> e.getId().equals(DE4AConstants.TAG_EVIDENCE_REQUEST)).findFirst().orElse(null);
        if (attached == null) {
            KafkaClientWrapper.sendError(LogMessages.LOG_ERROR_AS4_MSG_INVALID);
            throw new MEIncomingException(new MessageUtils(LogMessages.LOG_ERROR_AS4_MSG_INVALID.getKey()).value());
        }
        Document evidenceRequest = DOMUtils.newDocumentFromInputStream(attached.getSourceStream());
        try {
            Node nodeRequest = DOMUtils.getNodeFromXpath(DE4AConstants.XPATH_EVIDENCE_REQUEST, evidenceRequest.getDocumentElement());
            messageOwner.setMessage((Element) nodeRequest);
            messageOwner.setId(edmwrapper.getRequest().getRequestID());
            messageOwner.setSenderId(aRequest.getMetadata().getSenderID().getURIEncoded());
            messageOwner.setReceiverId(aRequest.getMetadata().getReceiverID().getURIEncoded());

            KafkaClientWrapper.sendInfo(LogMessages.LOG_AS4_REQ_RECEIPT, messageOwner.getId());
        } catch (MessageException e) {
            KafkaClientWrapper.sendError(LogMessages.LOG_ERROR_AS4_REQ_INCOMING, e.getMessage());
        }
        publisher.publishCustomEvent(messageOwner);
    }

	@Override
	public void handleIncomingResponse(IncomingEDMResponse aResponse) throws MEIncomingException {
		logger.debug("Incoming response...");
		phase4GatewayClient.processResponseAs4(aResponse);
	}

	@Override
	public void handleIncomingErrorResponse(IncomingEDMErrorResponse aErrorResponse) throws MEIncomingException {
		// TODO Auto-generated method stub

	}

}
