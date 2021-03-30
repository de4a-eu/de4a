package eu.idk.presistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import eu.idk.model.Source;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long>{
	List<Source> findByCanonicalEvidenceTypeId(String canonicalEvidenceTypeId);
	List<Source> findByCanonicalEvidenceTypeIdAndCountryCode(String canonicalEvidenceTypeId, String countryCode);
}
