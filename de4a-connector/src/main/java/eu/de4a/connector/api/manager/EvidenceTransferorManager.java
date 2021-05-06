package eu.de4a.connector.api.manager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.error.level.EErrorLevel;
import com.helger.peppolid.CIdentifier;
import eu.de4a.connector.as4.client.regrep.RegRepTransformer;
import eu.de4a.connector.as4.owner.MessageOwner;
import eu.de4a.connector.as4.owner.MessageResponseOwner;
import eu.de4a.connector.as4.owner.OwnerLocator;
import eu.de4a.connector.client.Client;
import eu.de4a.connector.error.exceptions.ResponseErrorException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.model.OwnerAddresses;
import eu.de4a.connector.model.RequestorRequest;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.repository.RequestorRequestRepository;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
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
		logger.info("Queued message to be send to the owner - RequestId: {}, DataEvaluatorId: {}, DataOwnerId: {}", 
		        request.getId(), request.getSenderId(), request.getReceiverId());
		if (logger.isDebugEnabled()) {			
			logger.debug(DOMUtils.documentToString(request.getMessage().getOwnerDocument()));
		}
		ResponseTransferEvidenceType responseTransferEvidenceType = null;
		
		DE4AKafkaClient.send(EErrorLevel.INFO, MessageFormat.format("Looking for data owner address - "
                + "DataOwnerId: {0}", request.getReceiverId()));
		
		OwnerAddresses ownerAddress  = ownerLocator.lookupOwnerAddress(request.getReceiverId()); 
		RequestorRequest requestorReq = new RequestorRequest();
		if(ownerAddress != null) {
		    RequestTransferEvidenceUSIIMDRType req = (RequestTransferEvidenceUSIIMDRType) ErrorHandlerUtils
		            .conversionDocWithCatching(DE4AMarshaller.drImRequestMarshaller(), 
                    request.getMessage().getOwnerDocument(), false, false, 
                    new ResponseTransferEvidenceException().withModule(ExternalModuleError.CONNECTOR_DT));
			if (req != null) {
				requestorReq.setCanonicalEvidenceTypeId(req.getCanonicalEvidenceTypeId());
				requestorReq.setDataOwnerId(req.getDataOwner().getAgentUrn());
				requestorReq.setReturnServiceUri("unused");
				responseTransferEvidenceType = (ResponseTransferEvidenceType) client.sendEvidenceRequest(
						req, ownerAddress.getEndpoint(), false);
				if(responseTransferEvidenceType != null) {
				    Document docResponse = (Document) ErrorHandlerUtils.conversionDocWithCatching(
				            DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE), 
				            responseTransferEvidenceType, true, false, 
				            new ResponseTransferEvidenceException().withModule(ExternalModuleError.CONNECTOR_DT));
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
			            new ResponseErrorException().withModule(ExternalModuleError.CONNECTOR_DT));
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
	    logger.info("Queued response from owner USI pattern - RequestId: {}, DataEvaluatorId: {}, DataOwnerId: {}", 
	            response.getId(), response.getDataEvaluatorId(), response.getDataOwnerId());
		if (logger.isDebugEnabled()) {			
			logger.debug(DOMUtils.documentToString(response.getMessage().getOwnerDocument()));
		}
		RequestorRequest usirequest = requestorRequestRepository.findById(response.getId()).orElse(null);
		if (usirequest == null) {
			logger.error("Does not exists any request with ID {}", response.getId());
		} else {
		    //TODO if as4 message DT-DR failed, what is the approach. retries?
			if(!sendResponseMessage(usirequest.getSenderId(), usirequest.getCanonicalEvidenceTypeId (), response.getMessage(),
					DE4AConstants.TAG_EVIDENCE_REQUEST_DT)) {
			    logger.error("Error sending RequestForwardEvidence to Data Requestor through AS4 gateway");
			}
		}
	}

	public boolean sendResponseMessage(String sender, String docTypeID, Element message, String tagContentId) {
		NodeInfo nodeInfo = client.getNodeInfo(sender, docTypeID, true,message);
		try {
		    String senderId = sender;
            if(sender.contains(TCIdentifierFactory.PARTICIPANT_SCHEME + CIdentifier.URL_SCHEME_VALUE_SEPARATOR)) {
                senderId = sender.replace(TCIdentifierFactory.PARTICIPANT_SCHEME + CIdentifier.URL_SCHEME_VALUE_SEPARATOR, "");
            }
            
		    String logMsg= MessageFormat.format("Sending response message via AS4 gateway - "
                    + "DataEvaluatorId: {0}, Message tag: {1}, CanonicalEvidenceType: {3}", 
                    senderId, tagContentId, docTypeID);
			logger.info(logMsg);
			DE4AKafkaClient.send(EErrorLevel.INFO, logMsg);
			
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
