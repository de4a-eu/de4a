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

import com.helger.commons.string.StringHelper;
import com.helger.dcng.api.me.model.MEMessage;
import com.helger.dcng.api.me.model.MEPayload;
import com.helger.dcng.core.regrep.DcngRegRepHelperIt2;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.XMLFactory;

import eu.de4a.connector.api.legacy.LegacyAPIHelper;
import eu.de4a.connector.api.service.DeliverService;
import eu.de4a.connector.api.service.DeliverServiceIT1;
import eu.de4a.connector.api.service.model.EMessageServiceType;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.model.ELogMessage;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.AdditionalParameterType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.ResponseErrorType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;

@Component
public class MessageExchangeManager
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MessageExchangeManager.class);

  @Autowired
  private DeliverService deliverService;
  @Autowired
  private DeliverServiceIT1 deliverServiceIT1;
  @Autowired
  private APIManager apiManager;

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
      throw new IllegalStateException ("Failed to resolve message type from XML document element local name '" +
                                       elemType +
                                       "'");

    // Create a new document with the payload only

    ResponseEntity <byte []> response = null;
    String rememberID = "";
    RequestExtractMultiEvidenceIMType aNewRequest = null;
    switch (aProcessID.getValue ())
    {
      case DE4AConstants.PROCESS_ID_REQUEST:
      {
        boolean backwardsCompatibility = false;
        Document aTargetDoc = null;
        switch (eMessageServiceType)
        {
          case IM:
          {
            LOGGER.info ("Converting IM request from DT to DO");
            final var aDRRequest = DE4ACoreMarshaller.drRequestTransferEvidenceIMMarshaller ().read (aRegRepElement);

            // check if is a 1st iteration message
            final RequestEvidenceItemType itemRequest = aDRRequest.getRequestEvidenceIMItemAtIndex (0);
            if (itemRequest != null &&
                itemRequest.hasAdditionalParameterEntries ())
            {
              final AdditionalParameterType addParam = itemRequest.getAdditionalParameter ().get (0);
              if (addParam != null && addParam.getLabel ().equals ("iteration") && addParam.getValue ().equals ("1"))
              {
                LOGGER.info ("backwardsCompatibility enabled");
                backwardsCompatibility = true;
                rememberID = aDRRequest.getRequestId ();
                aNewRequest = aDRRequest;
                // convert new to old
                final RequestExtractEvidenceIMType aOldRequest = LegacyAPIHelper.convertNewToOldRequest (aDRRequest);
                aTargetDoc = DE4AMarshaller.doImRequestMarshaller ().getAsDocument (aOldRequest);
              }
            }

            if (aTargetDoc == null)
            {
              // Not Iteration 1
              aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceIMMarshaller ().getAsDocument (aDRRequest);
            }
            break;
          }
          case USI:
          {
            LOGGER.info ("Converting USI request from DR to DO");
            final var aDRRequest = DE4ACoreMarshaller.drRequestTransferEvidenceUSIMarshaller ().read (aRegRepElement);
            aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceUSIMarshaller ().getAsDocument (aDRRequest);
            break;
          }
          case LU:
          {
            LOGGER.info ("Converting LU request from DR to DO");
            final var aDRRequest = DE4ACoreMarshaller.drRequestTransferEvidenceLUMarshaller ().read (aRegRepElement);
            aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceLUMarshaller ().getAsDocument (aDRRequest);
            break;
          }
          case SN:
          {
            LOGGER.info ("Copying SN request from DR to DO");
            final Document aNewDoc = XMLFactory.newDocument ();
            aNewDoc.appendChild (aNewDoc.adoptNode (aRegRepElement.cloneNode (true)));
            aTargetDoc = aNewDoc;
            break;
          }
          default:
            throw new IllegalStateException ("Unsupported message type " + eMessageServiceType);
        }

        if (backwardsCompatibility)
        {
          response = this.deliverServiceIT1.pushMessage (eMessageServiceType,
                                                         aTargetDoc,
                                                         senderID,
                                                         receiverID,
                                                         ELogMessage.LOG_REQ_DO);
        }
        else
        {
          response = this.deliverService.pushMessage (eMessageServiceType,
                                                      aTargetDoc,
                                                      senderID,
                                                      receiverID,
                                                      ELogMessage.LOG_REQ_DO);
        }

        if (HttpStatus.OK.equals (response.getStatusCode ()))
          LOGGER.info ("Message successfully sent to the Data Owner");
        else
          LOGGER.error ("Error connecting with the Data Owner");
        break;
      }
      case DE4AConstants.PROCESS_ID_RESPONSE:
      {
        final Document aTargetDoc;
        switch (eMessageServiceType)
        {
          // Dunno if we need translations
          default:
          {
            if (LOGGER.isInfoEnabled ())
              LOGGER.info ("Copying " + eMessageServiceType + " request from DT to DE");
            final Document aNewDoc = XMLFactory.newDocument ();
            aNewDoc.appendChild (aNewDoc.adoptNode (aRegRepElement.cloneNode (true)));
            aTargetDoc = aNewDoc;
          }
        }
        response = this.deliverService.pushMessage (eMessageServiceType,
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

    if (StringHelper.hasText (rememberID))
    {
      final var aOldResponseMarshaller = DE4AMarshaller.doImResponseMarshaller (IDE4ACanonicalEvidenceType.NONE);
      final ResponseExtractEvidenceType aOldResponse = aOldResponseMarshaller.read (response.getBody ());
      final ResponseExtractMultiEvidenceType aNewResponse = LegacyAPIHelper.convertOldToNewResponse (aOldResponse,
                                                                                                     aNewRequest);
      final var marshaller = DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller (eu.de4a.iem.core.IDE4ACanonicalEvidenceType.NONE);

      final AS4MessageDTO messageDTO = new AS4MessageDTO (aNewResponse.getDataOwner ().getAgentUrn (),
                                                          aNewResponse.getDataEvaluator ().getAgentUrn (),
                                                          aNewResponse.getResponseExtractEvidenceItemAtIndex (0)
                                                                      .getCanonicalEvidenceTypeId (),
                                                          DE4AConstants.PROCESS_ID_RESPONSE);
      this.apiManager.processIncomingMessage (aNewResponse,
                                              messageDTO,
                                              aNewResponse.getRequestId (),
                                              "Response Evidence",
                                              marshaller);
    }
    else
    {
      if (response != null)
      {
        final ResponseErrorType aResponse = DE4ACoreMarshaller.defResponseMarshaller ().read (response.getBody ());
        if (aResponse != null)
        {
          if (aResponse.isAck ())
          {
            LOGGER.info ("DO accepted our request");
          }
          else
          {
            LOGGER.error ("DO rejected our request");
            aResponse.getError ().forEach (x -> LOGGER.error ("  DO Error [" + x.getCode () + "] " + x.getText ()));
          }
        }
        else
          LOGGER.warn ("Failed to interprete the DO response as a ResponseErrorType");
      }
    }
  }
}
