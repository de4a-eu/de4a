package eu.de4a.scsp.ws.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceUSIType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.scsp.preview.manager.PreviewManager;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.XDE4ACanonicalEvidenceType;
import eu.de4a.util.XDE4AMarshaller;

@Controller  
public class RequestController {
	private static final Logger logger =  LoggerFactory.getLogger (RequestController.class);
	@Autowired
	private PreviewManager previewManager;
	
		
	@PostMapping(value = "/requestExtractEvidenceIM")
	public @ResponseBody String sendRequest(@RequestBody String request) throws MessageException {

		logger.debug("Received RequestExtractEvidence - IM pattern ");
		RequestExtractEvidenceIMType requestObj = DE4AMarshaller.doImRequestMarshaller().read(request);
		ResponseExtractEvidenceType responseExtract = previewManager.getPIDResponseExtract(DE4AMarshaller
				.doImRequestMarshaller().getAsDocument(requestObj).getDocumentElement(), false);
		
		//temp Marshaller while IDE4ACanonicalEvidenceType for BirthCertificate is created
		return XDE4AMarshaller.doImResponseMarshaller(XDE4ACanonicalEvidenceType.BIRTH_CERTIFICATE)
				.getAsString(responseExtract);
	}
	
	@PostMapping(value = "/requestExtractEvidenceUSI")
	public @ResponseBody String requestUsi(@RequestBody String request) throws MessageException {
		logger.debug("Received RequestExtractEvidence - USI pattern");
		
		RequestExtractEvidenceUSIType requestObj = DE4AMarshaller.doUsiRequestMarshaller().read(request);
		
		// Register request
		previewManager.registerRequestorRequestData(requestObj, false, 
				DE4AConstants.TAG_EXTRACT_EVIDENCE_REQUEST);
		
		return DE4AMarshaller.doUsiResponseMarshaller().getAsString(DE4AResponseDocumentHelper
				.createResponseError(true));
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
					
	} 
	
	
	private Document marshall(RequestExtractEvidenceIMType request ) {   
        try
        {
        	Date date=new Date(request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth().toGregorianCalendar().getTimeInMillis(  ));
        	Calendar time=Calendar.getInstance( );  
        	time.setTime(date);
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setDay(time.get(Calendar.DAY_OF_MONTH));
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setMonth(time.get(Calendar.MONTH)+1);
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setYear(time.get(Calendar.YEAR));  
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            JAXBContext jaxbContext = JAXBContext.newInstance(RequestExtractEvidenceIMType.class);
            javax.xml.bind.Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter(); 
            jaxbMarshaller.marshal(request, sw); 
            return DOMUtils.stringToDocument(sw.toString()); 
 
        } catch ( Exception e) {
           logger.error("Error building request DOM",e);
           return null;
        } 
	}*/
}
