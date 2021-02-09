package eu.toop.rest;

import java.util.Calendar;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.evaluator.request.RequestBuilder;
import eu.de4a.exception.MessageException;
import eu.de4a.util.EvidenceTypeIds;
import eu.toop.controller.User;
 

@Component
public class Client {
	private static final Logger logger = LogManager.getLogger(Client.class); 
	@Value("${de4a.connector.url.return}")
	private String urlReturn; 
	@Value("${de4a.connector.url.requestor.phase4}")
	private String urlRequestor; 
	@Value("${de4a.connector.id.seed}")
	private String seed;
	@Autowired
	private RequestBuilder requestBuilder; 
	public ResponseTransferEvidence getEvidenceRequestIM (RequestTransferEvidence request) throws MessageException 
	{   
		logger.debug("Sending request {}",request.getRequestId()); 
		RestTemplate plantilla = new RestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory =
		                new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
		plantilla.setRequestFactory(requestFactory);
		ResponseEntity<ResponseTransferEvidence> response= plantilla.postForEntity(urlRequestor,request, ResponseTransferEvidence.class);
		return response.getBody();
	} 
	public boolean getEvidenceRequestUSI (RequestTransferEvidence request) throws MessageException 
	{   
		logger.debug("Sending request {}",request.getRequestId()); 
		RestTemplate plantilla = new RestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory =
		                new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
		plantilla.setRequestFactory(requestFactory);
		ResponseEntity<Ack> ack= plantilla.postForEntity(urlRequestor,request, Ack.class);
		return ack.getBody().getCode().equals(Ack.OK)?  true:false;
	} 
	public RequestTransferEvidence buildRequest(User user) {
		String requestId=seed+"-"+Calendar.getInstance().getTimeInMillis();
		logger.debug("building request {}",requestId);
		// String uri = "http://localhost:8083/de4a-connector/request?urlReturn=http://localhost:8682/de4a-evaluator/ReturnPage&evaluatorId="+dataOwnerdI+"&@evaluatorId="+evidenceServiceUri+"&requestId=777"; 
		String eidasId=user.getEidas();
		String name=null ,ap1=null,ap2=null,fullname=null,birthDate=null;
		if(user.getAp1()!=null) {
			name=user.getName();
			ap1=user.getAp1();
			ap2=user.getAp2()!=null &&!user.getAp2().isEmpty()?user.getAp2():"";
			fullname= user.getName()+" "+user.getAp1()+" "+ (ap2.isEmpty()?"":ap2); 
			birthDate=user.getBirthDate();
		}
		
		RequestTransferEvidence request = requestBuilder.buildRequest(requestId,user.getEvidenceServiceURI(),eidasId,birthDate,name,ap1,fullname);
		request.setCanonicalEvidenceId(EvidenceTypeIds.BIRTHCERTIFICATE.toString());
		
		return request;
	}
}
 