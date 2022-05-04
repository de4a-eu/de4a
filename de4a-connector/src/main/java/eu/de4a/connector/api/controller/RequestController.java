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
import eu.de4a.connector.api.RequestAPI;
import eu.de4a.connector.api.manager.APIManager;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceLUType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;

@Controller
@RequestMapping("/request")
@Validated
public class RequestController implements RequestAPI {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

	@Autowired
	private APIManager apiManager;


	@PostMapping(value = "/usi/", produces = MediaType.APPLICATION_XML_VALUE,
            consumes = MediaType.APPLICATION_XML_VALUE)
    @Override
    public ResponseEntity<byte[]> requestEvidenceUSI(@Valid final InputStream request) {
        LOGGER.debug("Request to API /usi/ received");

        final var marshaller = DE4ACoreMarshaller.drRequestTransferEvidenceUSIMarshaller();

        // Unmarshalling and schema validation
        final RequestExtractMultiEvidenceUSIType requestObj = (RequestExtractMultiEvidenceUSIType)
                APIRestUtils.conversionBytesWithCatching(marshaller,request, false, true,
                        new ConnectorException().withModule(EExternalModuleError.CONNECTOR_DR));

        // Check if there are multiple evidence request
        final String docTypeID;
        if(requestObj.getRequestEvidenceUSIItemCount() > 1) {
            docTypeID = DE4AConstants.EVIDENCE_SCHEME + "::" +
                    DE4AConstants.MULTI_ITEM_TYPE;
        } else {
            docTypeID = requestObj.getRequestEvidenceUSIItemAtIndex(0)
                    .getCanonicalEvidenceTypeId();
        }

        final AS4MessageDTO messageDTO = new AS4MessageDTO(requestObj.getDataEvaluator().getAgentUrn(),
                requestObj.getDataOwner().getAgentUrn())
                    .withContentID(requestObj.getClass().getSimpleName())
                    .withDocTypeID(docTypeID)
                    .withProcessID(DE4AConstants.MESSAGE_TYPE_REQUEST);

        final boolean isSent = this.apiManager.processIncomingMessage(requestObj, messageDTO,
                docTypeID, "USI Request", marshaller);

        //Default response - at this point must be successful but doesn't hurt double check
        return ResponseEntity.status((isSent ? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR))
                .body((byte[]) ConnectorExceptionHandler.getResponseError(null, isSent));
    }

    @PostMapping(value = "/im/", produces = MediaType.APPLICATION_XML_VALUE,
            consumes = MediaType.APPLICATION_XML_VALUE)
    @Override
    public ResponseEntity<byte[]> requestEvidenceIM(@Valid final InputStream request) {
        LOGGER.debug("Request to API /im/ received");

        final var marshaller = DE4ACoreMarshaller.drRequestTransferEvidenceIMMarshaller();

        // Unmarshalling and schema validation
        final RequestExtractMultiEvidenceIMType requestObj = (RequestExtractMultiEvidenceIMType)
                APIRestUtils.conversionBytesWithCatching(marshaller,request, false, true,
                        new ConnectorException().withModule(EExternalModuleError.CONNECTOR_DR));

        // Check if there are multiple evidence request
        final String docTypeID;
        if(requestObj.getRequestEvidenceIMItemCount() > 1) {
            docTypeID = DE4AConstants.EVIDENCE_SCHEME + "::" +
                    DE4AConstants.MULTI_ITEM_TYPE;
        } else {
            docTypeID = requestObj.getRequestEvidenceIMItemAtIndex(0)
                    .getCanonicalEvidenceTypeId();
        }

        final AS4MessageDTO messageDTO = new AS4MessageDTO(requestObj.getDataEvaluator().getAgentUrn(),
                requestObj.getDataOwner().getAgentUrn())
                    .withContentID(requestObj.getClass().getSimpleName())
                    .withDocTypeID(docTypeID)
                    .withProcessID(DE4AConstants.MESSAGE_TYPE_REQUEST);

        final boolean isSent = this.apiManager.processIncomingMessage(requestObj, messageDTO,
                docTypeID, "IM Request", marshaller);

        //Default response - at this point must be successful but doesn't hurt double check
        return ResponseEntity.status((isSent ? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR))
                .body((byte[]) ConnectorExceptionHandler.getResponseError(null, isSent));
    }

