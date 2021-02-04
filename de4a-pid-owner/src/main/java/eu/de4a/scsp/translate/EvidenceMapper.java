package eu.de4a.scsp.translate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import eu.de4a.scsp.translate.birth.BirthEvidenceTranslator;
@Component
public class EvidenceMapper {
	public static final String BIRTH_CERTIFICATE="SVDSCCNWS01"; 
	@Autowired
	private ApplicationContext context;
	public   EvidenceTranslator getTranslator(String evidenceServiceUri) {
		return (EvidenceTranslator) context.getBean("birthEvidenceTranslator");
	}
}
