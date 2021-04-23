package eu.de4a.util;

import java.util.Arrays;
import java.util.List;

public class SMPUtils {

    SMPUtils() {
        //empty constructor
    }

    /**
     * Retrieve SMP uri for passed AgentUrn
     * Eg. iso6523-actorid-upis::9991:SI990000105
     *
     * @param smpEndpoint
     * @param participantId
     * @param canonicalEvidenceTypeId
     * @return Smp uri to retrieve metadata service of agent
     */
    public static String getSmpUri(String smpEndpoint, String agentUrn, String canonicalEvidenceTypeId) {
        List<String> identifierParams = Arrays.asList(agentUrn.split(DE4AConstants.DOUBLE_SEPARATOR));
        String participantScheme = identifierParams.get(0);
        String participantId = identifierParams.get(1);

        StringBuilder uri;
        if(smpEndpoint.endsWith("/")) {
            uri = new StringBuilder(smpEndpoint);
        } else {
            uri = new StringBuilder(smpEndpoint).append("/");
        }
        uri.append(participantScheme).append(DE4AConstants.DOUBLE_SEPARATOR).append(participantId)
                .append(DE4AConstants.SERVICES_PATH)
                .append(canonicalEvidenceTypeId);
        return uri.toString();
    }
}