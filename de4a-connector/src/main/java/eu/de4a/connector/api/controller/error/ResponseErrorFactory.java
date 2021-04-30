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
	}
	public static String getResponseError(ConnectorException ex) {
		return handlers.get(ex.getClass()).getResponseError(ex);
	}
	public static String getGenericResponseError(Exception ex) {
		return GenericExceptionHandler.getResponseError(ex);
	}
	public static ConnectorExceptionHandler getHandlerFromClassException(Class<?> classException) {
	    return handlers.get(classException);
	}
	
}
