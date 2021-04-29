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
import eu.de4a.connector.model.OwnerAddresses;
import eu.de4a.connector.model.RequestorRequest;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.repository.RequestorRequestRepository;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.toop.connector.api.TCIdentifierFactory;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;

@Component
public class EvidenceTransferorManager extends EvidenceManager {
	private static final Logger logger = LoggerFactory.getLogger (EvidenceTransferorManager.class);

	@Autowired
	private Client client;
	@Autowired
	private OwnerLocator ownerLocator;
	@Autowired
	private RequestorRequestRepository requestorRequestRepository;


	public void queueMessage(MessageOwner request) {
		ResponseTransferEvidenceType responseTransferEvidenceType = null; 
		if (logger.isDebugEnabled()) {
			logger.debug("Queued message to send to owner:");
			logger.debug(DOMUtils.documentToString(request.getMessage().getOwnerDocument()));
		}
		OwnerAddresses ownerAddress  = ownerLocator.lookupOwnerAddress(request.getReceiverId()); 
		RequestorRequest requestorReq = new RequestorRequest();
		if(ownerAddress != null) {
			RequestTransferEvidenceUSIIMDRType req = DE4AMarshaller.drImRequestMarshaller().read(request.getMessage());
			if (req != null) {
				requestorReq.setCanonicalEvidenceTypeId(req.getCanonicalEvidenceTypeId());
				requestorReq.setDataOwnerId(req.getDataOwner().getAgentUrn());
				requestorReq.setReturnServiceUri("unused");
				try {
					responseTransferEvidenceType = (ResponseTransferEvidenceType) client.sendEvidenceRequest(
							req, ownerAddress.getEndpoint(), false);
				} catch (NoSuchMessageException | MessageException e) {
					logger.error("Fail...", e);
					// TODO error handling
				}
				if(responseTransferEvidenceType != null) {
					sendResponseMessage(req.getDataEvaluator().getAgentUrn(), req.getCanonicalEvidenceTypeId(),
							DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
							.getAsDocument(responseTransferEvidenceType).getDocumentElement(), DE4AConstants.TAG_EVIDENCE_RESPONSE);
				}
			} else {
				req = DE4AMarshaller.drUsiRequestMarshaller().read(request.getMessage());
				requestorReq.setCanonicalEvidenceTypeId(req.getCanonicalEvidenceTypeId());
				requestorReq.setDataOwnerId(req.getDataOwner().getAgentUrn());
				requestorReq.setReturnServiceUri("unused");
				try {
					client.sendEvidenceRequest(req, ownerAddress.getEndpoint(), true);
				} catch (MessageException e) {
					logger.error("Fail...",e);
					//TODO error handling
				}
			}
			// Save request information
			requestorReq.setIdrequest(request.getId());
			requestorReq.setEvidenceServiceUri(request.getReceiverId());
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
			sendResponseMessage(usirequest.getSenderId(), usirequest.getCanonicalEvidenceTypeId (), response.getMessage(),
					DE4AConstants.TAG_FORWARD_EVIDENCE_REQUEST);
		}
	}

	public boolean sendResponseMessage(String sender, String docTypeID, Element message, String tagContentId) {
		NodeInfo nodeInfo = client.getNodeInfo(sender, docTypeID, true,message);
		try {
			logger.debug("Sending  message to as4 gateway ...");

			String senderId = sender;
			if(sender.contains(TCIdentifierFactory.PARTICIPANT_SCHEME + DE4AConstants.DOUBLE_SEPARATOR)) {
				senderId = sender.replace(TCIdentifierFactory.PARTICIPANT_SCHEME + DE4AConstants.DOUBLE_SEPARATOR, "");
			}

			// TODO update as4 client, it is not handling payloads list anymore
			List<TCPayload> payloads = new ArrayList<>();
			TCPayload payload = new TCPayload();
			payload.setContentID(tagContentId);
			payload.setValue(DOMUtils.documentToByte(message.getOwnerDocument()));
			payload.setMimeType("application/xml");
			payloads.add(payload);
			Element requestWrapper = new RegRepTransformer().wrapMessage(message, false);
			as4Client.sendMessage(senderId, nodeInfo, nodeInfo.getDocumentIdentifier(), requestWrapper, payloads, false);
			return true;
		} catch (MEOutgoingException e) {
			logger.error("Error with as4 gateway comunications", e);
		} catch (MessageException e) {
			logger.error("Error building wrapper message", e);
		}
		return false;
	}

}
