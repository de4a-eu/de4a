package eu.de4a.connector.error.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;

public class ErrorHandlerUtils {
    private static final Logger logger = LoggerFactory.getLogger (ErrorHandlerUtils.class);
    
    ErrorHandlerUtils() {
        //empty constructor
    }
    
    public static ResponseEntity<byte[]> checkResponse(ResponseEntity<byte[]> response, ConnectorException ex, 
            boolean throwException) {
        if(response == null || !HttpStatus.OK.equals(response.getStatusCode())
                || ObjectUtils.isEmpty(response.getBody())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Failed or empty response received - {}", response);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.ERROR_RESPONSE) 
                .withModule(ex.getModule())
                .withMessageArg(MessageFormat.format("Failed or empty response received {0}", response))
                .withRequest(ex.getRequest());
            if(throwException) {
                throw exception;
            }
            return new ResponseEntity<>((byte[]) ResponseErrorFactory.getHandlerFromClassException(
                    ex.getClass()).getResponseError(exception, true), HttpStatus.BAD_REQUEST);
        }
        return response;
    }
    
    public static byte[] getRestObjectWithCatching(String url, boolean throwException,
            ConnectorException ex, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8)); 
        try {
            return restTemplate.getForObject(url, byte[].class);
        } catch(RestClientException e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on HTTP client GET connection", e);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                    .withFamily(FamilyErrorType.CONNECTION_ERROR)
                    .withMessageArg(e.getMessage());
            if(throwException) {
                throw exception;
            }
            return (byte[]) ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception, true);
        }
    }
    
    public static byte[] postRestObjectWithCatching(String url, byte[] request, boolean throwException,
            ConnectorException ex, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8));
        ResponseEntity<byte[]> response;
        try {
            response = restTemplate.postForEntity(url,
                    new HttpEntity<>(request, headers), byte[].class);
        } catch(RestClientException e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on HTTP client POST connection", e);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.CONNECTION_ERROR)
                .withMessageArg(e.getMessage());
            if(throwException) {
                throw exception;
            }
            return (byte[]) ResponseErrorFactory.getHandlerFromClassException(
                    ex.getClass()).getResponseError(exception, true);
        }
        response = checkResponse(response, ex, throwException);
        return response.getBody();
    }
    
    public static <T> Object conversionBytesWithCatching(DE4AMarshaller<T> marshaller, Object obj, 
            boolean objToBytes, boolean throwException, ConnectorException ex) {
        Object returnObj = null;
        String errorMsg = "Object received is not valid, check the structure";
        ConnectorException exception = ex.withFamily(FamilyErrorType.CONVERSION_ERROR)
                .withLayer(LayerError.INTERNAL_FAILURE);
        marshaller.readExceptionCallbacks().set(e -> {
            if(!ObjectUtils.isEmpty(e.getLinkedException()))
                ex.withMessageArg(e.getLinkedException().getMessage());
        });
        try {
            returnObj = getObjectTypeFromObject(marshaller, obj, objToBytes);
        } catch(Exception e) {
            if(logger.isDebugEnabled()) {
                logger.debug(errorMsg, e);
            }            
            if(throwException) {
                throw exception.withMessageArg(e.getMessage());
            }
            return ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception.withMessageArg(e.getMessage()), objToBytes);
        }
        if(returnObj == null) {            
            exception.withMessageArg(ex.getArgs());
            if(throwException) {
                throw exception;
            }
            return ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception, objToBytes);
        }
        return returnObj;
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Object getObjectTypeFromObject(DE4AMarshaller<T> marshaller, Object obj, 
            boolean objToBytes) {
        Object retObj;
        if(objToBytes) {
            retObj = marshaller.getAsBytes((T) obj);
        } else {
            if(obj instanceof String) {
                retObj = marshaller.read((String) obj);
            } else if(obj instanceof InputStream) {
                retObj = marshaller.read((InputStream) obj);
            } else if(obj instanceof byte[]) {
                retObj = marshaller.read((byte[]) obj);
            } else if(obj instanceof Document) {
                retObj = marshaller.read((Document) obj);
            } else {
                retObj = null;
            }
        }
        return retObj;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Object conversionDocWithCatching(DE4AMarshaller<T> marshaller, Object obj, 
            boolean objToDoc, boolean throwException, ConnectorException ex) {
        Object returnObj = null;
        String errorMsg = "Object received is not valid, check the structure";
        ConnectorException exception = ex.withLayer(LayerError.INTERNAL_FAILURE)
                .withFamily(FamilyErrorType.CONVERSION_ERROR);
        marshaller.readExceptionCallbacks().set(e -> {
            if(!ObjectUtils.isEmpty(e.getLinkedException()))
                ex.withMessageArg(e.getLinkedException().getMessage());
        });
        try {
            if(objToDoc) {
                returnObj = marshaller.getAsDocument((T) obj);
            } else {
                returnObj = marshaller.read((Document) obj);
            }
        } catch(Exception e) {
            if(logger.isDebugEnabled()) {
                logger.debug(errorMsg, e);
            }            
            if(throwException) {
                throw exception.withMessageArg(e.getMessage());
            }
            return ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception.withMessageArg(e.getMessage()), false);
        }
        if(returnObj == null) {
            exception.withMessageArg(ex.getArgs());
            if(throwException) {
                throw exception.withMessageArg(errorMsg);
            }
            return ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception.withMessageArg(errorMsg), false);
        }
        return returnObj;
    }

}
