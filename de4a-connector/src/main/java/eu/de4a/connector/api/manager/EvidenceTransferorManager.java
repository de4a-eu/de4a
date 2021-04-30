package eu.de4a.connector.api.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.connector.api.controller.error.ErrorHandlerUtils;
import eu.de4a.connector.api.controller.error.ExternalModuleError;
import eu.de4a.connector.api.controller.error.FamilyErrorType;
import eu.de4a.connector.api.controller.error.LayerError;
import eu.de4a.connector.api.controller.error.ResponseErrorException;
import eu.de4a.connector.api.controller.error.ResponseTransferEvidenceException;
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
		    RequestTransferEvidenceUSIIMDRType req = (RequestTransferEvidenceUSIIMDRType) ErrorHandlerUtils
		            .conversionDocWithCatching(DE4AMarshaller.drImRequestMarshaller(), 
                    request.getMessage().getOwnerDocument(), false, false, LayerError.INTERNAL_FAILURE, 
                    ExternalModuleError.NONE, new ResponseTransferEvidenceException(), null);
			if (req != null) {
				requestorReq.setCanonicalEvidenceTypeId(req.getCanonicalEvidenceTypeId());
				requestorReq.setDataOwnerId(req.getDataOwner().getAgentUrn());
				requestorReq.setReturnServiceUri("unused");
				responseTransferEvidenceType = (ResponseTransferEvidenceType) client.sendEvidenceRequest(
						req, ownerAddress.getEndpoint(), false);
				if(responseTransferEvidenceType != null) {
				    Document docResponse = (Document) ErrorHandlerUtils.conversionDocWithCatching(
				            DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE), 
				            responseTransferEvidenceType, true, false, LayerError.INTERNAL_FAILURE, ExternalModuleError.NONE, 
				            new ResponseTransferEvidenceException(), null);
				    //TODO if as4 message DT-DR failed, what is the approach. retries?
					if(!sendResponseMessage(req.getDataEvaluator().getAgentUrn(), req.getCanonicalEvidenceTypeId(),					        
							docResponse.getDocumentElement(), DE4AConstants.TAG_EVIDENCE_RESPONSE)) {
					    logger.error("Error sending ResponseTransferEvidence to Data Requestor through AS4 gateway");
					}
				} else {
				    throw new ResponseTransferEvidenceException()
    				    .withFamily(FamilyErrorType.CONNECTION_ERROR) 
    	                .withModule(ExternalModuleError.DATA_OWNER)
    	                .withMessageArg("Response from owner was null")
    	                .withHttpStatus(HttpStatus.OK);
				}
			} else {
			    req = (RequestTransferEvidenceUSIIMDRType) ErrorHandlerUtils.conversionDocWithCatching(
			            DE4AMarshaller.drUsiRequestMarshaller(), request.getMessage(), false, true,
			            LayerError.INTERNAL_FAILURE, ExternalModuleError.NONE, new ResponseErrorException(), null);
				requestorReq.setCanonicalEvidenceTypeId(req.getCanonicalEvidenceTypeId());
				requestorReq.setDataOwnerId(req.getDataOwner().getAgentUrn());
				requestorReq.setReturnServiceUri("unused");
				client.sendEvidenceRequest(req, ownerAddress.getEndpoint(), true);
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
		    //TODO if as4 message DT-DR failed, what is the approach. retries?
			if(!sendResponseMessage(usirequest.getSenderId(), usirequest.getCanonicalEvidenceTypeId (), response.getMessage(),
					DE4AConstants.TAG_FORWARD_EVIDENCE_REQUEST)) {
			    logger.error("Error sending RequestForwardEvidence to Data Requestor through AS4 gateway");
			}
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
