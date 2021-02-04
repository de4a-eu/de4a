package eu.de4a.scsp.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.connector.api.rest.TCPayload;
@Component
public class ScspGateway implements OwnerGateway{  
	private static final Logger logger = LogManager.getLogger(ScspGateway.class);
	private static final String XPATH_SCSP_RESPONSE="//*[local-name()='Respuesta']";
	@Value("${as4.pid.owner.endpoint}")
	private String endpoint; 
	@Autowired
	private RestTemplate restTemplate;
	public List<TCPayload> sendEvidenceRequest(org.w3c.dom.Element evidenceRequest) throws MessageException{
		if(logger.isDebugEnabled()) { 
			logger.debug("Request: {}",DOMUtils.documentToString(evidenceRequest.getOwnerDocument()));
		}
//		 
//		RestTemplate plantilla = new RestTemplate();
//		HttpComponentsClientHttpRequestFactory requestFactory =  new HttpComponentsClientHttpRequestFactory();
//		requestFactory.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
//		plantilla.setRequestFactory(requestFactory);
//		MultiValueMap<String, Object> body  = new LinkedMultiValueMap<>();  
//		body.add("request",unmarshallMe(evidenceRequest)); 
// 		HttpHeaders headers = new HttpHeaders();
// 		headers.setContentType(org.springframework.http.MediaType.APPLICATION_XML ); 
// 		HttpEntity<MultiValueMap<String, Object>> requestEntity = new    HttpEntity<MultiValueMap<String, Object>>( 	body, headers); 
// 		return (List<TCPayload>)plantilla.postForEntity( endpoint,requestEntity,Object.class);   
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.ALL )); 

		HttpEntity<RequestTransferEvidence> entity = new HttpEntity<>( unmarshallMe(evidenceRequest), headers); 
	//	entity.getBody().getDataRequestSubject().getDataSubjectPerson().getDateOfBirth().   setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(); 
		messageConverters.add(converter);
//		FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();  
//		messageConverters.add(formHttpMessageConverter);messageConverters.add(new AllEncompassingFormHttpMessageConverter());
//		messageConverters.add(new org.springframework.integration.http.converter.MultipartAwareFormHttpMessageConverter());
		messageConverters.add(new ResourceHttpMessageConverter());
		restTemplate.setMessageConverters(messageConverters);
		ResponseEntity<Resource>files =	  restTemplate.postForEntity( endpoint,  entity,  Resource    .class);
		 //	org.springframework.http.HttpEntity<MultiValueMap<String,MultipartFile>>  files= restTemplate.postForObject(endpoint, unmarshallMe(evidenceRequest) ,org.springframework.http.HttpEntity.class);
		try {
			return unzipFile(files.getBody());
		} catch (IOException e) {
			throw new MessageException("Error comunicaciones con Owner pid:"+e.getMessage());
		}// makePayloads((org.springframework.http.HttpEntity<MultiValueMap<String,MultipartFile>> )files) ;
	} 
	private  List<TCPayload> unzipFile(Resource resource) throws IOException { 
			List<TCPayload> payloads=new ArrayList<TCPayload>(); 
	        File temp=Files.createTempFile("de4a-", null).toFile();
	        IOUtils.copy(resource.getInputStream(), new FileOutputStream(temp));
	        ZipFile zip=new ZipFile(temp);
	        Enumeration<ZipEntry>entries=(Enumeration<ZipEntry>) zip.entries();
	        while( entries .hasMoreElements()) {
	        	ZipEntry entry=entries.nextElement();
	        	byte[]data=zip.getInputStream(entry).readAllBytes();
	        	TCPayload payload=new TCPayload();
	        	String name= getName(data, entry.getName());
				payload.setContentID(name);
				if(name.toLowerCase().endsWith("xml"))payload.setMimeType(MediaType.APPLICATION_XML_VALUE);
				else payload.setMimeType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				payload.setValue(data);
				payloads.add(payload);
	        }  
	        zip.close();
	        return payloads;
	    } 
	private String getName(byte[] data,String name) {
		try {
			Document doc=DOMUtils.byteToDocument(data);
			String value=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVIDENCE_RESPONSE, doc.getDocumentElement());
			if(value!=null &&!value.isEmpty())return DE4AConstants.TAG_EVIDENCE_RESPONSE;
			value=DOMUtils.getValueFromXpath(XPATH_SCSP_RESPONSE, doc.getDocumentElement());
			if(value!=null &&!value.isEmpty())return  DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE;
		} catch (Throwable e) {
			
		}
		return name;
	}
	private List<TCPayload> makePayloads(org.springframework.http.HttpEntity<MultiValueMap<String,MultipartFile>> files){
		List<TCPayload> payloads=new ArrayList<TCPayload>();
		for(String key:files.getBody().keySet()) {
			files.getBody().get(key).forEach(f->{
				TCPayload payload=new TCPayload();
				payload.setContentID(f.getName());
				payload.setMimeType(f.getContentType());
				try {
					payload.setValue(f.getBytes());
				} catch (IOException e) {
					logger.error("Error managing attached bytes",e);
				}
				payloads.add(payload);
			});
		}
		return payloads;
	}
	private  eu.de4a.conn.api.requestor.RequestTransferEvidence  unmarshallMe(Element request)  {  
        try
        {  
        	DOMUtils.stringToDocument(DOMUtils.documentToString(request.getOwnerDocument())); 
            JAXBContext jaxbContext = JAXBContext.newInstance(eu.de4a.conn.api.requestor.RequestTransferEvidence.class); 
            javax.xml.bind.Unmarshaller jaxbMarshaller = (Unmarshaller) jaxbContext.createUnmarshaller() ;  
            return (eu.de4a.conn.api.requestor.RequestTransferEvidence) jaxbMarshaller.unmarshal( request );  
 
        } catch (Exception e) {
        	logger.error("error:",e);
           return null;
        }  
}
	 
	
}
 