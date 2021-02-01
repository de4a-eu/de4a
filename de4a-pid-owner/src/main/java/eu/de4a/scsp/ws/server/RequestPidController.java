package eu.de4a.scsp.ws.server;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.scsp.translate.EvidenceMapper;
import eu.de4a.scsp.translate.EvidenceTranslator;
import eu.de4a.scsp.ws.client.ClientePidWS;
import eu.de4a.util.DE4AConstants;
import eu.toop.connector.api.rest.TCPayload;

@Controller  
public class RequestPidController {
	private static final Logger logger =  LoggerFactory.getLogger (RequestPidController.class); 
	private static final String XPATH_SERVICE_URI="//*[local-name()='EvidenceServiceURI']";
	@Autowired
	private ClientePidWS clientePidWS;  
	@Autowired
	private EvidenceMapper evidenceMapper; 
	 
	@PostMapping(value = "/request", consumes = {"application/xml","application/json"}, produces = {  "application/xml" }  )
	public  @ResponseBody List<TCPayload>  sendRequest(@RequestBody RequestTransferEvidence request) throws MessageException {   
 
			logger.debug("Received Canonical Request for being  sent to PID ");
			Element evidenceRequest= marshall(request).getDocumentElement();
			String evidenceServiceUri= DOMUtils.getValueFromXpath(XPATH_SERVICE_URI, evidenceRequest);
			EvidenceTranslator translator=evidenceMapper.getTranslator(evidenceServiceUri);
			Element scspRequest=translator.translateEvidenceRequest(evidenceRequest);
			Element scspResponse=(Element) clientePidWS.sendRequest(scspRequest);
			String id=DOMUtils.getNodeFromXpath(String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_REQUEST), evidenceRequest).getNodeValue();
			String idevaluator=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_EVALUATOR_ID, evidenceRequest);
			String nameevaluator=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_EVALUATOR_NAME, evidenceRequest);
			String idowner=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_OWNER_ID, evidenceRequest);
			String nameownerr=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_OWNER_NAME, evidenceRequest);
			
			Element evidenceResponse= translator.translateEvidenceResponse(scspResponse) ;
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(), String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_RESPONSE), id);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_EVALUATOR_ID_NODE   , idevaluator);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_EVALUATOR_NAME_NODE   , nameevaluator);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_OWNER_NAME_NODE   , nameownerr );
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_OWNER_ID_NODE   , idowner );
			
			List<TCPayload> payloads=new ArrayList<TCPayload>();
			
			TCPayload p=new TCPayload();
			 p.setContentID(DE4AConstants.TAG_EVIDENCE_RESPONSE);
			 p.setMimeType(MediaType.APPLICATION_XML.toString());
			 p.setValue(DOMUtils.documentToByte(evidenceResponse.getOwnerDocument()));
			 payloads.add(p);
			 
			 TCPayload scsp=new TCPayload();
			 scsp.setContentID(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE);
			 scsp.setMimeType(MediaType.APPLICATION_XML.toString() );
			 scsp.setValue(DOMUtils.documentToByte(scspResponse.getOwnerDocument()));
			 payloads.add(scsp);
			return payloads;
					
	} 
	private Document marshall(RequestTransferEvidence request ) {   
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(RequestTransferEvidence.class);
            javax.xml.bind.Marshaller jaxbMarshaller = (Marshaller) jaxbContext.createMarshaller(); 
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter(); 
            jaxbMarshaller.marshal(request, sw); 
            return DOMUtils.stringToDocument(sw.toString()); 
 
        } catch ( Exception e) {
           logger.error("Error building request DOM",e);
           return null;
        } 
}
}
