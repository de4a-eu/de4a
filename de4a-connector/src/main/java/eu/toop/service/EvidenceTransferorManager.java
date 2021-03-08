package eu.toop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.conn.api.smp.NodeInfo;
import eu.de4a.conn.owner.MessageOwner;
import eu.de4a.conn.owner.MessageResponseOwner;
import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.exception.MessageException;
import eu.de4a.model.EvidenceEntity;
import eu.de4a.model.RequestorRequest;
import eu.de4a.repository.RequestorRequestRepository;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.FileUtils;
import eu.de4a.util.SMPUtils;
import eu.toop.as4.client.regrep.CletusLevelTransformer;
import eu.toop.as4.owner.OwnerLocator;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;
import eu.toop.rest.Client;

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
	@Autowired
	private ApplicationEventMulticaster applicationEventMulticaster;
	
	public void queueMessage(MessageOwner request) {
		Element canonicalResponse = null;
		EvidenceEntity evidenceEntity = null;
		if (logger.isDebugEnabled()) {
			logger.debug("Queued a message to send a owner implementation:");
			logger.debug(DOMUtils.documentToString(request.getMessage().getOwnerDocument()));
		}
		try {
			evidenceEntity = ownerLocator.lookupEvidence(request.getEvidenceService());

		} catch (MessageException e) {
			logger.error("It´s not exists a evidence with name {}", request.getEvidenceService());
			// TODO gestion de errores chachi
		}
		String from = meId;
		// Se almacena la informacion de la request
		RequestorRequest requestorReq = new RequestorRequest();
		requestorReq.setIdrequest(request.getId());
		requestorReq.setEvidenceServiceUri(request.getEvidenceService());
		requestorReq.setReturnServiceUri(request.getReturnService());
		requestorReq.setSenderId(request.getSenderId());
		requestorReq.setDone(false);
		requestorRequestRepository.save(requestorReq);
		OwnerGateway gateway = null;
		try {
			gateway = ownerLocator.getOwnerGateway(evidenceEntity);
		} catch (ConfigurationException | NoSuchMessageException | MessageException e) {
			logger.error("Fail...", e);
			// TODO handler error
		}
		if(gateway != null && evidenceEntity != null) {
			RequestTransferEvidence req = (RequestTransferEvidence) DOMUtils
					.unmarshall(RequestTransferEvidence.class, request.getMessage().getOwnerDocument());
			if (StringUtils.isEmpty(req.getDataOwner().getUrlRedirect())) {
				try {
					canonicalResponse = client.sendEvidenceRequest(req, evidenceEntity.getEndpoint(), 
							evidenceEntity.getXpathResponse(), false);
				} catch (NoSuchMessageException | MessageException e) {
					logger.error("Fail...", e);
					// TODO handler error
				}
				
				String uriSmp = SMPUtils.getSmpUri(smpEndpoint, request.getReturnService());			
				sendResponseMessage(from, uriSmp, request.getId(), canonicalResponse);
			} else {
				try {
					client.sendEvidenceRequest(req, evidenceEntity.getEndpoint(), 
							evidenceEntity.getXpathResponse(), true);
				} catch (MessageException e) {
					logger.error("Fail...",e);
					//TODO handler error 
				}
			}
		}
	}
	
	public void queueMessageResponse(MessageResponseOwner response) {
		if (logger.isDebugEnabled()) {
			logger.debug("Queued a response from USI-Pattern owner:");
			logger.debug(DOMUtils.documentToString(response.getMessage().getOwnerDocument()));
		}
		RequestorRequest usirequest = requestorRequestRepository.findById(response.getId()).orElse(null);
		if (usirequest == null) {
			logger.error("Not located a request with ID {}", response.getId());
		} else {
			String uriSmp = SMPUtils.getSmpUri(smpEndpoint, usirequest.getReturnServiceUri());
			sendResponseMessage(meId, uriSmp, usirequest.getIdrequest(), response.getMessage());
		}
	}	
	
	public boolean sendResponseMessage(String sender, String uriSmp, String id, Element canonicalResponse) {
		// TODO hacer algo decente para el parseo de ids de evidencias.
		NodeInfo nodeInfo = client.getNodeInfo(uriSmp, true);
		try {
			logger.debug("Sending  message to as4 gateway ...");

			// TODO actualizar el as4 client, ya no se van a manejar una lista de payloads
			List<TCPayload> payloads = new ArrayList<>();
			TCPayload payload = new TCPayload();
			payload.setContentID(DE4AConstants.TAG_EVIDENCE_RESPONSE);
			payload.setValue(DOMUtils.documentToByte(canonicalResponse.getOwnerDocument()));
			payload.setMimeType("application/xml");
			payloads.add(payload);
			Element requestSillyWrapper = new CletusLevelTransformer().wrapMessage(canonicalResponse, false);
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
