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
				return buildResponse(files.getBody());
			} else {
				ResponseEntity<Ack> ack = restTemplate.postForEntity(urlRequest, entity, Ack.class);
				return DOMUtils.marshall(Ack.class, ack.getBody()).getDocumentElement();
			}
		} catch (IOException e) {
			throw new MessageException("Error comunicaciones con Owner pid:" + e.getMessage());
		}
	}

	private Element buildResponse(Resource resource) throws MessageException, IOException {
		List<TCPayload> payloads = new ArrayList<TCPayload>();
		File temp = Files.createTempFile("de4a-", null).toFile();
		IOUtils.copy(resource.getInputStream(), new FileOutputStream(temp));
		ZipFile zip = new ZipFile(temp);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

		// TODO si no se va a recibir mas que un element y no una lista de payloads, hay
		// que actualizar el servicio REST del pid-owner

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			byte[] data = zip.getInputStream(entry).readAllBytes();
			TCPayload payload = new TCPayload();
			String name = getName(data, entry.getName());
			payload.setContentID(name);
			payload.setValue(data);
			payloads.add(payload);
		}
		zip.close();
		TCPayload canonicalPayload = payloads.stream()
				.filter(p -> p.getContentID().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)).findFirst().orElse(null);
		if (canonicalPayload == null) {
			logger.error("There is no payload with on %s", DE4AConstants.TAG_EVIDENCE_RESPONSE);
			throw new MessageException("Not exists payload with tag name:" + DE4AConstants.TAG_EVIDENCE_RESPONSE);
		}
		Document doc = DOMUtils.byteToDocument(canonicalPayload.getValue());
		if (doc == null) {
			throw new MessageException("It`s not a well format XML: " + DE4AConstants.TAG_EVIDENCE_RESPONSE);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Request: {}", DOMUtils.documentToString(doc));
		}
		return doc.getDocumentElement();
	}

	private String getName(byte[] data, String name) {
		try {
			Document doc = DOMUtils.byteToDocument(data);
			String value = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVIDENCE_RESPONSE, doc.getDocumentElement());
			if (value != null && !value.isEmpty())
				return DE4AConstants.TAG_EVIDENCE_RESPONSE;
			value = DOMUtils.getValueFromXpath(XPATH_SCSP_RESPONSE, doc.getDocumentElement());
			if (value != null && !value.isEmpty())
				return DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE;
		} catch (MessageException e) {

		}
		return name;
	}

}
