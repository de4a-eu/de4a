package eu.de4a.connector.error.handler;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseExtractEvidenceUSIException;
import eu.de4a.connector.error.exceptions.ResponseForwardEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceUSIDTException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceUSIException;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;

public class ResponseErrorExceptionHandler extends ConnectorExceptionHandler {

    @Override
    public Object getResponseError(ConnectorException ex, boolean returnBytes) {
        ResponseErrorType response = buildResponse(ex);
        if(returnBytes) {
            if(ex instanceof ResponseExtractEvidenceUSIException) {
                return DE4AMarshaller.doUsiResponseMarshaller().getAsBytes(response);
            } else if(ex instanceof ResponseForwardEvidenceException) {
                return DE4AMarshaller.deUsiResponseMarshaller().getAsBytes(response);
            } else if(ex instanceof ResponseTransferEvidenceUSIException) {
                return DE4AMarshaller.drUsiResponseMarshaller().getAsBytes(response);
            } else if(ex instanceof ResponseTransferEvidenceUSIDTException) {
                return DE4AMarshaller.dtUsiResponseMarshaller().getAsBytes(response);
            }
        }
        return response;
    }
    
    public ResponseErrorType buildResponse(ConnectorException ex) {
        ResponseErrorType response = DE4AResponseDocumentHelper.createResponseError(false);
        ErrorListType errorList = new ErrorListType();
        String msg = getMessage(ex);
        errorList.addError(DE4AResponseDocumentHelper.createError(ex.buildCode(), msg));
        response.setErrorList(errorList);
        return response;
    }

}
