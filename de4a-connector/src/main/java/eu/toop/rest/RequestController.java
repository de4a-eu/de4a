package eu.toop.rest;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.de4a.conn.api.requestor.RequestLookupEvidenceServiceData;
import eu.de4a.conn.api.requestor.RequestLookupRoutingInformation;
import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.requestor.ResponseLookupEvidenceServiceData;
import eu.de4a.conn.api.requestor.ResponseLookupRoutingInformation;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.model.EvaluatorRequest;
import eu.de4a.repository.EvaluatorRequestRepository;
import eu.toop.service.EvidenceRequestorManager;

@Controller
@Validated
public class RequestController {
	private static final Logger logger = LoggerFactory.getLogger(RequestController.class);
	@Autowired
	private EvaluatorRequestRepository evaluatorRequestRepository;
	@Autowired
	private EvidenceRequestorManager evidenceRequestorManager;
	@Autowired
	private MessageSource messageSource;

	@PostMapping(value = "/lookupRouting", consumes = { "application/xml" }, produces = { "application/xml" })
	public @ResponseBody ResponseLookupRoutingInformation lookupRoutingInformation(@Valid @RequestBody RequestLookupRoutingInformation request) {
		
		ResponseLookupRoutingInformation response = evidenceRequestorManager.manageRequest(request);
		
		return response;		
	}
	
	@PostMapping(value = "/lookupEvidenceService", consumes = { "application/xml" }, produces = { "application/xml" })
	public @ResponseBody ResponseLookupEvidenceServiceData lookupEvidenceServiceData(@Valid @RequestBody RequestLookupEvidenceServiceData request) {
		ResponseLookupEvidenceServiceData response = evidenceRequestorManager.manageRequest(request);
		
		return response;		
	}

	@PostMapping(value = "/requestUSI", consumes = { "application/xml" }, produces = { "application/xml" })
	public @ResponseBody Ack sendRequestUSI(@Valid @RequestBody RequestTransferEvidence request) {
		String message;
		List<String> errors = validateRequest(request);
		if (errors != null) {
			message = messageSource.getMessage("error.400.args.unmarshalling", errors.toArray(),
					LocaleContextHolder.getLocale());
			// TODO generar mensaje de error no validacion de esquema
		}

		EvaluatorRequest entity = new EvaluatorRequest();
		entity.setIdevaluator(request.getDataEvaluator().getId());
		entity.setIdrequest(request.getRequestId());
		entity.setUrlreturn(request.getDataEvaluator().getUrlRedirect());
		entity.setUsi(true);
		evaluatorRequestRepository.save(entity);
		logger.debug("Saving evaluator request evaluator:{},request:{},urlreturn:{}",
				request.getDataEvaluator().getId(), request.getRequestId(), request.getReturnServiceId());
		boolean ok = evidenceRequestorManager.manageRequest(request, true);
		String msgKey = ok ? "error.rest.usi.inprocess" : "error.rest.usi.err";
		message = messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());

		return generateResponse(ok, message);
	}

	@PostMapping(value = "/request", consumes = { "application/xml" }, produces = { "application/xml" })
	public @ResponseBody ResponseTransferEvidence sendRequest(@Valid @RequestBody RequestTransferEvidence request) {
		String msgKey, message;
		ResponseTransferEvidence response = null;
		List<String> errors = validateRequest(request);

		if (errors != null) {
			message = messageSource.getMessage("error.400.args.unmarshalling", errors.toArray(),
					LocaleContextHolder.getLocale());
			// TODO generar mensaje de error no validacion de esquema
		} else {
			EvaluatorRequest entity = new EvaluatorRequest();
			entity.setIdevaluator(request.getDataEvaluator().getId());
			entity.setIdrequest(request.getRequestId());
			entity.setUrlreturn(request.getDataEvaluator().getUrlRedirect());
			entity.setUsi(false);
			evaluatorRequestRepository.save(entity);
			logger.debug("Saving evaluator request evaluator:{},request:{},urlreturn:{}",
					request.getDataEvaluator().getId(), request.getRequestId(), request.getReturnServiceId());
			response = evidenceRequestorManager.manageRequestIM(request);

		}
		return response;
	}

	private List<String> validateRequest(RequestTransferEvidence request) {
		return null;
		// TODO Inclusión de validación del mensaje de entrada contra el xsd
		// JAXBSource source=null;
//		try {
//			source = new JAXBSource(JAXBContext.newInstance(RequestTransferEvidence.class), request);
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//
//		Schema schema;
//		try {
//			SchemaFactory sf = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
//			schema =  sf.newSchema(new StreamSource(this.getClass().getResourceAsStream(  "requestor/DR-IM.xsd" )));
//			Validator validator = schema.newValidator();
//			validator.setErrorHandler(new CustomValidationErrorHandler());
//			validator.validate(source);
//		} catch ( Exception e) {
//			 
//		}
	}

	private Ack generateResponse(boolean ok, String message) {
		Ack ack = new Ack();
		ack.setCode(ok ? Ack.OK : Ack.FAIL);
		ack.setMessage(message);
		return ack;
	}
}
