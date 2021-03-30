package eu.de4a.scsp.owner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.de4a.scsp.owner.model.RequestDataPK;
import eu.de4a.scsp.owner.model.RequestorRequestData;

@Repository
public interface RequestorRequestDataRepository extends JpaRepository<RequestorRequestData, RequestDataPK> {

}
