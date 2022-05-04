package eu.de4a.connector.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import eu.de4a.connector.api.service.AS4Service;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.model.ELogMessages;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.iem.core.DE4ACoreMarshaller;

@Component
public class APIManager {
    
    @Autowired
    private AS4Service as4Service;

    /**
     * Common processing before sending the AS4 message
     * Here the AS4 service is invoked to send the message
     * 
     * @param <T>
     * @param requestObj
     * @param messageDTO
     * @param requestId
     * @param kafkaMsg
     * @param marshaller
     * @return if the message has been sent
     */
    @SuppressWarnings("unchecked")
    public <T> boolean processIncomingMessage(final Object requestObj, 
            final AS4MessageDTO messageDTO, final String requestId, 
            final String kafkaMsg, final DE4ACoreMarshaller<T> marshaller) {
        
        KafkaClientWrapper.sendInfo(ELogMessages.LOG_REQ_RECEIPT, kafkaMsg, requestId, 
                messageDTO.getDocTypeId(), messageDTO.getSenderID(), messageDTO.getReceiverID());
        
        Document doc = marshaller.getAsDocument((T) requestObj);

        // Sends AS4 message - it can trigger an error response via exceptions advised
        return this.as4Service.sendMessage(messageDTO.withMessage(doc));
    }    
}
