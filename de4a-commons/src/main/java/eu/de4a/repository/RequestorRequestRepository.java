package eu.de4a.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.de4a.model.RequestorRequest;

@Repository
public interface RequestorRequestRepository extends JpaRepository<RequestorRequest, String> {

}
