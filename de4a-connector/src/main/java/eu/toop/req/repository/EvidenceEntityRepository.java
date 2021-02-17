package eu.toop.req.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import eu.toop.req.model.EvidenceEntity;

@Component
public interface EvidenceEntityRepository extends CrudRepository<EvidenceEntity, String> {

}
