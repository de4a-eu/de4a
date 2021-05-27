package eu.de4a.connector.api.controller;

import java.io.InputStream;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.w3c.dom.Document;

import com.helger.commons.error.level.EErrorLevel;

import eu.de4a.connector.api.ResponseApi;
import eu.de4a.connector.api.manager.EvidenceTransferorManager;
import eu.de4a.connector.as4.owner.MessageResponseOwner;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceUSIDTException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;

@Controller
@Validated
public class ResponseController implements ResponseApi {
	private static final Logger logger = LoggerFactory.getLogger(ResponseController.class);

	@Autowired
    private EvidenceTransferorManager evidenceTransferorManager;

	@PostMapping(value = "/requestTransferEvidenceUSIDT", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<byte[]> requestTransferEvidenceUSIDT(InputStream request) {
	    MessageResponseOwner responseUSI;
	    DE4AMarshaller<RequestTransferEvidenceUSIDTType> marshaller = 
	            DE4AMarshaller.dtUsiRequestMarshaller(IDE4ACanonicalEvidenceType.NONE);
	    
	    RequestTransferEvidenceUSIDTType reqObj = (RequestTransferEvidenceUSIDTType) ErrorHandlerUtils
                .conversionBytesWithCatching(marshaller, request, false, true, 
                        new ResponseTransferEvidenceUSIDTException().withModule(ExternalModuleError.CONNECTOR_DT));
		try {
			Document doc = marshaller.getAsDocument(reqObj);
			responseUSI = new MessageResponseOwner();
			responseUSI.setMessage(doc.getDocumentElement());
			responseUSI.setId(DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, doc.getDocumentElement()));
			responseUSI.setDataEvaluatorId(DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVALUATOR_ID, doc.getDocumentElement()));
			responseUSI.setDataOwnerId(DOMUtils.getValueFromXpath(DE4AConstants.XPATH_OWNER_ID, doc.getDocumentElement()));
			
			DE4AKafkaClient.send(EErrorLevel.INFO, MessageFormat.format("RequestTransferEvidenceUSI message received - "
	                + "RequestId: {0}", responseUSI.getId()));			
		} catch (Exception e) {
		    String error = "There was an error processing RequestTransferEvidenceUSIDT";
			logger.error(error, e);
			throw new ResponseTransferEvidenceUSIDTException().withLayer(LayerError.INTERNAL_FAILURE)
			    .withFamily(FamilyErrorType.CONVERSION_ERROR)
			    .withModule(ExternalModuleError.CONNECTOR_DT)
			    .withMessageArg(error)
			    .withHttpStatus(HttpStatus.OK);
		}
		evidenceTransferorManager.queueMessageResponse(responseUSI);
		
		ResponseErrorType response = DE4AResponseDocumentHelper.createResponseError(true);
		return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.dtUsiResponseMarshaller().getAsBytes(response));
	}
}
