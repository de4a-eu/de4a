package eu.de4a.connector.api.manager;

import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import com.helger.dcng.api.me.model.MEMessage;
import com.helger.dcng.api.me.model.MEPayload;
import com.helger.peppolid.IProcessIdentifier;
import eu.de4a.connector.api.service.DeliverService;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.error.model.ELogMessages;
import eu.de4a.connector.utils.DOMUtils;

@Component
public class MessageExchangeManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageExchangeManager.class);

  @Autowired
  private DeliverService deliverService;

  /**
   *
   * Process message exchange wrapper. Include the business logic to forward the message to the
   * corresponding external component (DE/DO)
   *
   * @param messageWrapper
   * @return void
   */
  public void processMessageExchange(@Nonnull final MessageExchangeWrapper messageWrapper) {
    final MEMessage meMessage = messageWrapper.getMeMessage();

    final IProcessIdentifier iProcessID = meMessage.getProcessID();
    final String receiverID = meMessage.getReceiverID().getURIEncoded();
    final String senderID = meMessage.getSenderID().getURIEncoded();

    final MEPayload payload;
    if (meMessage.payloads().size() >= 2) {
      // TODO Useless - First is for regRep - Has to be aligned with the Connector-NG
      payload = messageWrapper.getMeMessage().payloads().get(1);
    } else {
      LOGGER.error("Incoming message seems to be ill-formatted - too few payloads. " + "Trying first one.");
      payload = meMessage.payloads().getFirst();
    }
    final Document docMsg = DOMUtils.newDocumentFromInputStream(payload.getData().getInputStream());

    ResponseEntity<byte[]> response;
    switch (iProcessID.getValue()) {
      case DE4AConstants.PROCESS_ID_REQUEST:
        response = this.deliverService.pushMessage(docMsg, senderID, receiverID, ELogMessages.LOG_REQ_DO);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
          LOGGER.info("Message successfully sent to the Data Owner");
        } else {
          LOGGER.error("Error connecting with the Data Owner");
        }
        break;
      case DE4AConstants.PROCESS_ID_RESPONSE:
        response = this.deliverService.pushMessage(docMsg, senderID, receiverID, ELogMessages.LOG_REQ_DE);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
          LOGGER.info("Message successfully sent to the Data Evaluator");
        } else {
          LOGGER.error("Error connecting with the Data Evaluator");
        }
        break;
      default:
        LOGGER.error("ProcessID exchanged is not found: {}", iProcessID.getValue());
    }
  }
}
