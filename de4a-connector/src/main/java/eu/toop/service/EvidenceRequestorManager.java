package eu.toop.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.mime.CMimeType;

import eu.de4a.conn.api.requestor.ErrorListType;
import eu.de4a.conn.api.requestor.ErrorType;
import eu.de4a.conn.api.requestor.EvidenceServiceType;
import eu.de4a.conn.api.requestor.IssuingAuthorityType;
import eu.de4a.conn.api.requestor.RequestLookupEvidenceServiceData;
import eu.de4a.conn.api.requestor.RequestLookupRoutingInformation;
import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.requestor.ResponseLookupEvidenceServiceData;
import eu.de4a.conn.api.requestor.ResponseLookupRoutingInformation;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.conn.api.smp.NodeInfo;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.as4.client.regrep.CletusLevelTransformer;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;
import eu.toop.rest.Client;

@Component 
public class EvidenceRequestorManager extends EvidenceManager{

	private static final Logger logger = LoggerFactory.getLogger (EvidenceRequestorManager.class);
	@Value("#{'${as4.me.id.jvm:${as4.me.id:}}'}")
	private String meId;
	@Value("#{'${idk.endpoint.jvm:${idk.endpoint:}}'}")
	private String idkEndpoint;
	@Value("${as4.evidence.service}")
	private String evidenceServiceUri;
	@Value("${as4.timeout.miliseconds:#{60000}}") 
	private long timeout;
	
	@Autowired
	private Client clientSmp; 
	@Autowired
	private ResponseManager responseManager;  
	
	public ResponseLookupRoutingInformation manageRequest(RequestLookupRoutingInformation request) {
		ResponseLookupRoutingInformation response = new ResponseLookupRoutingInformation();
		response.setError(new ErrorType());
		response.setEvidenceService(new EvidenceServiceType());
		
		if(!StringUtils.isEmpty(request.getCanonicalEvidenceId()) && !StringUtils.isEmpty(request.getCountry())) {
			//Se realiza la llamada al idk para obtener la lista de posibles candidatos
			IssuingAuthorityType issuingAuthority = clientSmp.getIssuingAuthority(request.getCanonicalEvidenceId(), request.getCountry());
			if(issuingAuthority != null) {
				response.setIssuingAuthority(issuingAuthority);
			}
		} else {
			response.getError().setCode("COD_EMPTY");
			response.getError().setText("Request mal formada. Falta informar campos obligatorios");
		}
		
		return response;
	}
	
	public ResponseLookupEvidenceServiceData manageRequest(RequestLookupEvidenceServiceData request) {
		ResponseLookupEvidenceServiceData response = new ResponseLookupEvidenceServiceData();
		response.setErrorList(new ErrorListType());
		response.setEvidenceService(new EvidenceServiceType());
		
		if(!StringUtils.isEmpty(request.getAdminTerritorialUnit()) && !StringUtils.isEmpty(request.getCanonicalEvidenceId()) 
				&& !StringUtils.isEmpty(request.getCountry())) {
			response.setEvidenceService(clientSmp.getEvidenceService(request.getCanonicalEvidenceId(), request.getCountry(), "atuCode", 
							request.getAdminTerritorialUnit()));
		} else {
			ErrorType error = new ErrorType();
			error.setCode("COD_EMPTY");
			error.setText("Request mal formada. Falta informar campos obligatorios");
			response.getErrorList().getError().set(0, new ErrorType());
		}
		
		return response;
	}
	
