package eu.de4a.connector.api.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.UnmarshalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.connector.error.model.MessageKeys;
import eu.de4a.connector.utils.MessageUtils;
import eu.de4a.iem.core.DE4AResponseDocumentHelper;
import eu.de4a.iem.core.jaxb.common.ResponseErrorType;

/**
 * Controller for handling type errors for more concise messages
 *
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRestExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String error = ex.getParameterName() + " parameter is missing";
        return buildBadRequestError(error);
    }

    @ExceptionHandler(value = { ConnectorException.class })
    protected ResponseEntity<byte[]> handleConnectorException(final ConnectorException ex, final WebRequest request) {
        LOGGER.error("handleConnectorException", ex);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8));
        return new ResponseEntity<>(ConnectorExceptionHandler.getResponseErrorObjectBytes(ex),
                headers, ex.getStatus());
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<byte[]> handleExceptionUnknown(final Exception ex, final WebRequest request) {
        LOGGER.error("handleExceptionUnknown", ex);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8));
        return new ResponseEntity<>(ConnectorExceptionHandler.getGenericResponseError(ex),
                headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }
        final List<String> args = new ArrayList<>();
        args.add(String.valueOf(ex.getContentType()));
        args.add(mediaTypes.toString());
        final String err = MessageUtils.format(MessageKeys.ERROR_400_MIMETYPE, args.toArray());
        return buildBadRequestError(err);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        String error;
        if (ex.getCause() instanceof UnmarshalException) {
            final String[] args = { ex.getMessage() };
            error = MessageUtils.format(MessageKeys.ERROR_400_UNMARSHALLING, args);
        } else {
            error = MessageUtils.format(MessageKeys.ERROR_400_ARGS_REQUIRED, null);
        }
        return buildBadRequestError(error);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        LOGGER.warn("REST service requested not found");

        headers.setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8));
        final String err = MessageUtils.format(MessageKeys.ERROR_SERVICE_NOT_FOUND,
                new Object[] { ((ServletWebRequest) request).getRequest().getRequestURI() });
        final ResponseErrorType responseError = DE4AResponseDocumentHelper.createResponseError(false);
        final String code = ELayerError.COMMUNICATIONS.getID() + EExternalModuleError.NONE.getId()
                + EFamilyErrorType.SERVICE_NOT_FOUND.getID();
        responseError.addError(DE4AResponseDocumentHelper.createError(code, err));
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.NOT_FOUND);

    }

    private ResponseEntity<Object> buildBadRequestError(final String err) {
        LOGGER.warn("REST Client BAD REQUEST-> {}", err);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8));
        final ResponseErrorType responseError = DE4AResponseDocumentHelper.createResponseError(false);
        final String code = ELayerError.COMMUNICATIONS.getID() + EExternalModuleError.NONE.getId()
                + EFamilyErrorType.MISSING_REQUIRED_ARGUMENTS.getID();
        responseError.addError(DE4AResponseDocumentHelper.createError(code, err));
        return new ResponseEntity<>(responseError, headers, HttpStatus.BAD_REQUEST);
    }
}
