package eu.de4a.scsp.ws.server;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.exception.MessageException;
import eu.de4a.scsp.preview.manager.PreviewManager;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;

@Controller  
public class RequestPidController {
	private static final Logger logger =  LoggerFactory.getLogger (RequestPidController.class);
	@Autowired
	private PreviewManager previewManager;
	
		
	@PostMapping(value = "/request", consumes = { "application/xml", "application/json" }, produces = {
			MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public ResponseEntity<Resource> sendRequest(@RequestBody RequestTransferEvidence request) throws MessageException {

		logger.debug("Received Canonical Request for being  sent to PID ");
		Element evidenceRequest = marshall(request).getDocumentElement();
		
		return previewManager.getPIDResponse(evidenceRequest);
	}
	
	@PostMapping(value = "/requestUSI", consumes = { "application/xml", "application/json" }, produces = { "application/xml" })
	public @ResponseBody Ack requestUsi(@RequestBody RequestTransferEvidence request) throws MessageException {
		logger.debug("Received Canonical Request by USI pattern");
		
		// Register request
		Document evidenceRequest = marshall(request);
		previewManager.registerRequestorRequestData(request, evidenceRequest.getDocumentElement(), 
				false, DE4AConstants.TAG_EVIDENCE_REQUEST);
		
		Ack ack = new Ack();
		ack.setCode(Ack.OK);
		ack.setMessage("Success");
		
		return ack;
	}
 
	/* 
	@PostMapping(value = "/request", consumes = {"application/xml","application/json"}, produces = { MediaType.MULTIPART_FORM_DATA_VALUE }  )
	public     ResponseEntity<MultiValueMap<String, Object>>    sendRequest(@RequestBody RequestTransferEvidence request, HttpServletResponse response) throws MessageException {   
 
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
			
			MultipartFile[] payloads=new MultipartFile[2];
			MultipartFile multipartCanonical=
					getMultipart(DE4AConstants.TAG_EVIDENCE_RESPONSE, MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(evidenceResponse.getOwnerDocument()));
			 
			MultipartFile multipartScsp=
					getMultipart(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE, MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(scspResponse.getOwnerDocument()));
			 
			 
			payloads[0]=multipartCanonical;
			payloads[1]=multipartScsp;
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE); 
			 
			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
			parts.add(DE4AConstants.TAG_EVIDENCE_RESPONSE,    new FileSystemResource(convert(multipartCanonical)));
			parts.add(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE,    new FileSystemResource(convert(multipartScsp)));
			  
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			ResponseEntity<MultiValueMap<String, Object>> responseHttp = new ResponseEntity<MultiValueMap<String, Object>>(parts,  HttpStatus.OK) ;
			  
			  
			return responseHttp; 
					
	} */
	
	
	private Document marshall(RequestTransferEvidence request ) {   
        try
        {
        	Date date=new Date(request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth().toGregorianCalendar().getTimeInMillis(  ));
        	Calendar time=Calendar.getInstance( );  
        	time.setTime(date);
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setDay(time.get(Calendar.DAY_OF_MONTH));
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setMonth(time.get(Calendar.MONTH)+1);
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setYear(time.get(Calendar.YEAR));  
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            JAXBContext jaxbContext = JAXBContext.newInstance(RequestTransferEvidence.class);
            javax.xml.bind.Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
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
