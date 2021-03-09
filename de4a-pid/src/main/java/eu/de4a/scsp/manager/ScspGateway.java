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

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.conn.owner.MessageResponseOwner;
import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.FileUtils;
import eu.de4a.util.RestUtils;
import eu.toop.connector.api.rest.TCPayload;

@Component
public class ScspGateway implements OwnerGateway {
	private static final Logger logger = LogManager.getLogger(ScspGateway.class);
	private static final String XPATH_SCSP_RESPONSE = "//*[local-name()='Respuesta']";
	@Value("${as4.pid.owner.endpoint}")
	private String endpoint;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ApplicationContext context;

	@Override
	public void sendEvidenceRequestAsynchronous(String requestorId, Element evidenceRequest,
			ApplicationEventMulticaster applicationEventMulticaster) throws MessageException {
		MessageResponseOwner responseUSI = new MessageResponseOwner(context);
		responseUSI.setId(DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, evidenceRequest));
		responseUSI.setRequestorId(requestorId);
		Element response = sendEvidenceRequest(evidenceRequest, true);
		//responseUSI.setMessage(response);
		//applicationEventMulticaster.multicastEvent(responseUSI);
	}

	public Element sendEvidenceRequest(Element evidenceRequest, boolean isUsi) throws MessageException {
		if (logger.isDebugEnabled()) {
			logger.debug("Request: {}", DOMUtils.documentToString(evidenceRequest.getOwnerDocument()));
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		String urlRequest = endpoint + (isUsi ? "USI" : "");

		HttpEntity<RequestTransferEvidence> entity = new HttpEntity<>((RequestTransferEvidence) DOMUtils
				.unmarshall(RequestTransferEvidence.class, evidenceRequest.getOwnerDocument()), headers);
		restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
		
		try {
			if(!isUsi) {
				ResponseEntity<Resource> files = restTemplate.postForEntity(urlRequest, entity, Resource.class);
				byte[] respBytes = FileUtils.buildResponse(files.getBody().getInputStream(), XPATH_SCSP_RESPONSE);
				Document respDoc = DOMUtils.byteToDocument(respBytes);
				return respDoc.getDocumentElement();
			} else {
				ResponseEntity<Ack> ack = restTemplate.postForEntity(urlRequest, entity, Ack.class);
				return DOMUtils.marshall(Ack.class, ack.getBody()).getDocumentElement();
			}
		} catch (IOException | NullPointerException e) {
			throw new MessageException("Error processing response from Owner:" + e.getMessage());
		}
	}

}
