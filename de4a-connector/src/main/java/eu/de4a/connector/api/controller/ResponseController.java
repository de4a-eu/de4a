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
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;

@Controller
@RequestMapping ("/response")
@Validated
public class ResponseController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ResponseController.class);

  @Autowired
  private APIManager apiManager;

  @PostMapping (value = "/usi/redirectUser/", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> redirectUserUsi (@Valid final InputStream request)
  {
    LOGGER.info ("Request to API /response/usi/redirectUser/ received");

    final var marshaller = DE4ACoreMarshaller.dtUSIRedirectUserMarshaller ();

    final RedirectUserType redirectUserMsg = APIRestUtils.conversionBytesWithCatching (request,
                                                                                       marshaller,
                                                                                       new ConnectorException ().withModule (EExternalModuleError.CONNECTOR_DT));

    final AS4MessageDTO messageDTO = new AS4MessageDTO (redirectUserMsg.getDataEvaluator ().getAgentUrn (),
                                                        redirectUserMsg.getDataOwner ().getAgentUrn (),
                                                        redirectUserMsg.getCanonicalEvidenceTypeId (),
                                                        DE4AConstants.PROCESS_ID_RESPONSE);

    this.apiManager.processIncomingMessage (redirectUserMsg, messageDTO, redirectUserMsg.getRequestId (), "Redirect User", marshaller);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getResponseErrorObjectBytes (null));
  }

  @PostMapping (value = "/evidence/", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> responseEvidence (@Valid final InputStream request)
  {
    LOGGER.info ("Request to API /response/evidence/ received");

    final var marshaller = DE4ACoreMarshaller.dtResponseExtractMultiEvidenceMarshaller (IDE4ACanonicalEvidenceType.NONE);

    final ResponseExtractMultiEvidenceType responseObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                                   marshaller,
                                                                                                   new ConnectorException ().withModule (EExternalModuleError.CONNECTOR_DT));

    // Check if there are multiple evidence responses
    final String docTypeID;
    if (responseObj.getResponseExtractEvidenceItemCount () > 1)
    {
      docTypeID = CIdentifier.getURIEncoded (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVIDENCE, DE4AConstants.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = responseObj.getResponseExtractEvidenceItemAtIndex (0).getCanonicalEvidenceTypeId ();
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (responseObj.getDataOwner ().getAgentUrn (),
                                                        responseObj.getDataEvaluator ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_RESPONSE);

    this.apiManager.processIncomingMessage (responseObj, messageDTO, responseObj.getRequestId (), "Response Evidence", marshaller);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getResponseErrorObjectBytes (null));
  }

  @PostMapping (value = "/subscription/", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> responseEventSubscription (@Valid final InputStream request)
  {
    LOGGER.info ("Request to API /response/subscription/ received");

    final var marshaller = DE4ACoreMarshaller.dtResponseEventSubscriptionMarshaller ();

    final ResponseEventSubscriptionType responseObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                                marshaller,
                                                                                                new ConnectorException ().withModule (EExternalModuleError.CONNECTOR_DT));

    // Check if there are multiple evidence responses
    final String docTypeID;
    if (responseObj.getResponseEventSubscriptionItemCount () > 1)
    {
      docTypeID = CIdentifier.getURIEncoded (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVENT_CATALOGUE, DE4AConstants.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = responseObj.getResponseEventSubscriptionItemAtIndex (0).getCanonicalEventCatalogUri ();
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (responseObj.getDataEvaluator ().getAgentUrn (),
                                                        responseObj.getDataOwner ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_RESPONSE);

    this.apiManager.processIncomingMessage (responseObj, messageDTO, responseObj.getRequestId (), "Response Evidence", marshaller);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getResponseErrorObjectBytes (null));
  }
}
