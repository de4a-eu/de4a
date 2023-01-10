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
import com.helger.dcng.api.DcngConfig;
import com.helger.dcng.api.me.model.MEMessage;
import com.helger.dcng.api.me.model.MEPayload;
import com.helger.dcng.core.regrep.DcngRegRepHelperIt2;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.XMLFactory;
import com.helger.xml.XMLHelper;

import eu.de4a.connector.api.legacy.LegacyAPIHelper;
import eu.de4a.connector.api.service.DeliverService;
import eu.de4a.connector.api.service.DeliverServiceIT1;
import eu.de4a.connector.api.service.model.EMessageServiceType;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.utils.MessageUtils;
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
import eu.de4a.kafkaclient.model.ELogMessage;

@Component
public class MessageExchangeManager
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MessageExchangeManager.class);
  private static final String XPATH_REQUEST_ID = "//*[local-name()='RequestId']/text()";

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
    LOGGER.info ("Now processing AS4 message with MessageExchangeManager");

    final MEMessage meMessage = messageWrapper.getMeMessage ();

    final String senderID = meMessage.getSenderID ().getURIEncoded ();
    final String receiverID = meMessage.getReceiverID ().getURIEncoded ();
    final IProcessIdentifier aProcessID = meMessage.getProcessID ();
    String metadata = "";

    LOGGER.info ("  Sender=" + senderID);
    LOGGER.info ("  Receiver=" + receiverID);
    LOGGER.info ("  ProcessID=" + (aProcessID == null ? null : aProcessID.getURIEncoded ()));

    if (meMessage.payloads ().size () != 1)
      throw new IllegalArgumentException ("Expecting the AS4 message to have exactly one payload only, but found " +
                                          meMessage.payloads ().size () +
                                          " payloads");

    // This is the RegRep
    final MEPayload aPayload = messageWrapper.getMeMessage ().payloads ().get (0);

    LOGGER.info ("  Now trying to extract Payload from RegRep");

    // Extract payload from RegRep
    final Element aRegRepElement = DcngRegRepHelperIt2.extractPayload (aPayload.getData ());
    if (aRegRepElement == null)
      throw new IllegalStateException ("Failed to extract the payload from the anticipated RegRep message - see the log for details");

    final String elementLocalName = XMLHelper.getLocalNameOrTagName (aRegRepElement);
    LOGGER.info ("  Now trying to find the Message Service for element '" + elementLocalName + "'");

    final EMessageServiceType eMessageServiceType = EMessageServiceType.getByElementLocalNameOrNull (elementLocalName);
    if (eMessageServiceType == null)
      throw new IllegalStateException ("Failed to resolve message type from XML document element local name '" +
                                       elementLocalName +
                                       "'");

    // Create a new document with the payload only

    final ResponseEntity <byte []> response;
    String sIteration1RememberID = null;
    RequestExtractMultiEvidenceIMType aNewRequest = null;
    final boolean bIsRequestForDO;
    ELogMessage logMessage;
    switch (aProcessID.getValue ())
    {
      case DE4AConstants.PROCESS_ID_REQUEST:
      {
        bIsRequestForDO = true;
        boolean backwardsCompatibility = false;
        Document aTargetDoc = null;
        switch (eMessageServiceType)
        {
          case IM:
          {
            LOGGER.info ("Sending IM request from DT to DO");
            logMessage = ELogMessage.LOG_REQ_IM_DR_DT;
            final var aDRRequest = DE4ACoreMarshaller.drRequestTransferEvidenceIMMarshaller ().read (aRegRepElement);
            metadata = MessageUtils.getRequestMetadata (aDRRequest.getRequestEvidenceIMItem ());

            // check if is a 1st iteration message
            final RequestEvidenceItemType itemRequest = aDRRequest.getRequestEvidenceIMItemAtIndex (0);
            if (itemRequest != null && itemRequest.hasAdditionalParameterEntries ())
            {
              final AdditionalParameterType addParam = itemRequest.getAdditionalParameter ().get (0);
              if (addParam != null && addParam.getLabel ().equals ("iteration") && addParam.getValue ().equals ("1"))
              {
                LOGGER.info ("backwardsCompatibility enabled");
                logMessage = ELogMessage.LOG_REQ_IM_LEGACY_DR_DT;
                backwardsCompatibility = true;
                sIteration1RememberID = aDRRequest.getRequestId ();
                aNewRequest = aDRRequest;
                // convert new to old
                final RequestExtractEvidenceIMType aOldRequest = LegacyAPIHelper.convertNewToOldRequest (aDRRequest);
                aTargetDoc = DE4AMarshaller.doImRequestMarshaller ().getAsDocument (aOldRequest);
                metadata = MessageUtils.getLegacyRequestMetadata (aOldRequest.getRequestId (),
                                                                  aOldRequest.getCanonicalEvidenceTypeId ());
              }
            }

            if (aTargetDoc == null)
            {
              // Not Iteration 1
              aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceIMMarshaller ().getAsDocument (aDRRequest);

              if (aTargetDoc == null)
                throw new IllegalStateException ("Failed sending IM request from DT to DO");
            }

            break;
          }
          case USI:
          {
            LOGGER.info ("Sending USI request from DT to DO");
            logMessage = ELogMessage.LOG_REQ_USI_DR_DT;
            final var aDRRequest = DE4ACoreMarshaller.drRequestTransferEvidenceUSIMarshaller ().read (aRegRepElement);
            aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceUSIMarshaller ().getAsDocument (aDRRequest);
            metadata = MessageUtils.getRequestMetadata (aDRRequest.getRequestEvidenceUSIItem ());

            if (aTargetDoc == null)
              throw new IllegalStateException ("Failed to convert USI request from DR to DO");
            break;
          }
          case LU:
          {
            LOGGER.info ("Sending LU request from DT to DO");
            logMessage = ELogMessage.LOG_REQ_LU_DR_DT;
            final var aDRRequest = DE4ACoreMarshaller.drRequestTransferEvidenceLUMarshaller ().read (aRegRepElement);
            aTargetDoc = DE4ACoreMarshaller.doRequestExtractMultiEvidenceLUMarshaller ().getAsDocument (aDRRequest);
            metadata = MessageUtils.getLookupRequestMetadata (aDRRequest.getRequestEvidenceLUItem ());

            if (aTargetDoc == null)
              throw new IllegalStateException ("Failed to convert LU request from DR to DO");
            break;
          }
          case SN:
          {
            LOGGER.info ("Sending SN request from DT to DO");
            logMessage = ELogMessage.LOG_REQ_SUBSC_DR_DT;
            final Document aNewDoc = XMLFactory.newDocument ();
            aNewDoc.appendChild (aNewDoc.adoptNode (aRegRepElement.cloneNode (true)));
            aTargetDoc = aNewDoc;
            break;
          }
          default:
            LOGGER.error ("Unsupported message type " + eMessageServiceType);
            throw new IllegalStateException ("Unsupported message type " + eMessageServiceType);
        }

        if (backwardsCompatibility)
        {
          response = this.deliverServiceIT1.pushMessage (eMessageServiceType,
                                                         aTargetDoc,
                                                         senderID,
                                                         receiverID,
                                                         logMessage,
                                                         metadata);
        }
        else
        {
          response = this.deliverService.pushMessage (eMessageServiceType,
                                                      aTargetDoc,
                                                      senderID,
                                                      receiverID,
                                                      logMessage,
                                                      metadata);
        }

        if (HttpStatus.OK.equals (response.getStatusCode ()))
          LOGGER.info ("Message successfully sent to the Data Owner");
        else
          LOGGER.error ("Error connecting with the Data Owner (status " + response.getStatusCode () + ")");
        break;
      }
      case DE4AConstants.PROCESS_ID_RESPONSE:
      case DE4AConstants.PROCESS_ID_NOTIFICATION:
      {
        bIsRequestForDO = false;
        final Document aTargetDoc;
        switch (eMessageServiceType)
        {
          // Dunno if we need translations
          default:
          {
            LOGGER.info ("Sending " +
                         eMessageServiceType +
                         "(" +
                         eMessageServiceType.getElementLocalName () +
                         ") request from DR to DE");
            final Document aNewDoc = XMLFactory.newDocument ();
            aNewDoc.appendChild (aNewDoc.adoptNode (aRegRepElement.cloneNode (true)));
            aTargetDoc = aNewDoc;
            switch (eMessageServiceType)
            {
              case RESPONSE:
                final var rte = DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller (eu.de4a.iem.core.IDE4ACanonicalEvidenceType.NONE)
                                                  .read (aRegRepElement);
                metadata = MessageUtils.getEvidenceResponseMetadata (rte.getResponseExtractEvidenceItem ());
                logMessage = ELogMessage.LOG_RES_EVIDENCE_DT_DR;
                if (StringHelper.hasText (sIteration1RememberID))
                  logMessage = ELogMessage.LOG_RES_IM_LEGACY_DT_DR;
                break;

              case REDIRECT:
                logMessage = ELogMessage.LOG_RES_REDIRECT_DT_DR;
                final var uru = DE4ACoreMarshaller.dtUSIRedirectUserMarshaller ().read (aRegRepElement);
                metadata = MessageUtils.getRedirectResponseMetadata (uru);
                break;

              case SUBSCRIPTION_RESP:
                logMessage = ELogMessage.LOG_RES_SUBSC_DT_DR;
                final var res = DE4ACoreMarshaller.dtResponseEventSubscriptionMarshaller ().read (aRegRepElement);
                metadata = MessageUtils.getEventSubscriptionResponseMetadata (res.getResponseEventSubscriptionItem ());
                break;

              case NOTIFICATION:
                logMessage = ELogMessage.LOG_EVENT_NOTIF_DT_DR;
                final var en = DE4ACoreMarshaller.dtEventNotificationMarshaller ().read (aRegRepElement);
                metadata = MessageUtils.getEventNotificationMetadata (en.getEventNotificationItem ());
                break;

              default:
                LOGGER.warn ("Unsupported message type " + eMessageServiceType);
                logMessage = ELogMessage.LOG_REQ_DE;
            }
          }
        }

        response = this.deliverService.pushMessage (eMessageServiceType,
                                                    aTargetDoc,
                                                    senderID,
                                                    receiverID,
                                                    logMessage,
                                                    metadata);

        if (HttpStatus.OK.equals (response.getStatusCode ()))
          LOGGER.info ("Message successfully sent to the Data Evaluator");
        else
          LOGGER.error ("Error connecting with the Data Evaluator (status " + response.getStatusCode () + ")");
        break;
      }
      default:
        bIsRequestForDO = false;
        response = null;
        LOGGER.error ("ProcessID exchanged is not found: " + aProcessID.getValue ());
    }

    if (StringHelper.hasText (sIteration1RememberID))
    {
      // This only affects the backwards compatibility layer
      // It deals with the synchronous response of sending the legacy IM request
      final var aOldResponseMarshaller = DE4AMarshaller.doImResponseMarshaller (IDE4ACanonicalEvidenceType.NONE);
      final ResponseExtractEvidenceType aOldResponse = aOldResponseMarshaller.read (response.getBody ());
      final ResponseExtractMultiEvidenceType aNewResponse = LegacyAPIHelper.convertOldToNewResponse (aOldResponse,
                                                                                                     aNewRequest);
      final var marshaller = DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller (eu.de4a.iem.core.IDE4ACanonicalEvidenceType.NONE);

      final AS4MessageDTO messageDTO = new AS4MessageDTO (aNewResponse.getDataOwner ().getAgentUrn (),
                                                          aNewResponse.getDataEvaluator ().getAgentUrn (),
                                                          DcngConfig.getIdentifierFactory ()
                                                                    .parseDocumentTypeIdentifier (aNewResponse.getResponseExtractEvidenceItemAtIndex (0)
                                                                                                              .getCanonicalEvidenceTypeId ()),
                                                          DE4AConstants.PROCESS_ID_RESPONSE);

      metadata = MessageUtils.getEvidenceResponseMetadata (aNewResponse.getResponseExtractEvidenceItem ());

      this.apiManager.processIncomingMessage (ELogMessage.LOG_RES_IM_LEGACY_DT_DR,
                                              aNewResponse,
                                              messageDTO,
                                              marshaller,
                                              aNewResponse.getRequestId (),
                                              metadata);
    }
    else
    {
      // The response forwarded to the DO does not need to return this API
      if (bIsRequestForDO && response != null)
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
