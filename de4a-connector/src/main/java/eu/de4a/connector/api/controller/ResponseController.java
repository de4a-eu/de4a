package eu.de4a.connector.api.controller;

import java.io.InputStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.w3c.dom.Document;

import eu.de4a.connector.api.ResponseApi;
import eu.de4a.connector.api.manager.EvidenceTransferorManager;
import eu.de4a.connector.as4.owner.MessageResponseOwner;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceUSIDTException;
import eu.de4a.connector.error.exceptions.ResponseUSIRedirectUserException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.error.utils.KafkaClientWrapper;
import eu.de4a.iem.jaxb.common.types.RedirectUserType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.DE4AConstants;

@Controller
@Validated
public class ResponseController implements ResponseApi {

	@Autowired
    private EvidenceTransferorManager evidenceTransferorManager;

	@PostMapping(value = "/requestTransferEvidenceUSIDT", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<byte[]> requestTransferEvidenceUSIDT(InputStream request) {
	    DE4AMarshaller<RequestTransferEvidenceUSIDTType> marshaller = 
	            DE4AMarshaller.dtUsiRequestMarshaller(IDE4ACanonicalEvidenceType.NONE);
	    
	    RequestTransferEvidenceUSIDTType reqObj = (RequestTransferEvidenceUSIDTType) ErrorHandlerUtils
                .conversionBytesWithCatching(marshaller, request, false, true, 
                        new ResponseTransferEvidenceUSIDTException().withModule(ExternalModuleError.CONNECTOR_DT));
	    KafkaClientWrapper.sendInfo(LogMessages.LOG_USI_DT_REQ_RECEIPT, reqObj.getRequestId());
	    
		return processResponseUsiMsg(marshaller, reqObj, reqObj.getRequestId(), reqObj.getDataEvaluator().getAgentUrn(), 
		        reqObj.getDataOwner().getAgentUrn(), DE4AConstants.TAG_EVIDENCE_REQUEST_DT);
	}
	
	@PostMapping(value = "/usiRedirectUser", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> redirectUserUsi(@Valid InputStream request) {
        DE4AMarshaller<RedirectUserType> marshaller = DE4AMarshaller.deUsiRedirectUserMarshaller();

        RedirectUserType redirectUserMsg = (RedirectUserType) ErrorHandlerUtils
                .conversionBytesWithCatching(marshaller, request, false, true, 
                        new ResponseUSIRedirectUserException().withModule(ExternalModuleError.CONNECTOR_DT)
                            .withHttpStatus(HttpStatus.BAD_REQUEST));
        KafkaClientWrapper.sendInfo(LogMessages.LOG_USI_RED_USER, RedirectUserType.class.getSimpleName(), 
                redirectUserMsg.getRequestId(), redirectUserMsg.getCanonicalEvidenceTypeId(),
                redirectUserMsg.getDataEvaluator().getAgentUrn(), "N/A");
        
        return processResponseUsiMsg(marshaller, redirectUserMsg, redirectUserMsg.getRequestId(),
                redirectUserMsg.getDataEvaluator().getAgentUrn(), null, DE4AConstants.TAG_REDIRECT_USER);
    }
    
    private <T> ResponseEntity<byte[]> processResponseUsiMsg(DE4AMarshaller<T> marshaller, T reqObj,
            String requestId, String dataEvaluatorId, String dataOwnerId, String msgTag) {
        MessageResponseOwner responseMsg;
        Document doc = marshaller.getAsDocument(reqObj);
        responseMsg = new MessageResponseOwner();
        responseMsg.setMessage(doc.getDocumentElement());
        responseMsg.setId(requestId);
        responseMsg.setDataEvaluatorId(dataEvaluatorId);
        responseMsg.setDataOwnerId(dataOwnerId);
        
        evidenceTransferorManager.queueMessageResponse(responseMsg, msgTag);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
