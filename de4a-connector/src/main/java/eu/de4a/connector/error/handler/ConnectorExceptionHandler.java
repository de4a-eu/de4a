package eu.de4a.connector.error.handler;

import java.util.Locale;

import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.utils.MessageUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.DE4AResponseDocumentHelper;
import eu.de4a.iem.core.jaxb.common.ResponseErrorType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ConnectorExceptionHandler {
    
    public static String getMessage(ConnectorException ex) {
        try {
            String key = ex.getMessage();
            ex.getArgs().add(0, ex.getModule().getLabel());
            return  MessageUtils.valueOf(key, ex.getArgs().toArray());
        } catch (NoSuchMessageException name) {
            log.error("Bundle key {} is missing for locale {}", ex.getMessage(), Locale.getDefault());
            return ex.getMessage();
        }
    }
    
    public static Object getResponseError(ConnectorException ex, boolean returnBytes) {
        final ResponseErrorType response;
        if(ex != null) {
            response = DE4AResponseDocumentHelper.createResponseError(false);
            String msg = getMessage(ex);
            response.addError(DE4AResponseDocumentHelper.createError(ex.buildCode(), msg));
        } else {
            response = DE4AResponseDocumentHelper.createResponseError(true);
        }
        if(returnBytes) {
            return DE4ACoreMarshaller.defResponseMessage().getAsBytes(response);
        }
        return response;
    }
    
    public static byte[] getGenericResponseError(Exception ex) {
        ResponseErrorType responseError = DE4AResponseDocumentHelper.createResponseError(false);
        String msg = ex.getMessage() == null ? "Internal Connector Error" : ex.getMessage();
        responseError.addError(DE4AResponseDocumentHelper.createError("99999", msg));
        return DE4ACoreMarshaller.defResponseMessage().getAsBytes(responseError);
    }
}
