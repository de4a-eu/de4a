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
import eu.de4a.iem.core.CIEM;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.kafkaclient.model.EExternalModule;
import eu.de4a.kafkaclient.model.ELogMessage;

@Controller
@RequestMapping ("/event")
public class EventController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (EventController.class);

  @Autowired
  private APIManager apiManager;

  @PostMapping (value = "/notification", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> eventNotification (@Valid final InputStream request)
  {
    LOGGER.info ("Request to API /event/notification/ received");

    final var marshaller = DE4ACoreMarshaller.dtEventNotificationMarshaller ();

    final EventNotificationType eventObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                     marshaller,
                                                                                     new ConnectorException ().withModule (EExternalModule.CONNECTOR_DT));

    if (eventObj.hasNoEventNotificationItemEntries ())
      throw new IllegalStateException ("Provided payload has no EventNotificationItem entries");

    // Check if there are multiple evidence responses
    final IDocumentTypeIdentifier docTypeID;
    if (eventObj.getEventNotificationItemCount () > 1)
    {
      docTypeID = DcngConfig.getIdentifierFactory ().createDocumentTypeIdentifier (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVENT_CATALOGUE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = DcngConfig.getIdentifierFactory ().parseDocumentTypeIdentifier (eventObj.getEventNotificationItemAtIndex (0).getCanonicalEventCatalogUri ());
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (eventObj.getDataOwner ().getAgentUrn (),
                                                        eventObj.getDataEvaluator ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_NOTIFICATION);

    this.apiManager.processIncomingMessage (ELogMessage.LOG_EVENT_NOTIF_DO_DT, eventObj, messageDTO, marshaller, eventObj.getNotificationId());

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }

}
