package eu.toop.rest;
 
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.toop.req.model.EvaluatorRequest;
import eu.toop.req.repository.EvaluatorRequestRepository;
import eu.toop.service.EvidenceRequestorManager;
@Controller 
@Validated
public class RequestController {
	private static final Logger logger =  LoggerFactory.getLogger (RequestController.class);
	@Autowired
	private EvaluatorRequestRepository   evaluatorRequestRepository;
	@Autowired
	private EvidenceRequestorManager evidenceRequestorManager;  
	@PostMapping(value = "/request", consumes = {   "application/xml" },  produces = {  "application/xml" }  )
	public @ResponseBody Ack sendRequest(@Valid @RequestBody RequestTransferEvidence request) 
{  
		//TODO Inclusión de validación del mensaje de entrada contra el xsd 
		

		
//JAXBSource source=null;
//	try {
//		source = new JAXBSource(JAXBContext.newInstance(RequestTransferEvidence.class), request);
//	} catch (JAXBException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//	Schema schema;
//	try {
//		SchemaFactory sf = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
//		schema =  sf.newSchema(new StreamSource(this.getClass().getResourceAsStream(  "requestor/DR-IM.xsd" )));
//		Validator validator = schema.newValidator();
//		validator.setErrorHandler(new CustomValidationErrorHandler());
//		validator.validate(source);
//	} catch ( Exception e) {
//		 
//	}
	
		EvaluatorRequest entity=new EvaluatorRequest();
		entity.setIdevaluator(request.getDataEvaluator().getId());
		entity.setIdrequest(request.getRequestId());
		entity.setUrlreturn(request.getReturnServiceId());
		evaluatorRequestRepository.save(entity);
		logger.debug("Saving evaluator request evaluator:{},request:{},urlreturn:{}",request.getDataEvaluator().getId(),request.getRequestId(),request.getReturnServiceId());
		boolean ok=evidenceRequestorManager.manageRequest(request);
		Ack ack=new Ack();
		ack.setCode(ok?Ack.OK:Ack.FAIL);
		ack.setMessage(ok?"In process":"Repeat again"); 
		return ack;
	}
}
	