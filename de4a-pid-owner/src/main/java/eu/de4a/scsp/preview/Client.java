package eu.de4a.scsp.preview;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.de4a.conn.api.rest.Ack;
import eu.de4a.exception.MessageException;
import eu.de4a.util.FileUtils;
import eu.de4a.util.RestUtils;

@Component
public class Client {
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	
	@Value("#{'${de4a.transferor.endpoint.jvm:${de4a.transferor.endpoint:}}'}")
	private String transferorEndPoint;

	public Ack sendTransferEvidenceUsi(ResponseEntity<Resource> responseEntity) throws MessageException {
		logger.debug("Sending RequestTransferEvidence USI to transferor");
		byte[] evidenceResponseStream = null;
		try {
			if(responseEntity != null && responseEntity.getBody() != null) {
				evidenceResponseStream = FileUtils.buildResponse(responseEntity.getBody().getInputStream(), 
						"//*[local-name()='Respuesta']");
			}
		} catch (IOException e) {
			logger.error("There was a problemm processing response inputStream", e);
		}
		
		String urlRequest = transferorEndPoint + "/transferEvidenceUSI";		
		RestTemplate plantilla = RestUtils.getRestTemplate();
        plantilla.getMessageConverters().add(new ResourceHttpMessageConverter());        
		ResponseEntity<Ack> response = plantilla.postForEntity(urlRequest, evidenceResponseStream,
				Ack.class);
		return response.getBody();
	}
	
}
