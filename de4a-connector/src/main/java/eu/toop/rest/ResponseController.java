package eu.toop.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.rest.Ack;
import eu.de4a.conn.owner.MessageResponseOwner;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.toop.connector.api.rest.TCPayload;

@Controller
@Validated
public class ResponseController {
	private static final Logger logger = LoggerFactory.getLogger(ResponseController.class);
	
	@Autowired
	private ApplicationEventMulticaster applicationEventMulticaster;
	@Autowired
	private ApplicationContext context;
	
	@PostMapping(value = "/transferEvidenceUSI", consumes = { MediaType.APPLICATION_OCTET_STREAM_VALUE }, 
			produces = { "application/xml", "application/json" })
	public @ResponseBody Ack responseUSI(@Valid @RequestBody byte[] response) 
			throws MessageException {
		logger.debug("Processing Transfer evidence response from USI pattern");		
	
		Document doc = DOMUtils.byteToDocument(response);
		
		MessageResponseOwner responseUSI = new MessageResponseOwner(context);
		responseUSI.setId(DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, doc.getDocumentElement()));
		responseUSI.setMessage(doc.getDocumentElement());
		applicationEventMulticaster.multicastEvent(responseUSI);
		
		logger.debug(DOMUtils.documentToString(doc));
		
		Ack ack = new Ack();
		ack.setCode(Ack.OK);
		ack.setMessage("Success");
		return ack;
	}
	
	private String getName(byte[] data, String name) {
		String XPATH_SCSP_RESPONSE = "//*[local-name()='Respuesta']";
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
