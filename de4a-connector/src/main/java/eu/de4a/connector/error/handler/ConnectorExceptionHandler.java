package eu.de4a.connector.error.handler;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.service.spring.MessageUtils;

public abstract class ConnectorExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConnectorExceptionHandler.class);

    public String getMessage(ConnectorException ex) {
        try {
            String key = ex.getMessage();
            ex.getArgs().add(0, ex.getModule().getLabel());
            return new MessageUtils(key, ex.getArgs().toArray()).value();
        } catch (NoSuchMessageException name) {
            logger.error("Bundle key {} is missing for locale {}", ex.getMessage(), Locale.getDefault());
            return ex.getMessage();
        }
    }

    public abstract Object getResponseError(ConnectorException exception, boolean returnBytes);
    public abstract Object buildResponse(ConnectorException exception);
}
