package eu.de4a.connector.error.handler;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.utils.MessageUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.DE4AResponseDocumentHelper;
import eu.de4a.iem.core.jaxb.common.ResponseErrorType;

@Component
public class ConnectorExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorExceptionHandler.class);

    private ConnectorExceptionHandler (){}

    public static String getMessage(final ConnectorException ex) {
        try {
            final String key = ex.getMessage();
            ex.getArgs().add(0, ex.getModule().getLabel());
            return  MessageUtils.valueOf(key, ex.getArgs().toArray());
        } catch (final NoSuchMessageException name) {
            LOGGER.error("Bundle key {} is missing for locale {}", ex.getMessage(), Locale.getDefault());
            return ex.getMessage();
        }
    }

    public static Object getResponseError(final ConnectorException ex, final boolean returnBytes) {
        final ResponseErrorType response;
        if(ex != null) {
            response = DE4AResponseDocumentHelper.createResponseError(false);
            final String msg = getMessage(ex);
            response.addError(DE4AResponseDocumentHelper.createError(ex.buildCode(), msg));
        } else {
            response = DE4AResponseDocumentHelper.createResponseError(true);
        }
        if(returnBytes) {
            return DE4ACoreMarshaller.defResponseMessage().getAsBytes(response);
        }
        return response;
    }

    public static byte[] getGenericResponseError(final Exception ex) {
        final ResponseErrorType responseError = DE4AResponseDocumentHelper.createResponseError(false);
        final String msg = ex.getMessage() == null ? "Internal Connector Error" : ex.getMessage();
        responseError.addError(DE4AResponseDocumentHelper.createError("99999", msg));
        return DE4ACoreMarshaller.defResponseMessage().getAsBytes(responseError);
    }
}
