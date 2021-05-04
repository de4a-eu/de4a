package eu.de4a.connector.api.controller;

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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.model.MessageKeys;
import eu.de4a.connector.error.utils.ResponseErrorFactory;
import eu.de4a.connector.service.spring.MessageUtils;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;

/**
 * Controller for handling BAD_REQUEST type errors for more concise messages
 *
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CustomRestExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildBadRequestError(error);
    }

    @ExceptionHandler(value = { ConnectorException.class })
    protected ResponseEntity<Object> handleConnectorException(ConnectorException ex, WebRequest request) {
        return new ResponseEntity<>(ResponseErrorFactory.getResponseError(ex), new HttpHeaders(), ex.getStatus());
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleExceptionUnknown(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ResponseErrorFactory.getGenericResponseError(ex), new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(ex.getContentType()));
        args.add(mediaTypes.toString());
        String err = new MessageUtils(MessageKeys.ERROR_400_MIMETYPE, args.toArray()).value();
        return buildBadRequestError(err);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error;
        if (ex.getCause() instanceof UnmarshalException) {
            String args[] = { ex.getMessage() };
            error = new MessageUtils(MessageKeys.ERROR_400_UNMARSHALLING, args).value();
        } else {
            error = new MessageUtils(MessageKeys.ERROR_400_ARGS_REQUIRED, null).value();
        }
        return buildBadRequestError(error);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        logger.warn("REST Client request not found service");
        String err = new MessageUtils(MessageKeys.ERROR_404, null).value();
        ResponseErrorType responseError = DE4AResponseDocumentHelper.createResponseError(false);
        responseError.setErrorList(new ErrorListType());
        // TODO same code for not found vs missing parameters?
        String code = LayerError.COMMUNICATIONS.ordinal() + ExternalModuleError.NONE.getId()
                + FamilyErrorType.MISSING_REQUIRED_ARGUMENTS.getID();
        responseError.getErrorList().addError(DE4AResponseDocumentHelper.createError(code, err));
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.NOT_FOUND);

    }

    private ResponseEntity<Object> buildBadRequestError(String err) {
        LOG.warn("REST Client BAD REQUEST-> {}", err);
        ResponseErrorType responseError = DE4AResponseDocumentHelper.createResponseError(false);
        responseError.setErrorList(new ErrorListType());
        String code = LayerError.COMMUNICATIONS.ordinal() + ExternalModuleError.NONE.getId()
                + FamilyErrorType.MISSING_REQUIRED_ARGUMENTS.getID();
        responseError.getErrorList().addError(DE4AResponseDocumentHelper.createError(code, err));
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
