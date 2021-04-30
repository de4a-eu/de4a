package eu.de4a.connector.api.controller.error;

import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;

public class ResponseErrorExceptionHandler extends ConnectorExceptionHandler {

    @Override
    public Object getResponseError(ConnectorException ex, boolean returnString) {
        ResponseErrorType response = buildResponse(ex);
        if(returnString) {
            return DE4AMarshaller.doUsiResponseMarshaller().getAsString(response);
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
