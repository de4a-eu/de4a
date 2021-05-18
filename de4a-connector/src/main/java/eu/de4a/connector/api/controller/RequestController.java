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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.helger.commons.error.level.EErrorLevel;

import eu.de4a.connector.api.RequestApi;
import eu.de4a.connector.api.manager.EvidenceRequestorManager;
import eu.de4a.connector.error.exceptions.ResponseErrorException;
import eu.de4a.connector.error.exceptions.ResponseLookupRoutingInformationException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.model.EvaluatorRequest;
import eu.de4a.connector.repository.EvaluatorRequestRepository;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;

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
	public ResponseEntity<String> lookupRoutingInformation(InputStream request) {
	    
	    RequestLookupRoutingInformationType reqObj = (RequestLookupRoutingInformationType) ErrorHandlerUtils
                .conversionStrWithCatching(DE4AMarshaller.idkRequestLookupRoutingInformationMarshaller(), request, false, true, 
                new ResponseLookupRoutingInformationException().withModule(ExternalModuleError.CONNECTOR_DR));
	    
	    DE4AKafkaClient.send(EErrorLevel.INFO, MessageFormat.format("RequestLookupRoutingInformation message received - "
	            + "CanonicalEvidenceType: {0}, CountryCode: {1}, DataOwnerId: {2}", reqObj.getCanonicalEvidenceTypeId(),
	            reqObj.getCountryCode(), reqObj.getDataOwnerId()));
	    
		ResponseLookupRoutingInformationType response = evidenceRequestorManager.manageRequest(reqObj);
		var respMarshaller = DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller();
		return ResponseEntity.status(HttpStatus.OK).body(respMarshaller.formatted().getAsString(response));
	}

	@PostMapping(value = "/requestTransferEvidenceUSI", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> requestTransferEvidenceUSI(InputStream request) {
	    
	    RequestTransferEvidenceUSIIMDRType reqObj = processIncommingEvidenceReq(DE4AMarshaller.drUsiRequestMarshaller(), 
                request, true);

		ResponseErrorType response = evidenceRequestorManager.manageRequestUSI(reqObj);		
		return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drUsiResponseMarshaller().getAsString(response));
	}

	@PostMapping(value = "/requestTransferEvidenceIM", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> requestTransferEvidenceIM(InputStream request) {
		
	    RequestTransferEvidenceUSIIMDRType reqObj = processIncommingEvidenceReq(DE4AMarshaller.drImRequestMarshaller(),
	            request, false);
		
		ResponseTransferEvidenceType response = evidenceRequestorManager.manageRequestIM(reqObj);
		return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
				.getAsString(response));
	}
	
	private <T> RequestTransferEvidenceUSIIMDRType processIncommingEvidenceReq(DE4AMarshaller<T> marshaller, InputStream request,
	        boolean isUsi) {
	    RequestTransferEvidenceUSIIMDRType reqObj = (RequestTransferEvidenceUSIIMDRType) ErrorHandlerUtils
                .conversionStrWithCatching(marshaller, request, false, true, 
                new ResponseErrorException().withModule(ExternalModuleError.CONNECTOR_DR));
	    
        String requestType = "RequestTransferEvidence" + (isUsi ? "USI" : "IM");
        DE4AKafkaClient.send(EErrorLevel.INFO, MessageFormat.format("{0} message received - "
                + "RequestId: {1}, CanonicalEvidenceType: {2}, DataEvaluator: {3}, DataOwner: {4}", 
                requestType, reqObj.getRequestId(), reqObj.getCanonicalEvidenceTypeId(), 
                reqObj.getDataEvaluator().getAgentUrn(), reqObj.getDataOwner().getAgentUrn()));
        
        saveEvaluatorRequest(reqObj, isUsi);        
        return reqObj;
	}
	
	private void saveEvaluatorRequest(RequestTransferEvidenceUSIIMDRType request, boolean isUsi) {
	    EvaluatorRequest entity = new EvaluatorRequest();
        entity.setIdevaluator(request.getDataEvaluator().getAgentNameValue());
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
