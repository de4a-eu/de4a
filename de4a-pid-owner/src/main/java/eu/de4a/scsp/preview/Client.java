package eu.de4a.scsp.preview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.util.XDE4ACanonicalEvidenceType;
import eu.de4a.util.XDE4AMarshaller;

@Component
public class Client {
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("#{'${de4a.transferor.endpoint.jvm:${de4a.transferor.endpoint:}}'}")
	private String transferorEndPoint;

	public ResponseErrorType sendRequestForwardEvidence(RequestForwardEvidenceType requestForward) throws MessageException {
		logger.debug("Sending RequestForwardEvidence to transferor - USI pattern");
		
		String urlRequest = transferorEndPoint + "/requestForwardEvidence";
		
		String request = XDE4AMarshaller.deUsiRequestMarshaller(XDE4ACanonicalEvidenceType.BIRTH_CERTIFICATE).getAsString(requestForward);
		
		ResponseEntity<String> response = restTemplate.postForEntity(urlRequest, request,
				String.class);
		ResponseErrorType responseObj = null;
		if(!StringUtils.isEmpty(response.getBody())) {
			responseObj = DE4AMarshaller.dtUsiResponseMarshaller().read(response.getBody());
		} else {
			//TODO error handling
		}
		return responseObj;
	}
	
}