	public boolean manageRequest(RequestTransferEvidence request,boolean usi) {
		String from= meId;
		Document doc=marshall(request);
		
		if(StringUtils.isEmpty(request.getEvidenceServiceData().getEvidenceServiceURI())) {		
			//Se realiza la llamada al idk para obtener la lista de posibles candidatos
			IssuingAuthorityType issuingAuthority = clientSmp.getIssuingAuthority(request.getCanonicalEvidenceId(), "ES");
			EvidenceServiceType evidenceService;
			
			//En este caso solo habra un item en OrganisationalStructure con lo que se vuelve a consultar incluyendo el atuCode de ese item
			if(issuingAuthority != null && !CollectionUtils.isEmpty(issuingAuthority.getIaOrganisationalStructure())) {
				evidenceService = 
						clientSmp.getEvidenceService(request.getCanonicalEvidenceId(), "ES", "atuCode", 
								issuingAuthority.getIaOrganisationalStructure().get(0).getAtuCode());
				if(evidenceService != null && !StringUtils.isEmpty(evidenceService.getDataTransferor()) 
						&& !StringUtils.isEmpty(evidenceService.getService()) && doc != null) {
					return sendRequestMessage(from, evidenceService.getService(), doc.getDocumentElement());
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return sendRequestMessage(from, request.getEvidenceServiceData().getEvidenceServiceURI(), doc.getDocumentElement());
	}
	public ResponseTransferEvidence manageRequestIM(RequestTransferEvidence request )  {
		String from= meId;
		Document doc=marshall(request);   
		
		if(StringUtils.isEmpty(request.getEvidenceServiceData().getEvidenceServiceURI())) {
			//Se realiza la llamada al idk para obtener la lista de posibles candidatos
			IssuingAuthorityType issuingAuthority = clientSmp.getIssuingAuthority(request.getCanonicalEvidenceId(), "ES");
			EvidenceServiceType evidenceService;
			
			//En este caso solo habra un item en OrganisationalStructure con lo que se vuelve a consultar incluyendo el atuCode de ese item
			if(issuingAuthority != null && !CollectionUtils.isEmpty(issuingAuthority.getIaOrganisationalStructure()) 
					&& issuingAuthority.getIaOrganisationalStructure().size() == 1) {
				evidenceService = 
						clientSmp.getEvidenceService(request.getCanonicalEvidenceId(), "ES", "atuCode", 
								issuingAuthority.getIaOrganisationalStructure().get(0).getAtuCode());
				if(evidenceService != null && !StringUtils.isEmpty(evidenceService.getDataOwner()) && doc != null) {
					return handleRequestTransferEvidence(from, evidenceService.getService(), 
							doc.getDocumentElement(), request.getRequestId());
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return handleRequestTransferEvidence(from, request.getEvidenceServiceData().getEvidenceServiceURI(), 
				doc.getDocumentElement(), request.getRequestId());
	}
	
	private ResponseTransferEvidence handleRequestTransferEvidence(String from, String evidenceServiceUri,
			Element documentElement, String requestId) {
		boolean ok = false;
		try {
			ok = sendRequestMessage(from, evidenceServiceUri, documentElement);
		} catch (Exception e) {
			MessageException me = new MessageException(e.getMessage());
			return responseManager.getErrorResponse(me);
		}
		if (!ok) {
			return null;
		}
		try {
			ok = waitAratito(requestId);

		} catch (InterruptedException e) {
			logger.error("Error waiting for response", e);
		}
		if (!ok) {
			logger.error("No se ha conseguido la repsuesta antes del timeout!!!");
			return null;
		}
		try {
			return responseManager.getResponse(requestId);
		} catch (MessageException e) {
			return responseManager.getErrorResponse(e);
		}
	}
	
	private boolean waitAratito(String id) throws InterruptedException { 
		long init=Calendar.getInstance().getTimeInMillis();
		boolean wait=!responseManager.isDone(id);
		boolean ok=!wait;
			while ( wait) {
			       logger.debug("Waiting for ThreadB to complete...");
			       Thread.sleep(500);
			       ok=responseManager.isDone(id);
			       wait=!ok && Calendar.getInstance().getTimeInMillis()-init<timeout;
			}  
			return ok;
		     
	} 
	private Document marshall(RequestTransferEvidence request ) {   
		        try  {  
		        	JAXBContext jc = JAXBContext.newInstance(RequestTransferEvidence.class); 
		            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		            dbf.setNamespaceAware(true);
		            DocumentBuilder db = dbf.newDocumentBuilder();
		            Document document = db.newDocument();

		            // Marshal Object to the Document
		            Marshaller marshaller = jc.createMarshaller();
		            marshaller.marshal(request, document); 
		            return document;
		        } catch (JAXBException | ParserConfigurationException e) {
		           logger.error("Error building request DOM",e);
		           return null;
		        } 
	}
	
	public boolean sendRequestMessage(String sender, String service, Element userMessage) {
		String uriSmp = clientSmp.getSmpUri(service);
		NodeInfo nodeInfo = clientSmp.getNodeInfo(uriSmp);
		try {
			logger.debug("Sending  message to as4 gateway ...");
			Element requestSillyWrapper = new CletusLevelTransformer().wrapMessage(userMessage, true);
			List<TCPayload> payloads = new ArrayList<>();
			TCPayload p = new TCPayload();
			p.setContentID(DE4AConstants.TAG_EVIDENCE_REQUEST);
			p.setMimeType(CMimeType.APPLICATION_XML.getAsString());
			p.setValue(DOMUtils.documentToByte(userMessage.getOwnerDocument()));
			payloads.add(p);
			as4Client.sendMessage(sender, nodeInfo, service, requestSillyWrapper, payloads, true);
			return true;
		} catch (MEOutgoingException e) {
			logger.error("Error with as4 gateway comunications", e);
		} catch (MessageException e) {
			logger.error("Error building regrep message", e);
		}
		return false;
	}
	

}
