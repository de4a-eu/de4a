package eu.de4a.connector.api.controller.error;

import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.MessagesUtils;

public class ResponseTransferEvidenceExceptionHandler extends ConnectorExceptionHandler {
    private RequestTransferEvidenceUSIIMDRType request;
    
    public void setRequest(RequestTransferEvidenceUSIIMDRType request) {
        this.request = request;
    }
    
    @Override
    public String getResponseError(ConnectorException ex) {        
        ErrorListType errorList = new ErrorListType();
        String msg = getMessage(ex);
        errorList.addError(DE4AResponseDocumentHelper.createError(ex.buildCode(), msg));
        ResponseTransferEvidenceType response = MessagesUtils
                .getErrorResponseFromRequest(this.request, errorList);
        return DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
                .getAsString(response);
    }

}
