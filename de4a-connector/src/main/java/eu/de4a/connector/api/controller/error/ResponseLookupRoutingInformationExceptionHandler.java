package eu.de4a.connector.api.controller.error;

import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;

public class ResponseLookupRoutingInformationExceptionHandler  extends ConnectorExceptionHandler {
    
	@Override
	public Object getResponseError(ConnectorException ex, boolean returnString) {        
        ResponseLookupRoutingInformationType responseLookup = buildResponse(ex);
        if(returnString) {
            return DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(responseLookup);
        }
        return responseLookup;
	}
	
	public ResponseLookupRoutingInformationType buildResponse(ConnectorException ex) {
	    ResponseLookupRoutingInformationType response = new ResponseLookupRoutingInformationType();  
        ErrorListType errorList = new ErrorListType();  
        String msg=getMessage(ex) ;
        errorList.addError( DE4AResponseDocumentHelper.createError(ex.buildCode(),msg));
        response.setErrorList(errorList);
        return response;
	}
 
}
