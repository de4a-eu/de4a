package eu.de4a.connector.api.controller.error;

import java.text.MessageFormat;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import eu.de4a.iem.xml.de4a.DE4AMarshaller;

public class ErrorHandlerUtils {
    
    ErrorHandlerUtils() {
        //empty constructor
    }
    
    public static boolean checkResponse(ResponseEntity<String> response, ExternalModuleError module, 
            ConnectorException ex) {
        if(response == null || !HttpStatus.ACCEPTED.equals(response.getStatusCode()) 
                || ObjectUtils.isEmpty(response.getBody())) {
            throw ex.withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.ERROR_RESPONSE) 
                .withModule(module)
                .withMessageArg(MessageFormat.format("Failed or empty response received {0}", response))
                .withHttpStatus(HttpStatus.OK);
        }
        return true;
    }
    public static String getRestObjectWithCatching(String url,   ExternalModuleError module, 
            ConnectorException ex, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML); 
        try {
            return restTemplate.getForObject(url, String.class);
        } catch(RestClientException e) { 
            throw ex.withLayer(LayerError.COMMUNICATIONS)
            .withFamily(FamilyErrorType.CONNECTION_ERROR) 
            .withModule(module)
            .withMessageArg(e.getMessage())
            .withHttpStatus(HttpStatus.OK);
        } 
    }
    public static String postRestObjectWithCatching(String url, String request, ExternalModuleError module, 
            ConnectorException ex, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(url,
                    new HttpEntity<>(request, headers), String.class);
        } catch(RestClientException e) { 
            throw ex.withLayer(LayerError.COMMUNICATIONS)
            .withFamily(FamilyErrorType.CONNECTION_ERROR) 
            .withModule(module)
            .withMessageArg(e.getMessage())
            .withHttpStatus(HttpStatus.OK);
        }
        checkResponse(response, module, ex);
        return response.getBody();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Object conversionWithCatching(DE4AMarshaller<T> marshaller, Object obj, boolean objToStr, 
            LayerError layer, ExternalModuleError module, ConnectorException ex) {
        try {
            if(objToStr) {
                return marshaller.getAsString((T) obj);
            }
            return marshaller.read((String) obj);
        } catch(Exception e) { 
            throw ex.withLayer(layer)
            .withFamily(FamilyErrorType.CONVERSION_ERROR) 
            .withModule(module)
            .withMessageArg(e.getMessage())
            .withHttpStatus(HttpStatus.OK);
        }
    }

}
