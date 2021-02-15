package eu.toop.service;

import java.util.List;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.smp.NodeInfo;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.as4.client.regrep.CletusLevelTransformer;
import eu.toop.as4.owner.MessageOwner;
import eu.toop.as4.owner.OwnerLocator;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;
import eu.toop.req.model.RequestorRequest;
import eu.toop.req.repository.RequestorRequestRepository;
import eu.toop.rest.Client;

@Component 
public class EvidenceTransferorManager extends EvidenceManager {
	private static final Logger logger = LoggerFactory.getLogger (EvidenceTransferorManager.class);	
	
	@Value("${as4.me.id.jvm:#{null}}")
	private String meIdjvm; 
	@Autowired
	private Client clientSmp;
	@Autowired
	private OwnerLocator ownerLocator;
	@Autowired
	private RequestorRequestRepository requestorRequestRepository;
	
	  public void yourfather( MessageOwner request) {
//		  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//	      DocumentBuilder dBuilder=null;
//		try {
//			dBuilder = factory.newDocumentBuilder();
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	      Document  docresponse=null;
//		try {  
//			docresponse = dBuilder.parse(this.getClass().getClassLoader()  .getResourceAsStream( "edm/response.xml"));
//			DOMUtils.changeNodo(docresponse, DE4AConstants.XPATH_ID, id);
//		} catch (SAXException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		  List<TCPayload> payloads=null;
		try {
			payloads = ownerLocator.getOwnerGateway(request.getEvidenceService()).sendEvidenceRequest(request.getMessage());
		} catch (ConfigurationException | MessageException e) {
			// TODO gestion de errores chachi
		  
		} 
		String from= meIdjvm;
		
		//Se almacena la informacion de la request
		RequestorRequest requestorReq = new RequestorRequest();
		requestorReq.setIdrequest(request.getId());
		requestorReq.setEvidenceServiceUri(request.getEvidenceService());
		requestorReq.setSenderId(request.getSenderId());
		requestorReq.setDone(false);
		requestorRequestRepository.save(requestorReq);
		
		sendRequestMessage( from, request.getEvidenceService(), request.getId(), payloads);
	  }
	public boolean sendRequestMessage(String sender, String evidenceService, String id, List<TCPayload> payloads ) {
		NodeInfo nodeInfo=clientSmp.getNodeInfo(evidenceService);
		try {
			logger.debug("Sending  message to as4 gateway ...");
			TCPayload canonicalpay=payloads.stream().filter(p->p.getContentID().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)).findFirst().orElse(null);
			Document tosilly=DOMUtils.byteToDocument(canonicalpay.getValue()); 
			Element requestSillyWrapper=new CletusLevelTransformer().wrapMessage(tosilly.getDocumentElement(), false);
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
