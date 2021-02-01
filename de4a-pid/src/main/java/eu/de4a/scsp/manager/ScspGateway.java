package eu.de4a.scsp.manager;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Element;

import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException; 
import eu.toop.connector.api.rest.TCPayload;
@Component
public class ScspGateway implements OwnerGateway{  
	private static final Logger logger = LogManager.getLogger(ScspGateway.class);
	@Value("${as4.pid.owner.endpoint}")
	private String endpoint; 
	public List<TCPayload> sendEvidenceRequest(org.w3c.dom.Element evidenceRequest) throws MessageException{
		if(logger.isDebugEnabled()) { 
			logger.debug("Request: {}",DOMUtils.documentToString(evidenceRequest.getOwnerDocument()));
		}
		 
		RestTemplate plantilla = new RestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory =  new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
		plantilla.setRequestFactory(requestFactory);
		MultiValueMap<String, Object> body  = new LinkedMultiValueMap<>();  
		body.add("request",evidenceRequest); 
 		HttpHeaders headers = new HttpHeaders();
 		headers.setContentType(org.springframework.http.MediaType.APPLICATION_XML ); 
 		HttpEntity<MultiValueMap<String, Object>> requestEntity = new    HttpEntity<MultiValueMap<String, Object>>( 	body, headers); 
 		return (List<TCPayload>)plantilla.postForObject( endpoint,unmarshallMe(evidenceRequest),List.class);  
		
	} 
	
	private  eu.de4a.conn.api.requestor.RequestTransferEvidence  unmarshallMe(Element request)  {  
        try
        {  
        	DOMUtils.documentToString(request.getOwnerDocument());
            JAXBContext jaxbContext = JAXBContext.newInstance(eu.de4a.conn.api.requestor.RequestTransferEvidence.class);
            javax.xml.bind.Unmarshaller jaxbMarshaller = (Unmarshaller) jaxbContext.createUnmarshaller() ;  
            return (eu.de4a.conn.api.requestor.RequestTransferEvidence) jaxbMarshaller.unmarshal(request); 
 
        } catch (Exception e) {
        	logger.error("error:",e);
           return null;
        }  
}
	
}
 