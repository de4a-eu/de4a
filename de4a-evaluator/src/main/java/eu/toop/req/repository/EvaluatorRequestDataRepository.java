package eu.toop.req.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import eu.toop.req.model.EvaluatorRequestData;
import eu.toop.req.model.EvaluatorRequestDataPK;
@Component
public   interface EvaluatorRequestDataRepository extends    JpaRepository<EvaluatorRequestData,EvaluatorRequestDataPK>  {
	 
	 

}
