package eu.idk.presistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.idk.model.EvidenceTypeIds;
import eu.idk.model.IssuingAuthority;

@Repository
public interface IssuingAuthorityRepository extends JpaRepository<IssuingAuthority, Integer> {
	
	IssuingAuthority findByEvidenceTypeAndCountryCode(String evidenceType, String countryCode);
}
