package eu.toop.req.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import eu.toop.req.model.RequestorRequest;

@Component
public interface RequestorRequestRepository extends CrudRepository<RequestorRequest, String> {

}
