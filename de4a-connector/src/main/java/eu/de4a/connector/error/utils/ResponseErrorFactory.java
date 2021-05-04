package eu.de4a.connector.error.utils;

import java.util.HashMap;
import java.util.Map;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseErrorException;
import eu.de4a.connector.error.exceptions.ResponseExtractEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseLookupRoutingInformationException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.exceptions.SMPLookingMetadataInformationException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.handler.GenericExceptionHandler;
import eu.de4a.connector.error.handler.ResponseErrorExceptionHandler;
import eu.de4a.connector.error.handler.ResponseExtractEvidenceExceptionHandler;
import eu.de4a.connector.error.handler.ResponseLookupRoutingInformationExceptionHandler;
import eu.de4a.connector.error.handler.ResponseTransferEvidenceExceptionHandler;
import eu.de4a.connector.error.handler.SMPLookingMetadataInformationExceptionHandler;

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
