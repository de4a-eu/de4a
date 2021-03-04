package eu.de4a.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import eu.de4a.model.DomibusRequest;

@Repository
public   interface DomibusRequestRepository extends JpaRepository<DomibusRequest,String> {
	
}