    @PostMapping(value = "/lu/", produces = MediaType.APPLICATION_XML_VALUE,
            consumes = MediaType.APPLICATION_XML_VALUE)
    @Override
    public ResponseEntity<byte[]> requestEvidenceLU(@Valid final InputStream request) {
        LOGGER.debug("Request to API /lu/ received");

        final var marshaller = DE4ACoreMarshaller.drRequestTransferEvidenceIMMarshaller();

        // Unmarshalling and schema validation
        final RequestExtractMultiEvidenceLUType requestObj = (RequestExtractMultiEvidenceLUType)
                APIRestUtils.conversionBytesWithCatching(marshaller,request, false, true,
                        new ConnectorException().withModule(EExternalModuleError.CONNECTOR_DR));

        // Check if there are multiple evidence request
        final String docTypeID;
        if(requestObj.getRequestEvidenceLUItemCount() > 1) {
            docTypeID = DE4AConstants.EVIDENCE_SCHEME + "::" +
                    DE4AConstants.MULTI_ITEM_TYPE;
        } else {
            docTypeID = requestObj.getRequestEvidenceLUItemAtIndex(0)
                    .getCanonicalEvidenceTypeId();
        }

        final AS4MessageDTO messageDTO = new AS4MessageDTO(requestObj.getDataEvaluator().getAgentUrn(),
                requestObj.getDataOwner().getAgentUrn())
                    .withContentID(requestObj.getClass().getSimpleName())
                    .withDocTypeID(docTypeID)
                    .withProcessID(DE4AConstants.MESSAGE_TYPE_REQUEST);

        final boolean isSent = this.apiManager.processIncomingMessage(requestObj, messageDTO,
                docTypeID, "LU Request", marshaller);

        //Default response - at this point must be successful but doesn't hurt double check
        return ResponseEntity.status((isSent ? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR))
                .body((byte[]) ConnectorExceptionHandler.getResponseError(null, isSent));
    }

    @PostMapping(value = "/subscription/", produces = MediaType.APPLICATION_XML_VALUE,
            consumes = MediaType.APPLICATION_XML_VALUE)
    @Override
    public ResponseEntity<byte[]> requestEventSubscription(@Valid final InputStream request) {
        LOGGER.debug("Request to API /subscription/ received");

        final var marshaller = DE4ACoreMarshaller.drRequestEventSubscriptionMarshaller();

        // Unmarshalling and schema validation
        final RequestEventSubscriptionType requestObj = (RequestEventSubscriptionType)
                APIRestUtils.conversionBytesWithCatching(marshaller,request, false, true,
                        new ConnectorException().withModule(EExternalModuleError.CONNECTOR_DR));

        // Check if there are multiple evidence request
        final String docTypeID;
        if(requestObj.getEventSubscripRequestItemCount() > 1) {
            docTypeID = DE4AConstants.EVENT_CATALOGUE_SCHEME + "::" +
                    DE4AConstants.MULTI_ITEM_TYPE;
        } else {
            docTypeID = requestObj.getEventSubscripRequestItemAtIndex(0)
                    .getCanonicalEventCatalogUri();
        }

        final AS4MessageDTO messageDTO = new AS4MessageDTO(requestObj.getDataEvaluator().getAgentUrn(),
                requestObj.getDataOwner().getAgentUrn())
                    .withContentID(requestObj.getClass().getSimpleName())
                    .withDocTypeID(docTypeID)
                    .withProcessID(DE4AConstants.MESSAGE_TYPE_REQUEST);

        final boolean isSent = this.apiManager.processIncomingMessage(requestObj, messageDTO,
                docTypeID, "Subscription Request", marshaller);

        //Default response - at this point must be successful but doesn't hurt double check
        return ResponseEntity.status((isSent ? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR))
                .body((byte[]) ConnectorExceptionHandler.getResponseError(null, isSent));
    }
}
