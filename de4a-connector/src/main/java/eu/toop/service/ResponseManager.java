package eu.toop.service;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.toop.as4.client.ResponseWrapper; 
import eu.toop.req.model.EvaluatorRequest;
import eu.toop.req.repository.EvaluatorRequestRepository;
import eu.toop.rest.Client;
import eu.toop.service.spring.Conf;

@Component
@Aspect
public class ResponseManager {
	private static final Logger logger =  LoggerFactory.getLogger (Conf.class);
	@Autowired
	private Client client;
	@Autowired
	private EvaluatorRequestRepository evaluatorRequestRepository; 
	@AfterReturning(pointcut="execution(* *.processResponseAs4(..))", returning="retVal") 
	public void cathResponseFromMultipleAs4(Object retVal) {
		ResponseWrapper response=(ResponseWrapper)retVal; 
		String id=response.getId();//"request00000001@de4a";
		EvaluatorRequest evaluatorinfo=evaluatorRequestRepository.findById(id).orElse(null);
		if(evaluatorinfo==null) {
			logger.error("what are u  container me?id="+id);
		}else {
			logger.debug("pushing data 2 ",evaluatorinfo.getUrlreturn()); 
			client.pushEvidence(evaluatorinfo.getUrlreturn(), response);
			evaluatorinfo.setDone(true);
			evaluatorRequestRepository.save(evaluatorinfo);
		}
	}  
//	el response manager debe enviar al evaluator lo que se mandaba en manageREsponse.
//	Despues se ha de hacer lo del plugin ws de domibus de checkear peticion pendiente y mandar respuesta.
//	despues probar a que el transferor tira con un domibus en un tomcat fuera del eclipse
//	
//	
	/*public void manageResponse(IncomingDe4aResponse response) {
		String id=response.getTopLevelContentID();
		EvaluatorRequest evaluatorinfo=evaluatorRequestRepository.findById(id).orElse(null);
		if(evaluatorinfo==null) {
			logger.error("what are u  container me?");
		}else {
			logger.debug("pushing data 2 ",evaluatorinfo.getUrlreturn()); 
			client.pushEvidence2(evaluatorinfo.getUrlreturn(),  response  ,id );
			evaluatorinfo.setDone(true);
			evaluatorRequestRepository.save(evaluatorinfo);
		}
	}*/
	 public boolean isDone(String id) {
		 EvaluatorRequest evaluator=evaluatorRequestRepository.findById(id).orElse(null);
		 return evaluator==null?false:evaluator.isDone();
	 }
	 
	
}
