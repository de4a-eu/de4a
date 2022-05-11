package eu.de4a.connector.api.manager;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.dcng.api.me.model.MEMessage;
import com.helger.dcng.api.me.model.MEPayload;
import com.helger.dcng.core.regrep.DcngRegRepHelperIt2;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.XMLFactory;

import eu.de4a.connector.api.service.DeliverService;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.error.model.ELogMessages;

@Component
public class MessageExchangeManager
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MessageExchangeManager.class);

  @Autowired
  private DeliverService deliverService;

  /**
   * Process message exchange wrapper. Include the business logic to forward the
   * message to the corresponding external component (DE/DO)
   *
   * @param messageWrapper
   */
  public void processMessageExchange (@Nonnull final MessageExchangeWrapper messageWrapper)
  {
    final MEMessage meMessage = messageWrapper.getMeMessage ();

    final IProcessIdentifier iProcessID = meMessage.getProcessID ();
    final String receiverID = meMessage.getReceiverID ().getURIEncoded ();
    final String senderID = meMessage.getSenderID ().getURIEncoded ();

    if (meMessage.payloads ().size () != 1)
      throw new IllegalArgumentException ("Expecting the AS4 message to have exactly one payload only, but found " +
                                          meMessage.payloads ().size () +
                                          " payloads");

    // This is the RegRep
    final MEPayload aPayload = messageWrapper.getMeMessage ().payloads ().get (0);
    final Element aRegRepElement = DcngRegRepHelperIt2.extractPayload (aPayload.getData ());
    if (aRegRepElement == null)
      throw new IllegalStateException ("Failed to extract the payload from the anticipated RegRep message - see the log for details");

    // Create a new document with the payload only
    final Document aRegRepDoc = XMLFactory.newDocument ();
    aRegRepDoc.appendChild (aRegRepDoc.adoptNode (aRegRepElement.cloneNode (true)));

    ResponseEntity <byte []> response;
    switch (iProcessID.getValue ())
    {
      case DE4AConstants.PROCESS_ID_REQUEST:
        response = this.deliverService.pushMessage (aRegRepDoc, senderID, receiverID, ELogMessages.LOG_REQ_DO);
        if (HttpStatus.OK.equals (response.getStatusCode ()))
          LOGGER.info ("Message successfully sent to the Data Owner");
        else
          LOGGER.error ("Error connecting with the Data Owner");
        break;
      case DE4AConstants.PROCESS_ID_RESPONSE:
        response = this.deliverService.pushMessage (aRegRepDoc, senderID, receiverID, ELogMessages.LOG_REQ_DE);
        if (HttpStatus.OK.equals (response.getStatusCode ()))
          LOGGER.info ("Message successfully sent to the Data Evaluator");
        else
          LOGGER.error ("Error connecting with the Data Evaluator");
        break;
      default:
        LOGGER.error ("ProcessID exchanged is not found: " + iProcessID.getValue ());
    }
  }
}
