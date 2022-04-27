package eu.de4a.connector.error.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.MessagesUtils;

public class ResponseTransferEvidenceExceptionHandler extends ConnectorExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger (ResponseTransferEvidenceExceptionHandler.class);
    
    @Override
    public Object getResponseError(ConnectorException ex, boolean returnBytes) {
        ResponseTransferEvidenceType response = buildResponse(ex);
        if(returnBytes) {
            return DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
                    .getAsBytes(response);
        }
        return response;
    }
    
    public ResponseTransferEvidenceType buildResponse(ConnectorException ex) {
        ErrorListType errorList = new ErrorListType();
        String msg = getMessage(ex);
        errorList.addError(DE4AResponseDocumentHelper.createError(ex.buildCode(), msg));
        
        // Avoid exception later on
        if (ex.getRequest()== null) {
          LOGGER.error ("The provided Exception has no Request contained, so I cannot build a response: "+ ex);          
          return null;
        }
        
        return MessagesUtils.getErrorResponseFromRequest((RequestTransferEvidenceUSIIMDRType) ex.getRequest(), errorList);
    }

}
