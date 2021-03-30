package eu.de4a.connector.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import eu.de4a.connector.api.RequestApi;
import eu.de4a.connector.model.EvaluatorRequest;
import eu.de4a.connector.repository.EvaluatorRequestRepository;
import eu.de4a.connector.service.EvidenceRequestorManager;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.util.XDE4ACanonicalEvidenceType;
import eu.de4a.util.XDE4AMarshaller;

@Controller
@Validated
public class RequestController implements RequestApi {
	private static final Logger logger = LoggerFactory.getLogger(RequestController.class);
	@Autowired
	private EvaluatorRequestRepository evaluatorRequestRepository;
	@Autowired
	private EvidenceRequestorManager evidenceRequestorManager;
	@Autowired
	private MessageSource messageSource;

	public String lookupRoutingInformation(RequestLookupRoutingInformationType request) {
		
		ResponseLookupRoutingInformationType response = evidenceRequestorManager.manageRequest(request);
		var respMarshaller = DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller();
		return respMarshaller.formatted().getAsString(response);
	}
	
	public String sendRequestUSI(String request) {
		//String message;
		RequestTransferEvidenceUSIIMDRType reqObj = DE4AMarshaller.drUsiRequestMarshaller().read(request);
//		List<String> errors = validateRequest(reqObj);
//		if (errors != null) {
//			message = messageSource.getMessage("error.400.args.unmarshalling", errors.toArray(),
//					LocaleContextHolder.getLocale());
//			// TODO generar mensaje de error no validacion de esquema
//		}
		EvaluatorRequest entity = new EvaluatorRequest();
		entity.setIdevaluator(reqObj.getDataEvaluator().getAgentNameValue());
		entity.setIdrequest(reqObj.getRequestId());
		entity.setUrlreturn(reqObj.getDataEvaluator().getRedirectURL());
		entity.setUsi(true);
		logger.debug("Saving evaluator request - evaluator:{},request:{},urlreturn:{}",
				reqObj.getDataEvaluator().getAgentNameValue(), reqObj.getRequestId(), reqObj.getDataEvaluator().getRedirectURL());
		evaluatorRequestRepository.save(entity);
		
		boolean ok = evidenceRequestorManager.manageRequestUSI(reqObj);
		return generateResponse(reqObj, ok);
	}
	
	public String sendRequestIM(String request) {
//		String msgKey, message;
		ResponseTransferEvidenceType response = null;
		var reqMarshaller = DE4AMarshaller.drImRequestMarshaller();
		RequestTransferEvidenceUSIIMDRType reqObj = reqMarshaller.read(request);
//		List<String> errors = validateRequest(reqObj);

//		if (errors != null) {
//			message = messageSource.getMessage("error.400.args.unmarshalling", errors.toArray(),
//					LocaleContextHolder.getLocale());
//			// TODO generar mensaje de error no validacion de esquema
//		} else {
			EvaluatorRequest entity = new EvaluatorRequest();
			entity.setIdevaluator(reqObj.getDataEvaluator().getAgentNameValue());
			entity.setIdrequest(reqObj.getRequestId());
			entity.setUrlreturn(reqObj.getDataEvaluator().getRedirectURL());
			entity.setUsi(false);
			evaluatorRequestRepository.save(entity);
			logger.debug("Saving evaluator request evaluator:{},request:{},urlreturn:{}",
					reqObj.getDataEvaluator().getAgentNameValue(), reqObj.getRequestId(), reqObj.getDataEvaluator().getRedirectURL());
			response = evidenceRequestorManager.manageRequestIM(reqObj);

//		}
		return XDE4AMarshaller.drImResponseMarshaller(
				XDE4ACanonicalEvidenceType.getXDE4CanonicalEvidenceType(reqObj.getCanonicalEvidenceTypeId())).getAsString(response);
	}

	private String generateResponse(RequestTransferEvidenceUSIIMDRType request, boolean success) {
		ResponseErrorType response = DE4AResponseDocumentHelper.createResponseError(success);
		
		if(!success) {
			String msgKey = "error.rest.usi.err";
			String message = messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());
			ErrorListType errorList = new ErrorListType();
			errorList.getError().add(DE4AResponseDocumentHelper.createError("501", message));
			response.setErrorList(errorList);
		}		
		return DE4AMarshaller.drUsiResponseMarshaller().getAsString(response);
	}
}
