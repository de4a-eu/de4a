package eu.de4a.connector.config;

public class DE4AConstants {
	public static final String ID_MIMEPART="id";

	public static final String TAG_FORWARD_EVIDENCE_REQUEST="RequestForwardEvidence";
	public static final String TAG_EXTRACT_EVIDENCE_RESPONSE="ResponseExtractEvidence";
	public static final String TAG_NATIONAL_EVIDENCE_RESPONSE="NationalEvidenceResponse";
  public static final String XPATH_REQUEST_ID="//*[local-name()='RequestId']/text()";

	public static final String NAMESPACE_USI = "http://www.de4a.eu/2020/data/requestor/pattern/usi";

	//Processes identifiers
	public static final String MESSAGE_TYPE_REQUEST = "request";
	public static final String MESSAGE_TYPE_RESPONSE = "response";
	public static final String MESSAGE_TYPE_NOTIFICATION = "notification";

	// DE4A Schemes
	public static final String EVIDENCE_SCHEME = "urn:de4a-eu:CanonicalEvidenceType";
	public static final String PROCESS_SCHEME = "urn:de4a-eu:MessageType";
	public static final String EVENT_CATALOGUE_SCHEME = "urn:de4a-eu:CanonicalEventCatalogue";

	// DE4A Document types Values
	public static final String CANONICAL_EVIDENCE_TYPE = "CanonicalEvidenceType";
	public static final String MULTI_ITEM_TYPE = "MultiItem";

	private DE4AConstants (){}
}
