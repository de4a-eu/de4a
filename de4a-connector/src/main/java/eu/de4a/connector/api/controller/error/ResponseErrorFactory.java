package eu.de4a.connector.api.controller.error;

import java.util.HashMap;
import java.util.Map;

public class ResponseErrorFactory {
	private static Map<Class<?>,ConnectorExceptionHandler> handlers;
	static{
		handlers=new HashMap<Class<?>,ConnectorExceptionHandler>();
		handlers.put(ResponseLookupRoutingInformationException.class , new ResponseLookupRoutingInformationExceptionHandler());
		handlers.put(SMPLookingMetadataInformationException.class , new SMPLookingMetadataInformationExceptionHandler());
	}
	public static String getResponseError(ConnectorException ex) {
		return handlers.get(ex.getClass()).getResponseError(ex);
	}
	public static String getGenericResponseError(Exception ex) {
		return GenericExceptionHandler.getResponseError(ex);
	}
	
}
