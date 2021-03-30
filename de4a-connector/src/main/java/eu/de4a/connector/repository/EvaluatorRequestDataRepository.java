package eu.de4a.connector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.de4a.connector.model.EvaluatorRequestData;
import eu.de4a.connector.model.RequestDataPK;

@Repository
public interface EvaluatorRequestDataRepository extends JpaRepository<EvaluatorRequestData, RequestDataPK> {

}
