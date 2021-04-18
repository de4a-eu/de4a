package eu.de4a.connector.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.w3c.dom.Document;

import eu.de4a.connector.api.ResponseApi;
import eu.de4a.connector.as4.owner.MessageResponseOwner;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;

@Controller
@Validated
public class ResponseController implements ResponseApi {
	private static final Logger logger = LoggerFactory.getLogger(ResponseController.class);

	@Autowired
	private ApplicationEventMulticaster applicationEventMulticaster;
	@Autowired
	private ApplicationContext context;


	public String requestForwardEvidence(String requestForward) {
		boolean success;
		try {
			Document doc = DOMUtils.stringToDocument(requestForward);
			String id = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, doc.getDocumentElement());

			MessageResponseOwner responseUSI = new MessageResponseOwner(context);
			responseUSI.setId(id);
			responseUSI.setMessage(doc.getDocumentElement());
			applicationEventMulticaster.multicastEvent(responseUSI);

			success = true;
		} catch (Exception | MessageException e) {
			logger.error("There was a problem processing owner USI response");
			success = false;
		}
		ResponseErrorType response = DE4AResponseDocumentHelper.createResponseError(success);
		return DE4AMarshaller.dtUsiResponseMarshaller().getAsString(response);
	}
}
