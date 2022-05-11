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

import com.helger.dcng.api.DcngIdentifierFactory;
import com.helger.peppolid.CIdentifier;

import eu.de4a.connector.api.manager.APIManager;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.iem.core.CIEM;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;

@Controller
@RequestMapping ("/event")
public class EventController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (EventController.class);

  @Autowired
  private APIManager apiManager;

  @PostMapping (value = "/notification/", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> eventNotification (@Valid final InputStream request)
  {
    LOGGER.info ("Request to API /event/notification/ received");

    final var marshaller = DE4ACoreMarshaller.dtEventNotificationMarshaller ();

    final EventNotificationType eventObj = APIRestUtils.conversionBytesWithCatching (request,
                                                                                     marshaller,
                                                                                     new ConnectorException ().withModule (EExternalModuleError.CONNECTOR_DT));

    if (eventObj.hasNoEventNotificationItemEntries ())
      throw new IllegalStateException ("Provided payload has no EventNotificationItem entries");

    // Check if there are multiple evidence responses
    final String docTypeID;
    if (eventObj.getEventNotificationItemCount () > 1)
    {
      docTypeID = CIdentifier.getURIEncoded (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVENT_CATALOGUE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = eventObj.getEventNotificationItemAtIndex (0).getCanonicalEventCatalogUri ();
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (eventObj.getDataOwner ().getAgentUrn (),
                                                        eventObj.getDataEvaluator ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_NOTIFICATION);

    this.apiManager.processIncomingMessage (eventObj, messageDTO, eventObj.getNotificationId (), "Event Notification", marshaller);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }

}
