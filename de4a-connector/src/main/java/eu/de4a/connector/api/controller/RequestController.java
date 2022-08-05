package eu.de4a.connector.api.controller;

import java.io.InputStream;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.helger.dcng.api.DcngIdentifierFactory;
import com.helger.peppolid.CIdentifier;

import eu.de4a.connector.api.manager.APIManager;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.connector.utils.MessageUtils;
import eu.de4a.iem.core.CIEM;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceLUType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;
import eu.de4a.kafkaclient.model.EExternalModule;
import eu.de4a.kafkaclient.model.ELogMessage;

@Controller
@RequestMapping ("/request")
@Validated
public class RequestController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (RequestController.class);

  @Autowired
  private APIManager apiManager;

  @PostMapping (value = "/im", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> requestEvidenceIM (@Valid final InputStream request)
  {
    LOGGER.info ("[DE-DR] Request to API /request/im/ received");

    final var marshaller = DE4ACoreMarshaller.drRequestTransferEvidenceIMMarshaller ();

    // Unmarshalling and schema validation
    final RequestExtractMultiEvidenceIMType requestObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                                   marshaller,
                                                                                                   new ConnectorException ().withModule (EExternalModule.CONNECTOR_DR));
    
    if (requestObj.hasNoRequestEvidenceIMItemEntries ())
      throw new IllegalStateException ("Provided payload has no RequestEvidenceIMItem entries");

    // Check if there are multiple evidence request
    final String docTypeID;
    if (requestObj.getRequestEvidenceIMItemCount () > 1)
    {
      docTypeID = CIdentifier.getURIEncoded (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVIDENCE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = requestObj.getRequestEvidenceIMItemAtIndex (0).getCanonicalEvidenceTypeId ();
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (requestObj.getDataEvaluator ().getAgentUrn (),
                                                        requestObj.getDataOwner ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_REQUEST);

    String requestMetadata = MessageUtils.getRequestMetadata(requestObj.getRequestEvidenceIMItem());
    this.apiManager.processIncomingMessage (ELogMessage.LOG_REQ_IM_DE_DR, requestObj, messageDTO, marshaller, requestObj.getRequestId(), requestMetadata);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }

  @PostMapping (value = "/usi", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> requestEvidenceUSI (@Valid final InputStream request)
  {
    LOGGER.info ("[DE-DR] Request to API /request/usi/ received");

    final var marshaller = DE4ACoreMarshaller.drRequestTransferEvidenceUSIMarshaller ();

    // Unmarshalling and schema validation
    final RequestExtractMultiEvidenceUSIType requestObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                                    marshaller,
                                                                                                    new ConnectorException ().withModule (EExternalModule.CONNECTOR_DR));
    if (requestObj.hasNoRequestEvidenceUSIItemEntries ())
      throw new IllegalStateException ("Provided payload has no RequestEvidenceUSIItem entries");

    // Check if there are multiple evidence request
    final String docTypeID;
    if (requestObj.getRequestEvidenceUSIItemCount () > 1)
    {
      docTypeID = CIdentifier.getURIEncoded (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVIDENCE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = requestObj.getRequestEvidenceUSIItemAtIndex (0).getCanonicalEvidenceTypeId ();
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (requestObj.getDataEvaluator ().getAgentUrn (),
                                                        requestObj.getDataOwner ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_REQUEST);

    String requestMetadata = MessageUtils.getRequestMetadata(requestObj.getRequestEvidenceUSIItem());
    this.apiManager.processIncomingMessage (ELogMessage.LOG_REQ_USI_DE_DR, requestObj, messageDTO, marshaller, requestObj.getRequestId(), requestMetadata);
    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }

  @PostMapping (value = "/lu", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> requestEvidenceLU (@Valid final InputStream request)
  {
    LOGGER.info ("[DE-DR] Request to API /request/lu/ received");

    final var marshaller = DE4ACoreMarshaller.drRequestTransferEvidenceLUMarshaller ();

    // Unmarshalling and schema validation
    final RequestExtractMultiEvidenceLUType requestObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                                   marshaller,
                                                                                                   new ConnectorException ().withModule (EExternalModule.CONNECTOR_DR));
    if (requestObj.hasNoRequestEvidenceLUItemEntries ())
      throw new IllegalStateException ("Provided payload has no RequestEvidenceLUItem entries");

    // Check if there are multiple evidence request
    final String docTypeID;
    if (requestObj.getRequestEvidenceLUItemCount () > 1)
    {
      docTypeID = CIdentifier.getURIEncoded (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVIDENCE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = requestObj.getRequestEvidenceLUItemAtIndex (0).getCanonicalEvidenceTypeId ();
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (requestObj.getDataEvaluator ().getAgentUrn (),
                                                        requestObj.getDataOwner ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_REQUEST);

    String requestMetadata = MessageUtils.getLookupRequestMetadata(requestObj.getRequestEvidenceLUItem());
    this.apiManager.processIncomingMessage (ELogMessage.LOG_REQ_LU_DE_DR, requestObj, messageDTO, marshaller, requestObj.getRequestId(), requestMetadata);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }

  @PostMapping (value = "/subscription", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> requestEventSubscription (@Valid final InputStream request)
  {
    LOGGER.info ("[DE-DR] Request to API /request/subscription/ received");

    final var marshaller = DE4ACoreMarshaller.drRequestEventSubscriptionMarshaller ();

    // Unmarshalling and schema validation
    final RequestEventSubscriptionType requestObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                              marshaller,
                                                                                              new ConnectorException ().withModule (EExternalModule.CONNECTOR_DR));
    if (requestObj.hasNoEventSubscripRequestItemEntries ())
      throw new IllegalStateException ("Provided payload has no EventSubscripRequestItem entries");

    // Check if there are multiple evidence request
    final String docTypeID;
    if (requestObj.getEventSubscripRequestItemCount () > 1)
    {
      docTypeID = CIdentifier.getURIEncoded (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVENT_CATALOGUE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = requestObj.getEventSubscripRequestItemAtIndex (0).getCanonicalEventCatalogUri ();
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (requestObj.getDataEvaluator ().getAgentUrn (),
                                                        requestObj.getDataOwner ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_REQUEST);

    String requestMetadata = MessageUtils.getSubscriptionRequestMetadata(requestObj.getEventSubscripRequestItem());
    this.apiManager.processIncomingMessage (ELogMessage.LOG_REQ_SUBSC_DE_DR, requestObj, messageDTO, marshaller, requestObj.getRequestId(), requestMetadata);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }
}
