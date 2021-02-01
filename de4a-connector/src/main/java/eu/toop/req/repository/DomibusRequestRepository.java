package eu.toop.req.repository;

import org.springframework.stereotype.Component;

import eu.toop.req.model.DomibusRequest;
@Component
public   interface DomibusRequestRepository extends org.springframework.data.repository.CrudRepository<DomibusRequest,String> {
	 
	 

}
