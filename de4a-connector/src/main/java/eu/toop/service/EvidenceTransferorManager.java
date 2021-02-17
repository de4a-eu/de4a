package eu.toop.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.smp.NodeInfo;
import eu.de4a.conn.owner.MessageResponseOwner;
import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.as4.client.regrep.CletusLevelTransformer;
import eu.toop.as4.owner.MessageOwner;
import eu.toop.as4.owner.OwnerLocator;
import eu.toop.as4.owner.OwnerMessageEventPublisher;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;
import eu.toop.req.model.EvidenceEntity;
import eu.toop.req.model.RequestorRequest;
import eu.toop.req.repository.RequestorRequestRepository;
import eu.toop.rest.Client;
import kotlin.collections.ArrayDeque;

@Component 
public class EvidenceTransferorManager extends EvidenceManager {
	private static final Logger logger = LoggerFactory.getLogger (EvidenceTransferorManager.class);	

	@Value("#{'${as4.me.id.jvm:${as4.me.id:}}'}")
	private String meId; 
	@Autowired
	private Client clientSmp;
	@Autowired
	private OwnerLocator ownerLocator;
	@Autowired
	private RequestorRequestRepository requestorRequestRepository;
	@Autowired
	private OwnerMessageEventPublisher publisher; 
	
	public void queueMessage( MessageOwner request) { 
		Element canonicalResponse=null;
		EvidenceEntity evidenceEntity=null;
		if(logger.isDebugEnabled()) { 
			logger.debug("Queued a message to send a owner implementation:");
			logger.debug(DOMUtils.documentToString(request.getMessage().getOwnerDocument()));
		}
		try {
			evidenceEntity=ownerLocator.lookupEvidence(request.getEvidenceService());
			
		} catch ( MessageException e) {
			logger.error("It´s not exists a evidence with name {}",request.getEvidenceService());
			// TODO gestion de errores chachi 
		} 
		String from=meId ; 
		//Se almacena la informacion de la request
		RequestorRequest requestorReq = new RequestorRequest();
		requestorReq.setIdrequest(request.getId());
		requestorReq.setEvidenceServiceUri(request.getEvidenceService());
		requestorReq.setSenderId(request.getSenderId());
		requestorReq.setDone(false);
		requestorRequestRepository.save(requestorReq);
		OwnerGateway gateway=null;
		try {
			gateway= ownerLocator.getOwnerGateway(evidenceEntity);
		} catch (ConfigurationException | NoSuchMessageException | MessageException  e) {
			logger.error("Fail...",e);
			//TODO handler error 
		}
		if(evidenceEntity.isUsi()==false) {
			try {
				canonicalResponse =gateway.sendEvidenceRequest(request.getMessage());
			} catch (NoSuchMessageException | MessageException e) {
				logger.error("Fail...",e);
				//TODO handler error 
			}
			sendRequestMessage( from, request.getEvidenceService(), request.getId(), canonicalResponse);
		} 
	}
	public void queueMessageResponse( MessageResponseOwner response) {
		if(logger.isDebugEnabled()) { 
			logger.debug("Queued a response from USI-Pattern owner:");
			logger.debug(DOMUtils.documentToString(response.getMessage().getOwnerDocument()));
		}
		RequestorRequest usirequest=requestorRequestRepository.findById(response.getId()).orElse(null) ;
		if(usirequest == null) {
			logger.error("Not located a request with ID {}",response.getId());
		}else {
			sendRequestMessage(meId,usirequest.getEvidenceServiceUri(), usirequest.getIdrequest(), response.getMessage());
		}
	}  
	public boolean sendRequestMessage(String sender, String evidenceService, String id,Element canonicalResponse ) {
		//TODO hacer algo decente para el parseo de ids de evidencias.
		String uriSmp=clientSmp.getSmpUri(evidenceService,sender);
		
		NodeInfo nodeInfo=clientSmp.getNodeInfo(uriSmp);
		try {
			logger.debug("Sending  message to as4 gateway ...");  
			
			//TODO actualizar el as4 client, ya no se van a manejar una lista de payloads
			List<TCPayload>payloads =new ArrayList<TCPayload>();
			TCPayload payload=new TCPayload();
			payload.setContentID(DE4AConstants.TAG_EVIDENCE_RESPONSE);
			payload.setValue(DOMUtils.documentToByte(canonicalResponse.getOwnerDocument()));
			payload.setMimeType("application/xml");
			payloads.add(payload);
			Element requestSillyWrapper=new CletusLevelTransformer().wrapMessage(canonicalResponse, false);
			as4Client.sendMessage(sender,nodeInfo, evidenceService,requestSillyWrapper,payloads,false);
			return true;
		}  catch (MEOutgoingException e) {
			logger.error("Error with as4 gateway comunications",e);
		} catch (MessageException e) {
			logger.error("Error building wrapper message",e);
		}
		return false;
	}
	
}
