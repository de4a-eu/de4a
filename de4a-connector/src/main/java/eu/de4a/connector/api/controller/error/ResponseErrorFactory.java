package eu.de4a.connector.api.controller.error;

import java.util.HashMap;
import java.util.Map;

public class ResponseErrorFactory {
	private static Map<Class<?>,ConnectorExceptionHandler> handlers;
	
	ResponseErrorFactory() {
	    //empty constructor
	}
	
	static{
		handlers = new HashMap<>();
		handlers.put(ResponseLookupRoutingInformationException.class , new ResponseLookupRoutingInformationExceptionHandler());
		handlers.put(SMPLookingMetadataInformationException.class , new SMPLookingMetadataInformationExceptionHandler());
		handlers.put(ResponseTransferEvidenceException.class, new ResponseTransferEvidenceExceptionHandler());
        handlers.put(ResponseErrorException.class, new ResponseErrorExceptionHandler());
        handlers.put(ResponseExtractEvidenceException.class, new ResponseExtractEvidenceExceptionHandler());
	}
	public static String getResponseError(ConnectorException ex) {
		return (String) handlers.get(ex.getClass()).getResponseError(ex, true);
	}
	public static String getGenericResponseError(Exception ex) {
		return GenericExceptionHandler.getResponseError(ex);
	}
	public static ConnectorExceptionHandler getHandlerFromClassException(Class<?> classException) {
	    return handlers.get(classException);
	}
	
}
