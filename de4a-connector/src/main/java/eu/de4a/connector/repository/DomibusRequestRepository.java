package eu.de4a.connector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import eu.de4a.connector.model.DomibusRequest;

@Repository
public   interface DomibusRequestRepository extends JpaRepository<DomibusRequest,String> {
	
}
