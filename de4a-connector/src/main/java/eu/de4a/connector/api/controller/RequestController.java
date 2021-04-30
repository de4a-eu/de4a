package eu.de4a.connector.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import eu.de4a.connector.api.RequestApi;
import eu.de4a.connector.api.manager.EvidenceRequestorManager;
import eu.de4a.connector.model.EvaluatorRequest;
import eu.de4a.connector.repository.EvaluatorRequestRepository;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
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
	public String lookupRoutingInformation(RequestLookupRoutingInformationType request) {

		ResponseLookupRoutingInformationType response = evidenceRequestorManager.manageRequest(request);
		var respMarshaller = DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller();
		return respMarshaller.formatted().getAsString(response);
	}

	@PostMapping(value = "/requestTransferEvidenceUSI", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
	public String sendRequestUSI(String request) {
		RequestTransferEvidenceUSIIMDRType reqObj = DE4AMarshaller.drUsiRequestMarshaller().read(request);
		EvaluatorRequest entity = new EvaluatorRequest();
		entity.setIdevaluator(reqObj.getDataEvaluator().getAgentNameValue());
		entity.setIdrequest(reqObj.getRequestId());
		entity.setUrlreturn(reqObj.getDataEvaluator().getRedirectURL());
		entity.setUsi(true);
		logger.debug("Saving evaluator request - evaluator:{},request:{},urlreturn:{}",
				reqObj.getDataEvaluator().getAgentNameValue(), reqObj.getRequestId(), reqObj.getDataEvaluator().getRedirectURL());
		evaluatorRequestRepository.save(entity);

		ResponseErrorType response = evidenceRequestorManager.manageRequestUSI(reqObj);		
		return DE4AMarshaller.drUsiResponseMarshaller().getAsString(response);
	}

	@PostMapping(value = "/requestTransferEvidenceIM", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
	public String sendRequestIM(String request) {
		ResponseTransferEvidenceType response = null;
		var reqMarshaller = DE4AMarshaller.drImRequestMarshaller();
		RequestTransferEvidenceUSIIMDRType reqObj = reqMarshaller.read(request);
		EvaluatorRequest entity = new EvaluatorRequest();
		entity.setIdevaluator(reqObj.getDataEvaluator().getAgentNameValue());
		entity.setIdrequest(reqObj.getRequestId());
		entity.setUrlreturn(reqObj.getDataEvaluator().getRedirectURL());
		entity.setUsi(false);
		evaluatorRequestRepository.save(entity);
		logger.debug("Saving evaluator request evaluator:{},request:{},urlreturn:{}",
				reqObj.getDataEvaluator().getAgentNameValue(), reqObj.getRequestId(),
				reqObj.getDataEvaluator().getRedirectURL());
		response = evidenceRequestorManager.manageRequestIM(reqObj);
			
		return DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
				.getAsString(response);
	}
}
