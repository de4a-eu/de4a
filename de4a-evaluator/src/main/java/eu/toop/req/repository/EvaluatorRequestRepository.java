package eu.toop.req.repository;

import org.springframework.stereotype.Component;

import eu.toop.req.model.EvaluatorRequest;
@Component
public   interface EvaluatorRequestRepository extends org.springframework.data.repository.CrudRepository<EvaluatorRequest,String> {
	 
	 

}
