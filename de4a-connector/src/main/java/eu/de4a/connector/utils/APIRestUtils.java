package eu.de4a.connector.utils;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class APIRestUtils {
    
    
    public static ResponseEntity<byte[]> checkResponse(ResponseEntity<byte[]> response, ConnectorException ex, 
            boolean throwException) {
        if(response == null || !HttpStatus.OK.equals(response.getStatusCode())) {
            if(log.isDebugEnabled()) {
                log.debug("Failed or empty response received - {}", response);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.ERROR_RESPONSE) 
                .withModule(ex.getModule())
                .withMessageArg(MessageFormat.format("Failed or empty response received {0}", response));
            if(throwException) {
                throw exception;
            }
            return new ResponseEntity<>((byte[]) ConnectorExceptionHandler.getResponseError(exception, true), 
                    HttpStatus.BAD_REQUEST);
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
            if(log.isDebugEnabled()) {
                log.debug("There was an error on HTTP client GET connection", e);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                    .withFamily(FamilyErrorType.CONNECTION_ERROR)
                    .withMessageArg(e.getMessage());
            if(throwException) {
                throw exception;
            }
            return (byte[]) ConnectorExceptionHandler.getResponseError(exception, true);
        }
    }
    
    public static ResponseEntity<byte[]> postRestObjectWithCatching(String url, byte[] request, boolean throwException,
            ConnectorException ex, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8));
        ResponseEntity<byte[]> response;
        try {
            response = restTemplate.postForEntity(url,
                    new HttpEntity<>(request, headers), byte[].class);
        } catch(RestClientException e) {
            if(log.isDebugEnabled()) {
                log.debug("There was an error on HTTP client POST connection", e);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.CONNECTION_ERROR)
                .withMessageArg(e.getMessage());
            if(throwException) {
                throw exception;
            }
            return new ResponseEntity<>((byte[]) ConnectorExceptionHandler.getResponseError(exception, true), 
                    HttpStatus.BAD_REQUEST);
        }
        return checkResponse(response, ex, throwException);
    }
    
    public static <T> Object conversionBytesWithCatching(DE4ACoreMarshaller<T> marshaller, Object obj, 
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
            if(log.isDebugEnabled()) {
                log.debug(errorMsg, e);
            }            
            if(throwException) {
                throw exception.withMessageArg(e.getMessage());
            }
            return ConnectorExceptionHandler
                    .getResponseError(exception.withMessageArg(e.getMessage()), objToBytes);
        }
        if(returnObj == null) {            
            exception.withMessageArg(ex.getArgs());
            if(throwException) {
                throw exception;
            }
            return ConnectorExceptionHandler.getResponseError(exception, objToBytes);
        }
        return returnObj;
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Object getObjectTypeFromObject(DE4ACoreMarshaller<T> marshaller, Object obj, 
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
    public static <T> Object conversionDocWithCatching(DE4ACoreMarshaller<T> marshaller, Object obj, 
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
                returnObj = marshaller.read((Node) obj);
            }
        } catch(Exception e) {
            if(log.isDebugEnabled()) {
                log.debug(errorMsg, e);
            }            
            if(throwException) {
                throw exception.withMessageArg(e.getMessage());
            }
            return ConnectorExceptionHandler
                    .getResponseError(exception.withMessageArg(e.getMessage()), false);
        }
        if(returnObj == null) {
            exception.withMessageArg(ex.getArgs());
            if(throwException) {
                throw exception.withMessageArg(errorMsg);
            }
            return ConnectorExceptionHandler
                    .getResponseError(exception.withMessageArg(errorMsg), false);
        }
        return returnObj;
    }

    public static URIBuilder buildURI(String endpoint, @Nonnull String[] paths, String[] params, 
            String[] values) {
        
        URIBuilder uriBuilder;      
        try {
            uriBuilder = new URIBuilder(endpoint);
        
            if(uriBuilder.toString().endsWith("/") && paths.length > 0) 
                uriBuilder.setPath(uriBuilder.getPath().substring(0, uriBuilder.getPath().length() - 1));
            
            Arrays.asList(paths).stream()
                .forEach(x -> uriBuilder.setPath(Objects.toString(uriBuilder.getPath(), "") + "/" + x));
            
            if(params != null && values != null) {
                if(params.length == values.length) {
                    for(int i = 0; i < params.length; i++) {
                        uriBuilder.addParameter(params[i], values[i]);
                    }
                } else throw new IllegalArgumentException("URIBuilder - Params and values don't matches");
            }
        } catch (NullPointerException | URISyntaxException e) {
            log.error("There was an error creating URI", e);
            return new URIBuilder();
        }        
        return uriBuilder;
    }
}
