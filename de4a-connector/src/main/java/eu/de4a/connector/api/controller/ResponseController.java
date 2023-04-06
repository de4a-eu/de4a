/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Austrian Federal Computing Center (BRZ)
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import com.helger.dcng.api.DcngConfig;
import com.helger.dcng.api.DcngIdentifierFactory;
import com.helger.peppolid.IDocumentTypeIdentifier;

import eu.de4a.connector.api.manager.APIManager;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.connector.utils.MessageUtils;
import eu.de4a.iem.core.CIEM;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import eu.de4a.kafkaclient.model.EExternalModule;
import eu.de4a.kafkaclient.model.ELogMessage;

@Controller
@RequestMapping ("/response")
@Validated
public class ResponseController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ResponseController.class);

  @Autowired
  private APIManager apiManager;

  @PostMapping (value = "/usi/redirectUser", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> redirectUserUsi (@Valid final InputStream request)
  {
    LOGGER.info ("[DO-DT] Request to API /response/usi/redirectUser/ received");

    final var marshaller = DE4ACoreMarshaller.dtUSIRedirectUserMarshaller ();

    final RedirectUserType redirectUserMsg = APIRestUtils.conversionBytesWithCatching (request,
                                                                                       marshaller,
                                                                                       new ConnectorException ().withModule (EExternalModule.CONNECTOR_DT));

    final AS4MessageDTO messageDTO = new AS4MessageDTO (redirectUserMsg.getDataOwner ().getAgentUrn (),
                                                        redirectUserMsg.getDataEvaluator().getAgentUrn (),
                                                        DcngConfig.getIdentifierFactory ().parseDocumentTypeIdentifier (redirectUserMsg.getCanonicalEvidenceTypeId ()),
                                                        DE4AConstants.PROCESS_ID_RESPONSE);

    final String responseMetadata = MessageUtils.getRedirectResponseMetadata(redirectUserMsg);
    this.apiManager.processIncomingMessage (ELogMessage.LOG_RES_REDIRECT_DO_DT,
    		redirectUserMsg, messageDTO, marshaller, redirectUserMsg.getRequestId(), responseMetadata);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }

  @PostMapping (value = "/evidence", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> responseEvidence (@Valid final InputStream request)
  {
    LOGGER.info ("[DO-DT] Request to API /response/evidence/ received");

    final var marshaller = DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller (IDE4ACanonicalEvidenceType.NONE);

    final ResponseExtractMultiEvidenceType responseObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                                   marshaller,
                                                                                                   new ConnectorException ().withModule (EExternalModule.CONNECTOR_DT));

    if (responseObj.hasNoResponseExtractEvidenceItemEntries ())
      throw new IllegalStateException ("Provided payload has no ResponseExtractEvidenceItem entries");

    // Check if there are multiple evidence responses
    final IDocumentTypeIdentifier docTypeID;
    if (responseObj.getResponseExtractEvidenceItemCount () > 1)
    {
      docTypeID = DcngConfig.getIdentifierFactory ().createDocumentTypeIdentifier (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVIDENCE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = DcngConfig.getIdentifierFactory ().parseDocumentTypeIdentifier (responseObj.getResponseExtractEvidenceItemAtIndex (0).getCanonicalEvidenceTypeId ());
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (responseObj.getDataOwner ().getAgentUrn (),
                                                        responseObj.getDataEvaluator ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_RESPONSE);

    final String responseMetadata = MessageUtils.getEvidenceResponseMetadata(responseObj.getResponseExtractEvidenceItem());
    this.apiManager.processIncomingMessage (ELogMessage.LOG_RES_EVIDENCE_DO_DT,
    		responseObj, messageDTO, marshaller, responseObj.getRequestId(), responseMetadata);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }

  @PostMapping (value = "/subscription", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> responseEventSubscription (@Valid final InputStream request)
  {
    LOGGER.info ("[DO-DT] Request to API /response/subscription/ received");

    final var marshaller = DE4ACoreMarshaller.dtResponseEventSubscriptionMarshaller ();

    final ResponseEventSubscriptionType responseObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                                marshaller,
                                                                                                new ConnectorException ().withModule (EExternalModule.CONNECTOR_DT));
    if (responseObj.hasNoResponseEventSubscriptionItemEntries ())
      throw new IllegalStateException ("Provided payload has no ResponseEventSubscriptionItem entries");

    // Check if there are multiple evidence responses
    final IDocumentTypeIdentifier docTypeID;
    if (responseObj.getResponseEventSubscriptionItemCount () > 1)
    {
      docTypeID = DcngConfig.getIdentifierFactory ().createDocumentTypeIdentifier (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVENT_CATALOGUE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = DcngConfig.getIdentifierFactory ().parseDocumentTypeIdentifier (responseObj.getResponseEventSubscriptionItemAtIndex (0).getCanonicalEventCatalogUri ());
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (responseObj.getDataOwner ().getAgentUrn (),
                                                        responseObj.getDataEvaluator ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_RESPONSE);

    responseObj.getResponseEventSubscriptionItemAtIndex(0).getError();
    final String responseMetadata = MessageUtils.getEventSubscriptionResponseMetadata(responseObj.getResponseEventSubscriptionItem());
    this.apiManager.processIncomingMessage (ELogMessage.LOG_RES_SUBSC_DO_DT,
    		responseObj, messageDTO, marshaller, responseObj.getRequestId(), responseMetadata);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }
}
