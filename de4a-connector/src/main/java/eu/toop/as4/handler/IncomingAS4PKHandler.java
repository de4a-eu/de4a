package eu.toop.as4.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.helger.phase4.attachment.WSS4JAttachment;

import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.as4.client.Phase4GatewayClient;
import eu.toop.as4.client.RequestWrapper;
import eu.toop.as4.owner.OwnerMessageEventPublisher;
import eu.toop.connector.api.me.incoming.IMEIncomingHandler;
import eu.toop.connector.api.me.incoming.IncomingEDMErrorResponse;
import eu.toop.connector.api.me.incoming.IncomingEDMRequest;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.incoming.MEIncomingException;
import eu.toop.connector.mem.phase4.servlet.EdmRequestWrapper; 
@Component
public class IncomingAS4PKHandler implements IMEIncomingHandler{
	private static final Logger logger = LoggerFactory.getLogger (IncomingAS4PKHandler.class);
	@Autowired
	private OwnerMessageEventPublisher publisher; 
	@Autowired
	private Phase4GatewayClient phase4GatewayClient; 

	@Override
	public void handleIncomingRequest(IncomingEDMRequest aRequest) throws MEIncomingException {
		// TODO Auto-generated method stub
		logger.debug("Aqui andamos..."); 
		EdmRequestWrapper edmwrapper=(EdmRequestWrapper)aRequest;
		RequestWrapper wrapper=new  RequestWrapper(); 
		WSS4JAttachment attachao=edmwrapper.getAttacheds().stream().filter(e->e.getId().equals(DE4AConstants.TAG_EVIDENCE_REQUEST)).findFirst().orElse(null);
		if(attachao==null) {
			String err="EvidenceRequest not found!";
			logger.error(err);
			throw new MEIncomingException(err);
		}
		Document evidenceRequest=DOMUtils.newDocumentFromInputStream(attachao.getSourceStream());
		wrapper.setRequest(evidenceRequest.getDocumentElement());
		try {
			String id=DOMUtils.getValueFromXpath(String.format(DE4AConstants.XPATH_REQUEST_ID,
					DE4AConstants.TAG_EVIDENCE_REQUEST),
					evidenceRequest.getDocumentElement());
			wrapper.setId(id);
			wrapper.setSenderId(aRequest.getMetadata().getSenderID().getValue());
			wrapper.setEvidenceServiceUri(aRequest.getMetadata().getDocumentTypeID().getValue());
			wrapper.setReturnServiceUri(DOMUtils.getValueFromXpath(DE4AConstants.XPATH_RETURN_SERVICE_ID, 
					evidenceRequest.getDocumentElement()));
			if(logger.isDebugEnabled())logger.debug("Request with id {} received",id);
		} catch (MessageException e) {
			logger.error("Error handling request id",e);
		}
		publisher.publishCustomEvent(wrapper);
	}

	@Override
	public void handleIncomingResponse(IncomingEDMResponse aResponse) throws MEIncomingException {
		// TODO Auto-generated method stub
		logger.debug("Aqui response..."); 
		phase4GatewayClient.processResponseAs4(aResponse); 
	}

	@Override
	public void handleIncomingErrorResponse(IncomingEDMErrorResponse aErrorResponse) throws MEIncomingException {
		// TODO Auto-generated method stub
		
	}

}
