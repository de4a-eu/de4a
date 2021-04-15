package eu.de4a.connector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.de4a.connector.model.RequestDataPK;
import eu.de4a.connector.model.RequestorRequestData;

@Repository
public interface RequestorRequestDataRepository extends JpaRepository<RequestorRequestData, RequestDataPK> {

}
