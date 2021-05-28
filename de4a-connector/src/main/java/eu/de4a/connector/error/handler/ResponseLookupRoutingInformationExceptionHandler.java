package eu.de4a.connector.error.handler;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;

public class ResponseLookupRoutingInformationExceptionHandler  extends ConnectorExceptionHandler {
    
	@Override
	public Object getResponseError(ConnectorException ex, boolean returnBytes) {        
        ResponseLookupRoutingInformationType responseLookup = buildResponse(ex);
        if(returnBytes) {
            return DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsBytes(responseLookup);
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
