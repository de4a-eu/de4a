package eu.toop.rest;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.de4a.conn.api.requestor.EvidenceServiceType;
import eu.de4a.conn.api.requestor.RequestLookupEvidenceServiceData;
import eu.de4a.conn.api.requestor.RequestLookupRoutingInformation;
import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.requestor.ResponseLookupEvidenceServiceData;
import eu.de4a.conn.api.requestor.ResponseLookupRoutingInformation;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.evaluator.request.RequestBuilder;
import eu.de4a.exception.MessageException;
import eu.de4a.util.RestUtils;
import eu.toop.controller.ResponseManager;
import eu.toop.controller.User;
 

@Component
public class Client {
	private static final Logger logger = LogManager.getLogger(Client.class); 
	@Value("${de4a.connector.url.return}")
	private String urlReturn; 
	@Value("${de4a.connector.url.requestor}")
	private String urlRequestor; 
	@Value("${de4a.connector.id.seed}")
	private String seed;
	@Autowired
	private RequestBuilder requestBuilder; 
	@Autowired
	private ResponseManager responseManager; 
	
	public ResponseLookupRoutingInformation getRoutingInfo(RequestLookupRoutingInformation request) throws MessageException {
		logger.debug("Sending lookup routing information request {}", request);
		String urlRequest = urlRequestor + "/lookupRouting";
		
		RestTemplate plantilla = RestUtils.getRestTemplate();
		ResponseEntity<ResponseLookupRoutingInformation> response = plantilla.postForEntity(urlRequest, request, 
				ResponseLookupRoutingInformation.class);
		
		return response.getBody();		
	}
	
	public ResponseLookupEvidenceServiceData getLookupServiceData(RequestLookupEvidenceServiceData request) {
		logger.debug("Sending lookup service data request {}", request);
		String urlRequest = urlRequestor + "/lookupEvidenceService";
		
		RestTemplate plantilla =RestUtils.getRestTemplate();
		ResponseEntity<ResponseLookupEvidenceServiceData> response = plantilla.postForEntity(urlRequest, request, 
				ResponseLookupEvidenceServiceData.class);
		
		return response.getBody();
	}
	
	public boolean getEvidenceRequestIM (RequestTransferEvidence request) throws MessageException 
	{
		logger.debug("Sending IM request {}", request.getRequestId());
		String urlRequest = urlRequestor + "/request";

		RestTemplate plantilla = RestUtils.getRestTemplate();
		ResponseEntity<ResponseTransferEvidence> response = plantilla.postForEntity(urlRequest, request,
				ResponseTransferEvidence.class);
		responseManager.manageResponse(response.getBody());
		return response.getBody().getError() == null;
	}

	public boolean getEvidenceRequestUSI (RequestTransferEvidence request) throws MessageException 
	{
		logger.debug("Sending USI equest {}", request.getRequestId());
		String urlRequest = urlRequestor + "/requestUSI";

		RestTemplate plantilla = RestUtils.getRestTemplate();
		ResponseEntity<Ack> ack = plantilla.postForEntity(urlRequest, request, Ack.class);
		return ack.getBody().getCode().equals(Ack.OK);
	}

	public RequestTransferEvidence buildRequest(User user, EvidenceServiceType evidenceServiceType) {
		String requestId = seed + "-" + Calendar.getInstance().getTimeInMillis();
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

		RequestTransferEvidence request = requestBuilder.buildRequest(requestId, evidenceServiceType, eidasId,
				birthDate, name, ap1, fullname);
		return request;
	}
}
 