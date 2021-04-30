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
    
    public static boolean checkResponse(ResponseEntity<String> response, ExternalModuleError module, 
            ConnectorException ex, RequestTransferEvidenceUSIIMDRType request) {
        if(response == null || !HttpStatus.ACCEPTED.equals(response.getStatusCode()) 
                || ObjectUtils.isEmpty(response.getBody())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Failed or empty response received - {}", response);
            }
            throw ex.withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.ERROR_RESPONSE) 
                .withModule(module)
                .withMessageArg(MessageFormat.format("Failed or empty response received {0}", response))
                .withRequest(request)
                .withHttpStatus(HttpStatus.OK);
        }
        return true;
    }
    public static String getRestObjectWithCatching(String url, ExternalModuleError module, 
            ConnectorException ex, RestTemplate restTemplate, RequestTransferEvidenceUSIIMDRType request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML); 
        try {
            return restTemplate.getForObject(url, String.class);
        } catch(RestClientException e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on HTTP client GET connection", e);
            }
            throw ex.withLayer(LayerError.COMMUNICATIONS)
            .withFamily(FamilyErrorType.CONNECTION_ERROR) 
            .withModule(module)
            .withMessageArg(e.getMessage())
            .withRequest(request)
            .withHttpStatus(HttpStatus.OK);
        } 
    }
    public static String postRestObjectWithCatching(String url, String request, ExternalModuleError module, 
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
            throw ex.withLayer(LayerError.COMMUNICATIONS)
            .withFamily(FamilyErrorType.CONNECTION_ERROR) 
            .withModule(module)
            .withMessageArg(e.getMessage())
            .withRequest(requestObj)
            .withHttpStatus(HttpStatus.OK);
        }
        checkResponse(response, module, ex, requestObj);
        return response.getBody();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Object conversionStrWithCatching(DE4AMarshaller<T> marshaller, Object obj, boolean objToStr, 
            LayerError layer, ExternalModuleError module, ConnectorException ex, RequestTransferEvidenceUSIIMDRType request) {
        try {
            if(objToStr) {
                return marshaller.getAsString((T) obj);
            }
            return marshaller.read((String) obj);
        } catch(Exception e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on marshal conversion of String object", e);
            }
            throw ex.withLayer(layer)
            .withFamily(FamilyErrorType.CONVERSION_ERROR) 
            .withModule(module)
            .withMessageArg(e.getMessage())
            .withRequest(request)
            .withHttpStatus(HttpStatus.OK);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Object conversionDocWithCatching(DE4AMarshaller<T> marshaller, Object obj, boolean objToDoc, 
            LayerError layer, ExternalModuleError module, ConnectorException ex, RequestTransferEvidenceUSIIMDRType request) {
        try {
            if(objToDoc) {
                return marshaller.getAsDocument((T) obj);
            }
            return marshaller.read((Document) obj);
        } catch(Exception e) {
            if(logger.isDebugEnabled()) {
                logger.debug("There was an error on marshal conversion of Document object", e);
            }
            throw ex.withLayer(layer)
                .withFamily(FamilyErrorType.CONVERSION_ERROR) 
                .withModule(module)
                .withMessageArg(e.getMessage())
                .withRequest(request)
                .withHttpStatus(HttpStatus.OK);
        }
    }

}
