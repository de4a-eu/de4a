package eu.toop.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.de4a.evaluator.request.RequestBuilder;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.AgentType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.toop.controller.ResponseManager;
import eu.toop.controller.User;
import nl.flotsam.xeger.Xeger;
 

@Component
public class Client {
	private static final Logger logger = LogManager.getLogger(Client.class); 
	@Value("${de4a.connector.url.requestor}")
	private String urlRequestor;
	@Autowired
	private RequestBuilder requestBuilder; 
	@Autowired
	private ResponseManager responseManager;
	@Autowired
	private RestTemplate restTemplate;
	
	public ResponseLookupRoutingInformationType getRoutingInfo(RequestLookupRoutingInformationType request) throws MessageException {
		logger.debug("Sending lookup routing information request {}", request);
		String urlRequest = urlRequestor + "/lookupRoutingInformation";
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.postForEntity(urlRequest, request, 
					String.class);
			var respMarshaller = DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller();
			ResponseLookupRoutingInformationType respObj = respMarshaller.read(response.getBody());
			return respObj;		
		}catch(Exception e) {
			logger.error("Error from DR",e);
			return null;
		}
		
		
	}
	
	public boolean getEvidenceRequestIM (RequestTransferEvidenceUSIIMDRType request) throws MessageException 
	{
		logger.debug("Sending IM request {}", request.getRequestId());
		String urlRequest = urlRequestor + "/requestTransferEvidenceIM";

		
		ResponseEntity<String> response = restTemplate.postForEntity(urlRequest, 
				 DE4AMarshaller.drImRequestMarshaller().getAsString(request), String.class);
		ResponseTransferEvidenceType respObj = DE4AMarshaller
				.drImResponseMarshaller(EDE4ACanonicalEvidenceType.NONE).read(response.getBody());
		responseManager.manageResponse(respObj);
		return respObj.getErrorList() == null;
	}

	public ResponseErrorType getEvidenceRequestUSI (RequestTransferEvidenceUSIIMDRType request) throws MessageException 
	{
		logger.debug("Sending USI request {}", request.getRequestId());
		String urlRequest = urlRequestor + "/requestTransferEvidenceUSI";

		
		ResponseEntity<String> response = restTemplate.postForEntity(urlRequest, 
				DE4AMarshaller.drUsiRequestMarshaller().getAsString(request), String.class);
		if(response.getBody() != null) {
			ResponseErrorType responseObj = DE4AMarshaller.drUsiResponseMarshaller().read(response.getBody());
			return responseObj;
		}
		return null;
	}

	public RequestTransferEvidenceUSIIMDRType buildRequest(User user, AgentType dataOwner, String canonicalEvidenceTypeId) {
		String regex = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[8-9a-bA-B][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";
		Xeger generator = new Xeger(regex);
		String requestId = generator.generate();
		logger.debug("building request {}", requestId);

		String eidasId = user.getEidas();
		String name = null, ap1 = null, ap2 = null, fullname = null, birthDate = null;
		if (user.getAp1() != null) {
			name = user.getName();
			ap1 = user.getAp1();
			ap2 = user.getAp2() != null && !user.getAp2().isEmpty() ? user.getAp2() : "";
			fullname = user.getName() + " " + user.getAp1() + " " + (ap2.isEmpty() ? "" : ap2);
			birthDate = user.getBirthDate();
		}

		RequestTransferEvidenceUSIIMDRType request = requestBuilder.buildRequest(requestId, dataOwner, canonicalEvidenceTypeId, eidasId,
				birthDate, name, ap1, fullname);
		return request;
	}
}
 