package eu.de4a.connector.api.controller;

import java.io.InputStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.de4a.connector.api.ResponseAPI;
import eu.de4a.connector.api.manager.APIManager;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/response")
@Validated
@Log4j2
public class ResponseController implements ResponseAPI {

	@Autowired
    private APIManager apiManager;
	
	
	@PostMapping(value = "/usi/redirectUser/", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> redirectUserUsi(@Valid InputStream request) {
        log.debug("Request to API /redirectUser/ received");

        var marshaller = DE4ACoreMarshaller.dtUSIRedirectUserMarshaller();

        RedirectUserType redirectUserMsg = (RedirectUserType) APIRestUtils
                .conversionBytesWithCatching(marshaller, request, false, true, 
                        new ConnectorException().withModule(ExternalModuleError.CONNECTOR_DT));
        
        AS4MessageDTO messageDTO = new AS4MessageDTO(redirectUserMsg.getDataEvaluator().getAgentUrn(), 
                redirectUserMsg.getDataOwner().getAgentUrn())
                    .withContentID(redirectUserMsg.getClass().getSimpleName())
                    .withDocTypeID(redirectUserMsg.getCanonicalEvidenceTypeId())
                    .withProcessID(DE4AConstants.MESSAGE_TYPE_RESPONSE);
        
        boolean isSent = this.apiManager.processIncomingMessage(redirectUserMsg, messageDTO, redirectUserMsg.getRequestId(),
                "Redirect User", marshaller);
        
        return ResponseEntity.status((isSent ? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR))
                .body((byte[]) ConnectorExceptionHandler.getResponseError(null, isSent));
    }

    @PostMapping(value = "/evidence/", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
    @Override
    public ResponseEntity<byte[]> responseEvidence(@Valid InputStream request) {
        log.debug("Request to API /evidence/ received");

        var marshaller = DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller(IDE4ACanonicalEvidenceType.NONE);

        ResponseExtractMultiEvidenceType responseObj = (ResponseExtractMultiEvidenceType) APIRestUtils
                .conversionBytesWithCatching(marshaller, request, false, true, 
                        new ConnectorException().withModule(ExternalModuleError.CONNECTOR_DT));
        
        // Check if there are multiple evidence responses
        final String docTypeID;
        if(responseObj.getResponseExtractEvidenceItemCount() > 1) {
            docTypeID = DE4AConstants.EVIDENCE_SCHEME + "::" +
                    DE4AConstants.MULTI_ITEM_TYPE;
        } else {
            docTypeID = responseObj.getResponseExtractEvidenceItemAtIndex(0)
                    .getCanonicalEvidenceTypeId(); 
        }
        
        AS4MessageDTO messageDTO = new AS4MessageDTO(responseObj.getDataOwner().getAgentUrn(), 
                responseObj.getDataEvaluator().getAgentUrn())
                    .withContentID(responseObj.getClass().getSimpleName())
                    .withDocTypeID(docTypeID)
                    .withProcessID(DE4AConstants.MESSAGE_TYPE_RESPONSE);
        
        boolean isSent = this.apiManager.processIncomingMessage(responseObj, messageDTO, responseObj.getRequestId(),
                "Response Evidence", marshaller);
        
        return ResponseEntity.status((isSent ? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR))
                .body((byte[]) ConnectorExceptionHandler.getResponseError(null, isSent));
    }

    @PostMapping(value = "/subscription/", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
    @Override
    public ResponseEntity<byte[]> responseEventSubscription(@Valid InputStream request) {
        log.debug("Request to API /subscription/ received");

        var marshaller = DE4ACoreMarshaller.dtResponseEventSubscriptionMarshaller();

        ResponseEventSubscriptionType responseObj = (ResponseEventSubscriptionType) APIRestUtils
                .conversionBytesWithCatching(marshaller, request, false, true, 
                        new ConnectorException().withModule(ExternalModuleError.CONNECTOR_DT));
        
        // Check if there are multiple evidence responses
        final String docTypeID;
        if(responseObj.getResponseEventSubscriptionItemCount() > 1) {
            docTypeID = DE4AConstants.EVENT_CATALOGUE_SCHEME + "::" +
                    DE4AConstants.MULTI_ITEM_TYPE;
        } else {
            docTypeID = responseObj.getResponseEventSubscriptionItemAtIndex(0)
                    .getCanonicalEventCatalogUri();
        }
        
        AS4MessageDTO messageDTO = new AS4MessageDTO(responseObj.getDataEvaluator().getAgentUrn(), 
                responseObj.getDataOwner().getAgentUrn())
                    .withContentID(responseObj.getClass().getSimpleName())
                    .withDocTypeID(docTypeID)
                    .withProcessID(DE4AConstants.MESSAGE_TYPE_RESPONSE);
        
        boolean isSent = this.apiManager.processIncomingMessage(responseObj, messageDTO, responseObj.getRequestId(),
                "Response Evidence", marshaller);
        
        return ResponseEntity.status((isSent ? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR))
                .body((byte[]) ConnectorExceptionHandler.getResponseError(null, isSent));
    }
}
