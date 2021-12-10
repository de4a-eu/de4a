package eu.de4a.util;

public class DE4AConstants {
	public static final String ID_MIMEPART="id";

	public static final String TAG_EVIDENCE_REQUEST="RequestTransferEvidence";
	public static final String TAG_EVIDENCE_REQUEST_DT="RequestTransferEvidenceUSIDT";
	public static final String TAG_REDIRECT_USER="RedirectUser";
	public static final String TAG_EXTRACT_EVIDENCE_REQUEST="RequestExtractEvidence";
	public static final String TAG_EVIDENCE_RESPONSE="ResponseTransferEvidence";
	public static final String TAG_FORWARD_EVIDENCE_REQUEST="RequestForwardEvidence";
	public static final String TAG_EXTRACT_EVIDENCE_RESPONSE="ResponseExtractEvidence";
	public static final String TAG_NATIONAL_EVIDENCE_RESPONSE="NationalEvidenceResponse";
	public static final String XPATH_ABSOLUTE_PARAM="//*[local-name()='%s']";
	public static final String XPATH_EVIDENCE_RESPONSE="//*[local-name()='ResponseTransferEvidence']";
	public static final String XPATH_EXTRACT_EVIDENCE_RESPONSE="//*[local-name()='ResponseExtractEvidence']";	
	public static final String XPATH_EVIDENCE_REQUEST="//*[local-name()='RequestTransferEvidence']";
	public static final String XPATH_REQUEST_ID="//*[local-name()='%s']/*[local-name()='RequestId']/text()";
	public static final String XPATH_ID="//*[local-name()='RequestId']";
	public static final String XPATH_EVIDENCE_DATA="//*[local-name()='EvidenceData']";
	public static final String XPATH_EVALUATOR_ID="//*[local-name()='DataEvaluator']/*[local-name()='AgentUrn']/text()";
	public static final String XPATH_EVALUATOR_NAME="//*[local-name()='DataEvaluator']/*[local-name()='AgentName']/text()";
	public static final String XPATH_EVALUATOR_ID_NODE="//*[local-name()='DataEvaluator']/*[local-name()='id']";
	public static final String XPATH_EVALUATOR_NAME_NODE="//*[local-name()='DataEvaluator']/*[local-name()='name']";
	public static final String XPATH_OWNER_ID="//*[local-name()='DataOwner']/*[local-name()='AgentUrn']/text()";
	public static final String XPATH_OWNER_NAME="//*[local-name()='DataOwner']/*[local-name()='AgentName']/text()";
	public static final String XPATH_OWNER_ID_NODE="//*[local-name()='DataOwner']/*[local-name()='id']";
	public static final String XPATH_OWNER_NAME_NODE="//*[local-name()='DataOwner']/*[local-name()='name']";
	public static final String XPATH_CANONICAL_EVICENCE_ID="//*[local-name()='CanonicalEvidenceTypeId']/text()";
	public static final String XPATH_CANONICAL_EVICENCE="//*[local-name()='CanonicalEvidence']";
	public static final String XPATH_RETURN_SERVICE_ID="//*[local-name()='ReturnServiceId']";
	public static final String XPATH_SERVICE_URI="//*[local-name()='EvidenceServiceURI']";

	public static final String XPATH_EIDAS_DOC="//*[local-name()='PersonIdentifier']/text()";
	public static final String XPATH_EIDAS_SURNAME="//*[local-name()='FamilyName']/text()";
	public static final String XPATH_EIDAS_NAME="//*[local-name()='FirstName']/text()";
	public static final String XPATH_EIDAS_FULLNAME="//*[local-name()='BirthName']/text()";

	public static final String XPATH_EIDAS_BIRTHDATE="//*[local-name()='DateOfBirth']/text()";


	public static final String XPATH_EIDAS_DOC_NODE="//*[local-name()='PersonID']";
	public static final String XPATH_EIDAS_SURNAME_NODE="//*[local-name()='PersonFamilyName']";
	public static final String XPATH_EIDAS_NAME_NODE="//*[local-name()='PersonGivenName']";
	public static final String XPATH_EIDAS_FULLNAME_NODE="//*[local-name()='PersonBirthName']";
	public static final String XPATH_EIDAS_BIRTHDATE_NODE="//*[local-name()='PersonBirthDate']";
	
	public static final String NAMESPACE_USI = "http://www.de4a.eu/2020/data/requestor/pattern/usi";

	//SMP request MessageType
	public static final String MESSAGE_TYPE_REQUEST = "request";

	//SMP response MessageType
	public static final String MESSAGE_TYPE_RESPONSE = "response";

	// DE4A Schemes
	public static final String DOCTYPE_SCHEME = "urn:de4a-eu:CanonicalEvidenceType";
	public static final String PROCESS_SCHEME = "urn:de4a-eu:MessageType";

	public static final String CANONICAL_EVIDENCE_TYPE = "CanonicalEvidenceType";

	public static final String BIRTH_DATE_PATTERN="yyyy-MM-dd";

	private DE4AConstants() {
		//empty private constructor
	}
}
