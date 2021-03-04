package eu.de4a.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.de4a.model.RequestDataPK;
import eu.de4a.model.RequestorRequestData;

@Repository
public interface RequestorRequestDataRepository extends JpaRepository<RequestorRequestData, RequestDataPK> {

}
