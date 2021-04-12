package eu.de4a.connector.api.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.mime.CMimeType;

import eu.de4a.connector.as4.client.regrep.RegRepTransformer;
import eu.de4a.connector.client.Client;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ErrorType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.SMPUtils;
import eu.toop.connector.api.TCIdentifierFactory;
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
			response.setErrorList(new ErrorListType());
			ErrorType error = new ErrorType();
			error.setCode("COD_EMPTY");
			error.setText("Bad request. Missing mandatory fields");
			response.getErrorList().addError(error);
		}

		return response;
	}

	public boolean manageRequestUSI(RequestTransferEvidenceUSIIMDRType request) {
		Document doc = DE4AMarshaller.drUsiRequestMarshaller().getAsDocument(request);

		if (ObjectUtils.isEmpty(request.getDataOwner().getAgentUrn())) {
			return false;
		}
		return sendRequestMessage(request.getDataEvaluator().getAgentUrn(), request.getDataOwner().getAgentUrn(), doc.getDocumentElement(),
				request.getCanonicalEvidenceTypeId());
	}

	public ResponseTransferEvidenceType manageRequestIM(RequestTransferEvidenceUSIIMDRType request) {
		Document doc = DE4AMarshaller.drImRequestMarshaller().getAsDocument(request);
		if (ObjectUtils.isEmpty(request.getDataOwner().getAgentUrn())) {
			//TODO assertion is assured by required fields
			return null;
		}
		return handleRequestTransferEvidence(request.getDataEvaluator().getAgentUrn(), request.getDataOwner().getAgentUrn(), doc.getDocumentElement(),
				request.getRequestId(), request.getCanonicalEvidenceTypeId());
	}

	private ResponseTransferEvidenceType handleRequestTransferEvidence(String from, String dataOwnerId,
			Element documentElement, String requestId, String canonicalEvidenceTypeId) {
		boolean ok = false;
		try {
			ok = sendRequestMessage(from, dataOwnerId, documentElement, canonicalEvidenceTypeId);
		} catch (Exception e) {
			MessageException me = new MessageException(e.getMessage());
			return responseManager.getErrorResponse(me);
		}
		if (!ok) {
			return null;
		}
		try {
			ok = waitResponse(requestId);

		} catch (InterruptedException e) {
			logger.error("Error waiting for response", e);
			Thread.currentThread().interrupt();
			return null;
		}
		if (!ok) {
			logger.error("No response before timeout");
			return null;
		}
		try {
			return responseManager.getResponse(requestId);
		} catch (MessageException e) {
			return responseManager.getErrorResponse(e);
		}
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
		String uriSmp = SMPUtils.getSmpUri(smpEndpoint, dataOwnerId, canonicalEvidenceTypeId);
		String senderId = sender;
		NodeInfo nodeInfo = client.getNodeInfo(uriSmp, false);
		if(sender.contains(TCIdentifierFactory.PARTICIPANT_SCHEME + DE4AConstants.DOUBLE_SEPARATOR)) {
			senderId = sender.replace(TCIdentifierFactory.PARTICIPANT_SCHEME + DE4AConstants.DOUBLE_SEPARATOR, "");
		}
		try {
			logger.debug("Sending  message to as4 gateway ...");
			Element requestSillyWrapper = new RegRepTransformer().wrapMessage(userMessage, true);
			List<TCPayload> payloads = new ArrayList<>();
			TCPayload p = new TCPayload();
			p.setContentID(DE4AConstants.TAG_EVIDENCE_REQUEST);
			p.setMimeType(CMimeType.APPLICATION_XML.getAsString());
			p.setValue(DOMUtils.documentToByte(userMessage.getOwnerDocument()));
			payloads.add(p);
			as4Client.sendMessage(senderId, nodeInfo, dataOwnerId, requestSillyWrapper, payloads, false);
			return true;
		} catch (MEOutgoingException e) {
			logger.error("Error with as4 gateway comunications", e);
		} catch (MessageException e) {
			logger.error("Error building regrep message", e);
		}
		return false;
	}

}
