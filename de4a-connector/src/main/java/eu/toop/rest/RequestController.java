package eu.toop.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.toop.req.model.EvaluatorRequest;
import eu.toop.req.repository.EvaluatorRequestRepository;
import eu.toop.service.EvidenceRequestorManager;
@Controller 
public class RequestController {
	private static final Logger logger =  LoggerFactory.getLogger (RequestController.class);
	@Autowired
	private EvaluatorRequestRepository   evaluatorRequestRepository;
	@Autowired
	private EvidenceRequestorManager evidenceRequestorManager; 
	/*public @ResponseBody Ack sendRequest(@RequestParam  String urlReturn,@RequestParam String evaluatorId,@RequestParam String requestId) 
	{ 
		EvaluatorRequest entity=new EvaluatorRequest();
		entity.setIdevaluator(evaluatorId);
		entity.setIdrequest(requestId);
		entity.setUrlreturn(urlReturn);
		evaluatorRequestRepository.save(entity);
		logger.debug("Saving evaluator request evaluator:{},request:{},urlreturn:{}",evaluatorId,requestId, urlReturn);
		boolean ok=evidenceRequestorManager.yourfather(requestId,"");
		Ack ack=new Ack();
		ack.setCode(ok?"OK":"FAIL");
		ack.setMessage(ok?"In process":"Repeat again"); 
		return ack;
	}*/
	//@RequestMapping(method = RequestMethod.POST, value="/request", headers="Accept=*/*",   consumes = {   "application/xml" },  produces = {  "application/xml" }   )
	@PostMapping(value = "/request", consumes = {   "application/xml" },  produces = {  "application/xml" }  )
	public @ResponseBody Ack sendRequest(@RequestBody RequestTransferEvidence request) 
	{ 
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
	