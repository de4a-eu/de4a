package eu.de4a.connector.api.manager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.error.level.EErrorLevel;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;

import eu.de4a.connector.as4.client.regrep.RegRepTransformer;
import eu.de4a.connector.client.Client;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseLookupRoutingInformationException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceUSIException;
import eu.de4a.connector.error.handler.ResponseErrorExceptionHandler;
import eu.de4a.connector.error.handler.ResponseLookupRoutingInformationExceptionHandler;
import eu.de4a.connector.error.handler.ResponseTransferEvidenceExceptionHandler;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;

@Component
public class EvidenceRequestorManager extends EvidenceManager {

	private static final Logger logger = LoggerFactory.getLogger(EvidenceRequestorManager.class);
	@Value("#{'${smp.endpoint.jvm:${smp.endpoint:}}'}")
	private String smpEndpoint;
	@Value("#{'${idk.endpoint.jvm:${idk.endpoint:}}'}")
	private String idkEndpoint;
	@Value("${as4.timeout.miliseconds:#{60000}}")
	private long timeout;

	@Autowired
	private Client client;
	@Autowired
	private ResponseManager responseManager;

	public ResponseLookupRoutingInformationType manageRequest(RequestLookupRoutingInformationType request) {
		ResponseLookupRoutingInformationType response = new ResponseLookupRoutingInformationType();

		if (request != null && !ObjectUtils.isEmpty(request.getCanonicalEvidenceTypeId())) {
			if (ObjectUtils.isEmpty(request.getDataOwnerId())) {
				return client.getSources(request);
			} else {
				return client.getProvisions(request);
			}
		} else {
		    new ResponseLookupRoutingInformationExceptionHandler().buildResponse(
                    new ResponseLookupRoutingInformationException().withFamily(FamilyErrorType.MISSING_REQUIRED_ARGUMENTS)
                        .withLayer(LayerError.INTERNAL_FAILURE)
                        .withModule(ExternalModuleError.IDK)
                        .withMessageArg("CanonicalEvidenceTypeId is missing")
                        .withHttpStatus(HttpStatus.OK));
		}
		return response;
	}

	public ResponseErrorType manageRequestUSI(RequestTransferEvidenceUSIIMDRType request) {
	    Document doc = (Document) ErrorHandlerUtils.conversionDocWithCatching(DE4AMarshaller.drUsiRequestMarshaller(), 
	            request, true, true, new ResponseTransferEvidenceUSIException()
	                                        .withModule(ExternalModuleError.CONNECTOR_DR)
	                                        .withRequest(request));
		try {
            if(sendRequestMessage(request.getDataEvaluator().getAgentUrn(), request.getDataOwner().getAgentUrn(), doc.getDocumentElement(),
            		request.getCanonicalEvidenceTypeId())) {
                return DE4AResponseDocumentHelper.createResponseError(true);
            }            
        } catch (ConnectorException e) {
            return new ResponseErrorExceptionHandler().buildResponse(
                    new ResponseTransferEvidenceUSIException().withFamily(e.getFamily())
                        .withLayer(e.getLayer())
                        .withModule(e.getModule())
                        .withMessageArgs(e.getArgs()));
        }
		return DE4AResponseDocumentHelper.createResponseError(false);
	}

	public ResponseTransferEvidenceType manageRequestIM(RequestTransferEvidenceUSIIMDRType request) {
		Document doc = DE4AMarshaller.drImRequestMarshaller().getAsDocument(request);
		try {
            return handleRequestTransferEvidence(request.getDataEvaluator().getAgentUrn(), request.getDataOwner().getAgentUrn(), 
                    doc.getDocumentElement(), request.getRequestId(), request.getCanonicalEvidenceTypeId());
        } catch (ConnectorException e) {
            return new ResponseTransferEvidenceExceptionHandler().buildResponse(
                    new ResponseTransferEvidenceException().withLayer(e.getLayer())
                        .withFamily(e.getFamily())
                        .withModule(e.getModule())
                        .withMessageArgs(e.getArgs())
                        .withRequest(request));
        }
	}

