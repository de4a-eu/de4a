package eu.idk.presistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.idk.model.EvidenceService;
import eu.idk.model.EvidenceTypeIds;

@Repository
public interface EvidenceServiceRepository extends JpaRepository<EvidenceService, Object> {
	
	EvidenceService findByCanonicalEvidenceAndCountryCodeAndAtuCode(String canonicalEvidence, String countryCode, String atuCode);

}
