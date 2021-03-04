package eu.de4a.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.de4a.model.EvaluatorRequestData;
import eu.de4a.model.RequestDataPK;

@Repository
public interface EvaluatorRequestDataRepository extends JpaRepository<EvaluatorRequestData, RequestDataPK> {

}
