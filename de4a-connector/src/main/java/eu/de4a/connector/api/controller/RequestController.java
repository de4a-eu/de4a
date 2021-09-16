package eu.de4a.connector.api.controller;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import eu.de4a.connector.api.RequestApi;
import eu.de4a.connector.api.manager.EvidenceRequestorManager;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseLookupRoutingInformationException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceUSIException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.error.utils.KafkaClientWrapper;
import eu.de4a.connector.model.EvaluatorRequest;
import eu.de4a.connector.repository.EvaluatorRequestRepository;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;

@Controller
@Validated
public class RequestController implements RequestApi {
	private static final Logger logger = LoggerFactory.getLogger(RequestController.class);
	@Autowired
	private EvaluatorRequestRepository evaluatorRequestRepository;
	@Autowired
	private EvidenceRequestorManager evidenceRequestorManager;
	
	@GetMapping(value = "/")
	public String rootPath() {
	    return "index";
	}

	@PostMapping(value = "/lookupRoutingInformation", produces = MediaType.APPLICATION_XML_VALUE, 
	        consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<byte[]> lookupRoutingInformation(InputStream request) {
	    
	    RequestLookupRoutingInformationType reqObj = (RequestLookupRoutingInformationType) ErrorHandlerUtils
                .conversionBytesWithCatching(DE4AMarshaller.idkRequestLookupRoutingInformationMarshaller(), request, false, true, 
                new ResponseLookupRoutingInformationException().withModule(ExternalModuleError.CONNECTOR_DR));
	    	    
	    KafkaClientWrapper.sendInfo(LogMessages.LOG_IDK_REQ_RECEIPT, reqObj.getCanonicalEvidenceTypeId(),
                reqObj.getCountryCode(), reqObj.getDataOwnerId());
	    
		ResponseLookupRoutingInformationType response = evidenceRequestorManager.manageRequest(reqObj);
		var respMarshaller = DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller();
		return ResponseEntity.status(HttpStatus.OK).body(respMarshaller.formatted().getAsBytes(response));
	}

	@PostMapping(value = "/requestTransferEvidenceUSI", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<byte[]> requestTransferEvidenceUSI(InputStream request) {
	    
	  RequestExtractEvidenceType reqObj = processIncommingEvidenceReq(DE4AMarshaller.drUsiRequestMarshaller(), 
                request, true, new ResponseTransferEvidenceUSIException());

		ResponseErrorType response = evidenceRequestorManager.manageRequestUSI(reqObj);		
		return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drUsiResponseMarshaller().getAsBytes(response));
	}

	@PostMapping(value = "/requestTransferEvidenceIM", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<byte[]> requestTransferEvidenceIM(InputStream request) {
		
	  RequestExtractEvidenceType reqObj = processIncommingEvidenceReq(DE4AMarshaller.drImRequestMarshaller(),
	            request, false, new ResponseTransferEvidenceException());
		
		ResponseTransferEvidenceType response = evidenceRequestorManager.manageRequestIM(reqObj);
		return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
				.getAsBytes(response));
	}
	
	private <T> RequestExtractEvidenceType processIncommingEvidenceReq(DE4AMarshaller<T> marshaller, InputStream request,
	        boolean isUsi, ConnectorException ex) {	    
	  RequestExtractEvidenceType reqObj = (RequestExtractEvidenceType) ErrorHandlerUtils
                .conversionBytesWithCatching(marshaller, request, false, true, 
                ex.withModule(ExternalModuleError.CONNECTOR_DR));
	    
	    LogMessages.LOG_USI_REQ_RECEIPT.getKey();
	    LogMessages.LOG_IM_REQ_RECEIPT.getKey();
	    String requestType;
	    LogMessages logMessage;
	    if(isUsi) {
	        requestType = "requestTransferEvidence" + "USI";
	        logMessage = LogMessages.LOG_USI_REQ_RECEIPT;
	    } else {
	        requestType = "requestTransferEvidence" + "IM";
	        logMessage = LogMessages.LOG_IM_REQ_RECEIPT;
	    }
        KafkaClientWrapper.sendInfo(logMessage, requestType, reqObj.getRequestId(), reqObj.getCanonicalEvidenceTypeId(), 
                reqObj.getDataEvaluator().getAgentUrn(), reqObj.getDataOwner().getAgentUrn());
        
        saveEvaluatorRequest(reqObj, isUsi);        
        return reqObj;
	}
	
	private void saveEvaluatorRequest(RequestExtractEvidenceType request, boolean isUsi) {
	    EvaluatorRequest entity = new EvaluatorRequest();
        entity.setIdevaluator(request.getDataEvaluator().getAgentUrn());
        entity.setIdrequest(request.getRequestId());
        entity.setUrlreturn(request.getDataEvaluator().getRedirectURL());
        entity.setUsi(isUsi);
        evaluatorRequestRepository.save(entity);
        if(logger.isDebugEnabled())
            logger.debug("Saving evaluator request - evaluator:{}, request:{}, urlreturn:{}",
                    request.getDataEvaluator().getAgentNameValue(), request.getRequestId(),
                    request.getDataEvaluator().getRedirectURL());
	}
}
