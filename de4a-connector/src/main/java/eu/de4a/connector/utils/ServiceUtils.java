package eu.de4a.connector.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.de4a.connector.config.AddressesProperties;
import eu.de4a.connector.error.model.LogMessages;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceUtils {
    
    @Autowired
    private AddressesProperties addressesProperties;

    /**
     * 
     * Get the participant's endpoint
     * 
     * @param participantId - Participant identifier URI encoded
     * @param endpointType - determines the data flow which receives the message
     * @param isRequest - if true Data Owners are selected, if not Data Evaluators
     * @return String - URL with the participant endpoint
     */
    public String getParticipantAddress(String participantId, String endpointType, 
            boolean isRequest) {
        KafkaClientWrapper.sendInfo(LogMessages.LOG_PARTICIPANT_LOOKUP, participantId);

        Map<String, Map<String, String>> participants = (isRequest ? addressesProperties.getDataOwners()
                : addressesProperties.getDataEvaluators());

        // Bracket surrounding is needed for Spring to not ignore the keys colons in the parsing 
        Map<String, String> participantAddress = participants.get("[" + participantId + "]");
        if (participantAddress == null || participantAddress.get(endpointType) == null) {            
            KafkaClientWrapper.sendError(LogMessages.LOG_ERROR_PARTICIPANT_LOOKUP, participantId);
            return null;
        }
        return participantAddress.get(endpointType);
    }
    
}
