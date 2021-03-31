package eu.de4a.util;

import java.util.Arrays;
import java.util.List;

public class SMPUtils {	
	
	SMPUtils() {
		//empty constructor
	}

	/**
	 * Retrieve SMP uri for passed AgentUrn
	 * Eg. urn:de4a-eu:provision::9920:ESS2833002E
	 * Eg. iso6523-actorid-upis::9991:SI990000105
	 * 
	 * @param smpEndpoint
	 * @param participantId
	 * @param canonicalEvidenceTypeId
	 * @return Smp uri to retrieve service of transferor
	 */
	public static String getSmpUri(String smpEndpoint, String agentUrn, String canonicalEvidenceTypeId) {
		List<String> identifierParams = Arrays.asList(agentUrn.split(DE4AConstants.DOUBLE_SEPARATOR));
		String participantScheme = identifierParams.get(0);
		String participantId = identifierParams.get(1);

		StringBuilder uri = new StringBuilder(smpEndpoint);
		uri.append(participantScheme).append(DE4AConstants.DOUBLE_SEPARATOR).append(participantId)
				.append(DE4AConstants.SERVICES_PATH)
				.append(DE4AConstants.URN_SCHEME)
				.append(DE4AConstants.CANONICAL_EVIDENCE_TYPE)
				.append(DE4AConstants.DOUBLE_SEPARATOR).append(canonicalEvidenceTypeId);
		return uri.toString();
	}
}