	private ResponseTransferEvidenceType handleRequestTransferEvidence(String from, String dataOwnerId,
			Element documentElement, String requestId, String canonicalEvidenceTypeId) {
		boolean ok = false;
		sendRequestMessage(from, dataOwnerId, documentElement, canonicalEvidenceTypeId);
		try {
			ok = waitResponse(requestId);
		} catch (InterruptedException e) {
		    String errorMsg = "Error waiting for response";
			logger.error(errorMsg, e);
			Thread.currentThread().interrupt();
			throw new ConnectorException().withLayer(LayerError.INTERNAL_FAILURE)
			    .withFamily(FamilyErrorType.ERROR_RESPONSE)
			    .withModule(ExternalModuleError.CONNECTOR_DR)
			    .withMessageArg(errorMsg);
		}
		if (!ok) {
			String errorMsg = "No response before timeout";
            logger.error(errorMsg);
            throw new ConnectorException().withLayer(LayerError.INTERNAL_FAILURE)
                .withFamily(FamilyErrorType.ERROR_RESPONSE)
                .withModule(ExternalModuleError.CONNECTOR_DR)
                .withMessageArg(errorMsg);
		}
		return responseManager.getResponse(requestId, documentElement);		
	}

	private boolean waitResponse(String id) throws InterruptedException {
		long init = Calendar.getInstance().getTimeInMillis();
		boolean wait = !responseManager.isDone(id);
		boolean ok = !wait;
		while (wait) {
			logger.debug("Waiting for response to complete...");
			Thread.sleep(700);
			ok = responseManager.isDone(id);
			wait = !ok && Calendar.getInstance().getTimeInMillis() - init < timeout;
		}
		return ok;
	}

	public boolean sendRequestMessage(String sender, String dataOwnerId, Element userMessage,
			String canonicalEvidenceTypeId) {
	    String errorMsg;
		try {
		    IParticipantIdentifier doPI = SimpleIdentifierFactory.INSTANCE
		            .parseParticipantIdentifier(dataOwnerId.toLowerCase(Locale.ROOT));
		    IParticipantIdentifier sPI = SimpleIdentifierFactory
		            .INSTANCE.parseParticipantIdentifier(sender.toLowerCase(Locale.ROOT));
		    if(doPI != null) {
		        dataOwnerId = doPI.getValue();
		    }
		    if(sPI != null) {
		        sender = sPI.getValue();
		    }
		    
		    NodeInfo nodeInfo = client.getNodeInfo(dataOwnerId, canonicalEvidenceTypeId, false, userMessage);
			DE4AKafkaClient.send(EErrorLevel.INFO, MessageFormat.format("Sending request message via AS4 gateway - "
                    + "DataEvaluatorId: {0}, DataOwnerId: {1}, CanonicalEvidenceType: {2}",
                    sender, dataOwnerId, canonicalEvidenceTypeId));
			
			Element requestWrapper = new RegRepTransformer().wrapMessage(userMessage, true);
			List<TCPayload> payloads = new ArrayList<>();
			TCPayload p = new TCPayload();
			p.setContentID(DE4AConstants.TAG_EVIDENCE_REQUEST);
			p.setMimeType(MediaType.APPLICATION_XML_VALUE);
			p.setValue(DOMUtils.documentToByte(userMessage.getOwnerDocument()));
			payloads.add(p);
			
			as4Client.sendMessage(sender, nodeInfo, requestWrapper, payloads, true);
			
			return true;
		} catch (MEOutgoingException e) {
		    errorMsg = "Error with as4 gateway communications";
			logger.error(errorMsg, e);
			throw new ConnectorException()
                .withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.AS4_ERROR_COMMUNICATION)
                .withModule(ExternalModuleError.CONNECTOR_DT)
                .withMessageArg(errorMsg);
		} catch (ConnectorException cE) {
		    throw cE.withModule(ExternalModuleError.CONNECTOR_DT);
		} catch (NullPointerException | MessageException msgE) {
		    throw new ConnectorException().withLayer(LayerError.INTERNAL_FAILURE)
		        .withFamily(FamilyErrorType.CONVERSION_ERROR)
		        .withModule(ExternalModuleError.CONNECTOR_DR)
		        .withMessageArg(msgE.getMessage())
		        .withHttpStatus(HttpStatus.OK);
		}
	}

}
