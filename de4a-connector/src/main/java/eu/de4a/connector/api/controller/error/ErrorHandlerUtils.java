package eu.de4a.connector.api.controller.error;

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

import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;

public class ErrorHandlerUtils {
    private static final Logger logger = LoggerFactory.getLogger (ErrorHandlerUtils.class);
    
    ErrorHandlerUtils() {
        //empty constructor
    }
    
    public static ResponseEntity<String> checkResponse(ResponseEntity<String> response, ExternalModuleError module, 
            ConnectorException ex, RequestTransferEvidenceUSIIMDRType request, boolean throwException) {
        if(response == null || !HttpStatus.ACCEPTED.equals(response.getStatusCode()) 
                || ObjectUtils.isEmpty(response.getBody())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Failed or empty response received - {}", response);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.ERROR_RESPONSE) 
                .withModule(module)
                .withMessageArg(MessageFormat.format("Failed or empty response received {0}", response))
                .withRequest(request)
                .withHttpStatus(HttpStatus.OK);
            if(throwException) {
                throw exception;
            }
            return new ResponseEntity<>((String) ResponseErrorFactory.getHandlerFromClassException(
                    ex.getClass()).getResponseError(exception, false), HttpStatus.OK);
        }
        return response;
    }
    public static String getRestObjectWithCatching(String url, ExternalModuleError module, boolean throwException,
            ConnectorException ex, RestTemplate restTemplate, RequestTransferEvidenceUSIIMDRType request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML); 
        try {
            return restTemplate.getForObject(url, String.class);
        } catch(RestClientException e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on HTTP client GET connection", e);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                    .withFamily(FamilyErrorType.CONNECTION_ERROR) 
                    .withModule(module)
                    .withMessageArg(e.getMessage())
                    .withRequest(request)
                    .withHttpStatus(HttpStatus.OK);
            if(throwException) {
                throw exception;
            }
            return (String) ResponseErrorFactory.getHandlerFromClassException(ex.getClass()).getResponseError(exception, true);
        }
    }
    public static String postRestObjectWithCatching(String url, String request, ExternalModuleError module, boolean throwException,
            ConnectorException ex, RestTemplate restTemplate, RequestTransferEvidenceUSIIMDRType requestObj) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(url,
                    new HttpEntity<>(request, headers), String.class);
        } catch(RestClientException e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on HTTP client POST connection", e);
            }
            ConnectorException exception = ex.withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.CONNECTION_ERROR) 
                .withModule(module)
                .withMessageArg(e.getMessage())
                .withRequest(requestObj)
                .withHttpStatus(HttpStatus.OK);
            if(throwException) {
                throw exception;
            }
            return (String) ResponseErrorFactory.getHandlerFromClassException(ex.getClass()).getResponseError(exception, true);
        }
        response = checkResponse(response, module, ex, requestObj, throwException);
        return response.getBody();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Object conversionStrWithCatching(DE4AMarshaller<T> marshaller, Object obj, 
            boolean objToStr, boolean throwException, LayerError layer, ExternalModuleError module, 
            ConnectorException ex, RequestTransferEvidenceUSIIMDRType request) {
        Object returnObj = null;
        ConnectorException exception = ex.withLayer(layer)
            .withFamily(FamilyErrorType.CONVERSION_ERROR) 
            .withModule(module)
            .withRequest(request)
            .withHttpStatus(HttpStatus.OK);
        try {
            if(objToStr) {
                returnObj = marshaller.getAsString((T) obj);
            } else {
                returnObj = marshaller.read((String) obj);
            }
        } catch(Exception e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on marshal conversion of String object", e);
            }
            
            if(throwException) {
                throw exception.withMessageArg(e.getMessage());
            }
            return ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception.withMessageArg(e.getMessage()), objToStr);
        }
        if(returnObj == null) {
            if(throwException) {
                throw exception.withMessageArg("There was an error on marshal conversion of String object");
            }
            return ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception.withMessageArg("There was an error on marshal conversion of String object"), objToStr);
        }
        return returnObj;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Object conversionDocWithCatching(DE4AMarshaller<T> marshaller, Object obj, boolean objToDoc, 
            boolean throwException, LayerError layer, ExternalModuleError module, ConnectorException ex, 
            RequestTransferEvidenceUSIIMDRType request) {
        Object returnObj = null;
        ConnectorException exception = ex.withLayer(layer)
                .withFamily(FamilyErrorType.CONVERSION_ERROR) 
                .withModule(module)
                .withRequest(request)
                .withHttpStatus(HttpStatus.OK);
        try {
            if(objToDoc) {
                returnObj = marshaller.getAsDocument((T) obj);
            } else {
                returnObj = marshaller.read((Document) obj);
            }
        } catch(Exception e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on marshal conversion of Document object", e);
            }            
            if(throwException) {
                throw exception.withMessageArg(e.getMessage());
            }
            return ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception.withMessageArg(e.getMessage()), false);
        }
        if(returnObj == null) {
            if(throwException) {
                throw exception.withMessageArg("There was an error on marshal conversion of String object");
            }
            return ResponseErrorFactory.getHandlerFromClassException(ex.getClass())
                    .getResponseError(exception.withMessageArg("There was an error on marshal conversion of String object"), false);
        }
        return returnObj;
    }

}
