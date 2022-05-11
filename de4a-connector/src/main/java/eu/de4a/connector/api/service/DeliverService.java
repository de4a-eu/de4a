package eu.de4a.connector.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.helger.commons.string.StringHelper;

import eu.de4a.connector.api.legacy.LegacyAPIHelper;
import eu.de4a.connector.api.service.model.EMessageServiceTypes;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.error.model.ELogMessages;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.connector.utils.DOMUtils;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.connector.utils.ServiceUtils;

@Service
public class DeliverService
{
  private static final String XPATH_REQUEST_ID = "//*[local-name()='RequestId']/text()";

  @Autowired
  private ServiceUtils serviceUtils;

  /**
   * Deliver a message to the corresponding participant based on the receiver
   * internal configuration resolved by
   * {@link eu.de4a.connector.config.AddressesProperties}
   *
   * @param docMsg
   *        - DOM Document with the message
   * @param senderID
   *        - Sender participant identifier
   * @param receiverID
   *        - Receiver participant identifier
   * @param logMessage
   *        - Log tag for i18n
   * @return ResponseEntity with the response of the external service
   */
  public ResponseEntity <byte []> pushMessage (final Document docMsg,
                                               final String senderID,
                                               final String receiverID,
                                               final ELogMessages logMessage)
  {
    // Generic way for all request IDs
    final String sRequestID = DOMUtils.getValueFromXpath (XPATH_REQUEST_ID, docMsg.getDocumentElement ());

    if (StringHelper.hasText (sRequestID))
      LegacyAPIHelper.rememberFinalized (sRequestID, docMsg);

    final String elemType = docMsg.getDocumentElement ().getNodeName ();
    final EMessageServiceTypes eMessageServiceTypes = EMessageServiceTypes.getByTypeOrNull (elemType);
    if (eMessageServiceTypes == null)
      throw new IllegalStateException ("Failed to resolve message type from XML document element local name '" + elemType + "'");

    // Get where has to be sent depending of the content
    final String url = this.serviceUtils.getParticipantAddress (receiverID,
                                                                eMessageServiceTypes.getEndpointType (),
                                                                eMessageServiceTypes.isRequest ());
    if (url == null)
      throw new IllegalStateException ("Failed to determine DE/DO URL for receiver '" +
                                       receiverID +
                                       "' and message type " +
                                       eMessageServiceTypes);

    KafkaClientWrapper.sendInfo (logMessage, eMessageServiceTypes.getType (), sRequestID, senderID, receiverID, url);

    // Send message
    return APIRestUtils.postRestObjectWithCatching (url,
                                                    DOMUtils.documentToByte (docMsg),
                                                    new ConnectorException ().withModule (EExternalModuleError.DATA_OWNER));
  }
}
