package eu.de4a.util;

public class DE4AConstants {
	public static final String ID_MIMEPART="id";

	public static final String TAG_EVIDENCE_REQUEST="RequestTransferEvidence"; 
	public static final String TAG_EVIDENCE_RESPONSE="ResponseTransferEvidence"; 
	public static final String XPATH_EVIDENCE_RESPONSE="//*[local-name()='ResponseTransferEvidence']";
	public static final String TAG_NATIONAL_EVIDENCE_RESPONSE="NationalEvidenceResponse"; 
	public static final String XPATH_REQUEST_ID="//*[local-name()='%s']/*[local-name()='RequestId']/text()"; 
	public static final String XPATH_ID="//*[local-name()='RequestId']";
	public static final String XPATH_EVIDENCE_DATA="//*[local-name()='EvidenceData']";
	public static final String XPATH_EVALUATOR_ID="//*[local-name()='DataEvaluator']/*[local-name()='id']/text()";
	public static final String XPATH_EVALUATOR_NAME="//*[local-name()='DataEvaluator']/*[local-name()='name']/text()";
	public static final String XPATH_EVALUATOR_ID_NODE="//*[local-name()='DataEvaluator']/*[local-name()='id']";
	public static final String XPATH_EVALUATOR_NAME_NODE="//*[local-name()='DataEvaluator']/*[local-name()='name']";
	public static final String XPATH_OWNER_ID="//*[local-name()='DataOwner']/*[local-name()='id']/text()";
	public static final String XPATH_OWNER_NAME="//*[local-name()='DataOwner']/*[local-name()='name']/text()";
	public static final String XPATH_OWNER_ID_NODE="//*[local-name()='DataOwner']/*[local-name()='id']";
	public static final String XPATH_OWNER_NAME_NODE="//*[local-name()='DataOwner']/*[local-name()='name']";
	public static final String XPATH_CANONICAL_EVICENCE_ID="//*[local-name()='CanonicalEvidenceId']/text()";
	public static final String XPATH_RETURN_SERVICE_ID="//*[local-name()='ReturnServiceId']";
	public static final String XPATH_SERVICE_URI="//*[local-name()='EvidenceServiceURI']";
	
	public static final String XPATH_EIDAS_DOC="//*[local-name()='Identifier']/text()";  
	public static final String XPATH_EIDAS_SURNAME="//*[local-name()='FamilyName']/text()";   
	public static final String XPATH_EIDAS_NAME="//*[local-name()='GivenName']/text()";    
	public static final String XPATH_EIDAS_FULLNAME="//*[local-name()='BirthName']/text()";   
	
	public static final String XPATH_EIDAS_BIRTHDATE="//*[local-name()='DateOfBirth']/text()"; 
	

	public static final String XPATH_EIDAS_DOC_NODE="//*[local-name()='PersonID']";  
	public static final String XPATH_EIDAS_SURNAME_NODE="//*[local-name()='PersonFamilyName']";   
	public static final String XPATH_EIDAS_NAME_NODE="//*[local-name()='PersonGivenName']";   
	public static final String XPATH_EIDAS_FULLNAME_NODE="//*[local-name()='PersonBirthName']";   
	public static final String XPATH_EIDAS_BIRTHDATE_NODE="//*[local-name()='PersonBirthDate']"; 
	
	
	public static final String BIRTH_DATE_PATTERN="yyyy-MM-dd";
}
