package eu.de4a.connector.api.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import eu.de4a.connector.as4.client.regrep.RegRepTransformer;
import eu.de4a.connector.as4.owner.MessageOwner;
import eu.de4a.connector.as4.owner.MessageResponseOwner;
import eu.de4a.connector.as4.owner.OwnerLocator;
import eu.de4a.connector.client.Client;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.connector.model.OwnerAddresses;
import eu.de4a.connector.model.RequestorRequest;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.repository.RequestorRequestRepository;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.SMPUtils;
import eu.de4a.util.XDE4ACanonicalEvidenceType;
import eu.de4a.util.XDE4AMarshaller;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;

@Component 
public class EvidenceTransferorManager extends EvidenceManager {
	private static final Logger logger = LoggerFactory.getLogger (EvidenceTransferorManager.class);	

	@Value("#{'${as4.me.id.jvm:${as4.me.id:}}'}")
	private String meId; 
	@Value("#{'${smp.endpoint.jvm:${smp.endpoint:}}'}")
	private String smpEndpoint;
	@Autowired
	private Client client;
	@Autowired
	private OwnerLocator ownerLocator;
	@Autowired
	private RequestorRequestRepository requestorRequestRepository;
	
	
	public void queueMessage(MessageOwner request) {
		ResponseTransferEvidenceType responseTransferEvidenceType = null;
		OwnerAddresses evidenceEntity = null;
		if (logger.isDebugEnabled()) {
			logger.debug("Queued message to send to owner:");
			logger.debug(DOMUtils.documentToString(request.getMessage().getOwnerDocument()));
		}
		try {
			evidenceEntity = ownerLocator.lookupEvidence(request.getEvidenceService());

		} catch (MessageException e) {
			logger.error("No evidence with name {}", request.getEvidenceService());
			// TODO error handling
		}
		RequestorRequest requestorReq = new RequestorRequest();
		if(evidenceEntity != null) {
			RequestTransferEvidenceUSIIMDRType req = DE4AMarshaller.drImRequestMarshaller().read(request.getMessage());
			if (req != null) {
				requestorReq.setCanonicalEvidenceTypeId(req.getCanonicalEvidenceTypeId());
				requestorReq.setDataOwnerId(req.getDataOwner().getAgentUrn());
				requestorReq.setReturnServiceUri(SMPUtils.getSmpUri(smpEndpoint, request.getSenderId(), 
						req.getCanonicalEvidenceTypeId()));
				try {
					responseTransferEvidenceType = (ResponseTransferEvidenceType) client.sendEvidenceRequest(
							req, evidenceEntity.getEndpoint(), false);
				} catch (NoSuchMessageException | MessageException e) {
					logger.error("Fail...", e);
					// TODO error handling
				}			
				sendResponseMessage(meId, requestorReq.getReturnServiceUri(), XDE4AMarshaller.drImResponseMarshaller(
						XDE4ACanonicalEvidenceType.getXDE4CanonicalEvidenceType(req.getCanonicalEvidenceTypeId()))
						.getAsDocument(responseTransferEvidenceType).getDocumentElement(), DE4AConstants.TAG_EVIDENCE_RESPONSE);
			} else {
				req = DE4AMarshaller.drUsiRequestMarshaller().read(request.getMessage());
				requestorReq.setCanonicalEvidenceTypeId(req.getCanonicalEvidenceTypeId());
				requestorReq.setDataOwnerId(req.getDataOwner().getAgentUrn());
				requestorReq.setReturnServiceUri(SMPUtils.getSmpUri(smpEndpoint, request.getSenderId(), 
						req.getCanonicalEvidenceTypeId()));
				try {
					client.sendEvidenceRequest(req, evidenceEntity.getEndpoint(), true);
				} catch (MessageException e) {
					logger.error("Fail...",e);
					//TODO error handling 
				}
			}
			// Save request information			
			requestorReq.setIdrequest(request.getId());			
			requestorReq.setEvidenceServiceUri(request.getEvidenceService());			
			requestorReq.setSenderId(request.getSenderId());
			requestorReq.setDone(false);
			requestorRequestRepository.save(requestorReq);
		}
	}
	
	public void queueMessageResponse(MessageResponseOwner response) {
		if (logger.isDebugEnabled()) {
			logger.debug("Queued response from owner USI-Pattern:");
			logger.debug(DOMUtils.documentToString(response.getMessage().getOwnerDocument()));
		}
		RequestorRequest usirequest = requestorRequestRepository.findById(response.getId()).orElse(null);
		if (usirequest == null) {
			logger.error("Does not exists any request with ID {}", response.getId());
		} else {
			sendResponseMessage(meId, usirequest.getReturnServiceUri(), response.getMessage(), 
					DE4AConstants.TAG_FORWARD_EVIDENCE_REQUEST);
		}
	}	
	
	public boolean sendResponseMessage(String sender, String uriSmp, Element message, String tagContentId) {
		NodeInfo nodeInfo = client.getNodeInfo(uriSmp, true);
		try {
			logger.debug("Sending  message to as4 gateway ...");

			// TODO update as4 client, it is not handling payloads list anymore
			List<TCPayload> payloads = new ArrayList<>();
			TCPayload payload = new TCPayload();
			payload.setContentID(tagContentId);
			payload.setValue(DOMUtils.documentToByte(message.getOwnerDocument()));
			payload.setMimeType("application/xml");
			payloads.add(payload);
			Element requestSillyWrapper = new RegRepTransformer().wrapMessage(message, false);
			as4Client.sendMessage(sender, nodeInfo, nodeInfo.getDocumentIdentifier(), requestSillyWrapper, payloads, false);
			return true;
		} catch (MEOutgoingException e) {
			logger.error("Error with as4 gateway comunications", e);
		} catch (MessageException e) {
			logger.error("Error building wrapper message", e);
		}
		return false;
	}
	
}
