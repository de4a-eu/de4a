package eu.de4a.connector.api.controller.error;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import eu.de4a.connector.api.manager.ApplicationContextProvider; 

public abstract class ConnectorExceptionHandler {
	private static final Logger logger =  LoggerFactory.getLogger (ConnectorExceptionHandler.class);
	protected MessageSource messageSource;
	{
		messageSource = ApplicationContextProvider.getApplicationContext().getBean(MessageSource.class);
	
	}
	protected String getMessage(ConnectorException ex) {
		try {
			String key=ex.getMessage(); 
			ex.getArgs() .add(0,ex.getModule().getLabel()); 
			return messageSource.getMessage(key,ex.getArgs().toArray(),Locale.getDefault()) ;
		}catch(NoSuchMessageException nsme) {
			logger.error("Bundle key {} is missing for locale {}",ex.getMessage(),Locale.getDefault());
			return ex.getMessage();
		}
	}
	public abstract String getResponseError(ConnectorException exception) ;
}
