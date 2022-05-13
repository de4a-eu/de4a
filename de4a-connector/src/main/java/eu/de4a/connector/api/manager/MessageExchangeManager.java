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
import eu.de4a.connector.api.service.model.EMessageServiceType;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.error.model.ELogMessage;
import eu.de4a.iem.core.DE4ACoreMarshaller;

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
   *        The wrapped incoming AS4 message
   */
  public void processMessageExchange (@Nonnull final MessageExchangeWrapper messageWrapper)
  {
    final MEMessage meMessage = messageWrapper.getMeMessage ();

    final String senderID = meMessage.getSenderID ().getURIEncoded ();
    final String receiverID = meMessage.getReceiverID ().getURIEncoded ();
    final IProcessIdentifier aProcessID = meMessage.getProcessID ();

    if (meMessage.payloads ().size () != 1)
      throw new IllegalArgumentException ("Expecting the AS4 message to have exactly one payload only, but found " +
                                          meMessage.payloads ().size () +
                                          " payloads");

    // This is the RegRep
    final MEPayload aPayload = messageWrapper.getMeMessage ().payloads ().get (0);
    // Extract payload from RegRep
    final Element aRegRepElement = DcngRegRepHelperIt2.extractPayload (aPayload.getData ());
    if (aRegRepElement == null)
      throw new IllegalStateException ("Failed to extract the payload from the anticipated RegRep message - see the log for details");

    final String elemType = aRegRepElement.getNodeName ();
    final EMessageServiceType eMessageServiceType = EMessageServiceType.getByTypeOrNull (elemType);
    if (eMessageServiceType == null)
      throw new IllegalStateException ("Failed to resolve message type from XML document element local name '" + elemType + "'");

    // Create a new document with the payload only

    switch (aProcessID.getValue ())
    {
      case DE4AConstants.PROCESS_ID_REQUEST:
      {
        Document aTargetDoc = null;
        switch (eMessageServiceType)
        {
          case IM:
          {
            LOGGER.info ("Converting IM request from DR to DO");
            final var aDRRequest = DE4ACoreMarshaller.drRequestExtractMultiEvidenceIMMarshaller ().read (aRegRepElement);
            aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceIMMarshaller ().getAsDocument (aDRRequest);
            break;
          }
          case USI:
          {
            LOGGER.info ("Converting USI request from DR to DO");
            final var aDRRequest = DE4ACoreMarshaller.drRequestExtractMultiEvidenceUSIMarshaller ().read (aRegRepElement);
            aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceUSIMarshaller ().getAsDocument (aDRRequest);
            break;
          }
          case LU:
          {
            LOGGER.info ("Converting LU request from DR to DO");
            final var aDRRequest = DE4ACoreMarshaller.drRequestExtractMultiEvidenceLUMarshaller ().read (aRegRepElement);
            aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceLUMarshaller ().getAsDocument (aDRRequest);
            break;
          }
          case SN:
          {
            LOGGER.info ("Copying SN request from DR to DO");
            final Document aNewDoc = XMLFactory.newDocument ();
            aNewDoc.appendChild (aNewDoc.adoptNode (aRegRepElement.cloneNode (true)));
            aTargetDoc = aNewDoc;
          }
          default:
            throw new IllegalStateException ("Unsupported message type " + eMessageServiceType);
        }

        final ResponseEntity <byte []> response = this.deliverService.pushMessage (eMessageServiceType,
                                                                                   aTargetDoc,
                                                                                   senderID,
                                                                                   receiverID,
                                                                                   ELogMessage.LOG_REQ_DO);
        if (HttpStatus.OK.equals (response.getStatusCode ()))
          LOGGER.info ("Message successfully sent to the Data Owner");
        else
          LOGGER.error ("Error connecting with the Data Owner");
        break;
      }
      case DE4AConstants.PROCESS_ID_RESPONSE:
      {
        Document aTargetDoc = null;
        switch (eMessageServiceType)
        {
          // Dunno if we need translations
          default:
          {
            LOGGER.info ("Copying " + eMessageServiceType + " request from DT to DE");
            final Document aNewDoc = XMLFactory.newDocument ();
            aNewDoc.appendChild (aNewDoc.adoptNode (aRegRepElement.cloneNode (true)));
            aTargetDoc = aNewDoc;
          }
        }
        final ResponseEntity <byte []> response = this.deliverService.pushMessage (eMessageServiceType,
                                                                                   aTargetDoc,
                                                                                   senderID,
                                                                                   receiverID,
                                                                                   ELogMessage.LOG_REQ_DE);
        if (HttpStatus.OK.equals (response.getStatusCode ()))
          LOGGER.info ("Message successfully sent to the Data Evaluator");
        else
          LOGGER.error ("Error connecting with the Data Evaluator");
        break;
      }
      default:
        LOGGER.error ("ProcessID exchanged is not found: " + aProcessID.getValue ());
    }
  }
}
