package eu.de4a.connector.error.handler;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.MessagesUtils;

public class ResponseExtractEvidenceExceptionHandler extends ConnectorExceptionHandler {
    
    @Override
    public Object getResponseError(ConnectorException ex, boolean returnBytes) {
        ResponseExtractEvidenceType response = buildResponse(ex);
        if(returnBytes) {
            return DE4AMarshaller.doImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
                    .getAsBytes(response);
        }
        return response;
    }
    
    public ResponseExtractEvidenceType buildResponse(ConnectorException ex) {
        ErrorListType errorList = new ErrorListType();
        String msg = getMessage(ex);
        errorList.addError(DE4AResponseDocumentHelper.createError(ex.buildCode(), msg));
        ResponseExtractEvidenceType response = DE4AResponseDocumentHelper.createResponseExtractEvidence(
                MessagesUtils.transformRequestToOwnerIM((RequestTransferEvidenceUSIIMDRType) ex.getRequest()));
        response.setErrorList(errorList);
        return response;
    }

}
