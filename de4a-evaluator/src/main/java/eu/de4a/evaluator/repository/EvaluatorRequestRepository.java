package eu.de4a.evaluator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.de4a.evaluator.model.EvaluatorRequest;

@Repository
public interface EvaluatorRequestRepository extends JpaRepository<EvaluatorRequest, String> {

}
