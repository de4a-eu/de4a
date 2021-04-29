package eu.de4a.connector.api.controller.error;

import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;

public class GenericExceptionHandler { 
	public static String getResponseError( Exception ex) {  
		ResponseErrorType responseError=DE4AResponseDocumentHelper.createResponseError(false);
		responseError.setErrorList(new ErrorListType());
		String msg=ex.getMessage()==null?"Internal Connector Error":ex.getMessage();
		responseError.getErrorList().addError(DE4AResponseDocumentHelper.createError("99999", msg));
		return DE4AMarshaller.dtUsiResponseMarshaller() .getAsString(responseError);
	}
 
}
