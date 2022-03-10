package eu.de4a.connector.api.controller;

import java.io.InputStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.de4a.connector.api.NotificationAPI;
import eu.de4a.connector.api.manager.APIManager;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/event")
@Log4j2
public class EventController implements NotificationAPI {
    
    @Autowired
    private APIManager apiManager;

    @PostMapping(value = "/notification/", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
    @Override
    public ResponseEntity<byte[]> eventNotification(@Valid InputStream request) {
        log.debug("Request to API /notification/ received");

        var marshaller = DE4ACoreMarshaller.dtEventNotificationMarshaller();

        EventNotificationType eventObj = (EventNotificationType) APIRestUtils
                .conversionBytesWithCatching(marshaller, request, false, true, 
                        new ConnectorException().withModule(ExternalModuleError.CONNECTOR_DT));
        
        // Check if there are multiple evidence responses
        final String docTypeID;
        if(eventObj.getEventNotificationItemCount() > 1) {
            docTypeID = DE4AConstants.EVENT_CATALOGUE_SCHEME + "::" +
                    DE4AConstants.MULTI_ITEM_TYPE;
        } else {
            docTypeID = eventObj.getEventNotificationItemAtIndex(0)
                    .getCanonicalEventCatalogUri();
        }
        
        AS4MessageDTO messageDTO = new AS4MessageDTO(eventObj.getDataOwner().getAgentUrn(), 
                eventObj.getDataEvaluator().getAgentUrn())
                    .withContentID(eventObj.getClass().getSimpleName())
                    .withDocTypeID(docTypeID)
                    .withProcessID(DE4AConstants.MESSAGE_TYPE_NOTIFICATION);
        
        boolean isSent = this.apiManager.processIncomingMessage(eventObj, messageDTO, eventObj.getNotificationId(),
                "Event Notification", marshaller);
        
        return ResponseEntity.status((isSent ? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR))
                .body((byte[]) ConnectorExceptionHandler.getResponseError(null, isSent));
    }
    
}
