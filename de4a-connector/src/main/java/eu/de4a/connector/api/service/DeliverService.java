package eu.de4a.connector.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import eu.de4a.connector.api.service.model.EMessageServiceTypes;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.error.model.ELogMessages;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.connector.utils.DOMUtils;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.connector.utils.ServiceUtils;

@Service
public class DeliverService {
    @Autowired
    private ServiceUtils serviceUtils;

    /**
     * Deliver a message to the corresponding participant
     * based on the receiver internal configuration
     * resolved by {@link eu.de4a.connector.config.AddressesProperties}
     *
     * @param docMsg - DOM Document with the message
     * @param senderID - Sender participant identifier
     * @param receiverID - Receiver participant identifier
     * @param logMessage - Log tag for i18n
     * @return ResponseEntity with the response of the external service
     */
    public ResponseEntity<byte[]> pushMessage(final Document docMsg, final String senderID, final String receiverID,
            final ELogMessages logMessage) {

        final String requestID = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_REQUEST_ID,
                docMsg.getDocumentElement());

        final String elemType = docMsg.getDocumentElement().getNodeName();
        final EMessageServiceTypes eMessageServiceTypes = EMessageServiceTypes.getByTypeOrNull(elemType);
        if (eMessageServiceTypes == null)
          throw new IllegalStateException ("Failed to resolve message type from XML document element local name '"+elemType+"'");

        // Get where has to be sent depending of the content
        final String url = this.serviceUtils.getParticipantAddress(receiverID, eMessageServiceTypes.getEndpointType(),
                eMessageServiceTypes.isRequest());

        KafkaClientWrapper.sendInfo(logMessage, eMessageServiceTypes.getType(),
                requestID, senderID, receiverID, url);

        //Send message
        return APIRestUtils.postRestObjectWithCatching(url, DOMUtils.documentToByte(docMsg),
                 new ConnectorException().withModule(EExternalModuleError.DATA_OWNER));
    }
}
