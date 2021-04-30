package eu.de4a.connector.api.controller.error;

import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;

public class ResponseErrorExceptionHandler extends ConnectorExceptionHandler {

    @Override
    public String getResponseError(ConnectorException ex) {
        ResponseErrorType response = buildResponse(ex);
        return DE4AMarshaller.doUsiResponseMarshaller().getAsString(response);
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
