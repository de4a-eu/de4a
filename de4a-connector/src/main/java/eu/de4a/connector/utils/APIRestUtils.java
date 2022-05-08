package eu.de4a.connector.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.iem.core.DE4ACoreMarshaller;

public class APIRestUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(APIRestUtils.class);

  private APIRestUtils() {}

  private static ResponseEntity<byte[]> checkResponse(final ResponseEntity<byte[]> response, final ConnectorException ex,
      final boolean throwException) {
    if (response == null || !HttpStatus.OK.equals(response.getStatusCode())) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Failed or empty response received - {}", response);
      }
      final ConnectorException exception = ex.withLayer(ELayerError.COMMUNICATIONS)
          .withFamily(EFamilyErrorType.ERROR_RESPONSE).withModule(ex.getModule())
          .withMessageArg(MessageFormat.format("Failed or empty response received {0}", response));
      if (throwException) {
        throw exception;
      }
      return new ResponseEntity<>(ConnectorExceptionHandler.getResponseErrorObjectBytes(exception),
          HttpStatus.BAD_REQUEST);
    }
    return response;
  }

  public static ResponseEntity<byte[]> postRestObjectWithCatching(final String url, final byte[] request,
      final boolean throwException, final ConnectorException ex, final RestTemplate restTemplate) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8));
    ResponseEntity<byte[]> response;
    try {
      response = restTemplate.postForEntity(url, new HttpEntity<>(request, headers), byte[].class);
    } catch (final RestClientException e) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("There was an error on HTTP client POST connection", e);
      }
      final ConnectorException exception = ex.withLayer(ELayerError.COMMUNICATIONS)
          .withFamily(EFamilyErrorType.CONNECTION_ERROR).withMessageArg(e.getMessage());
      if (throwException) {
        throw exception;
      }
      return new ResponseEntity<>(ConnectorExceptionHandler.getResponseErrorObjectBytes(exception),
          HttpStatus.BAD_REQUEST);
    }
    return checkResponse(response, ex, throwException);
  }

  @Nonnull
  public static <T> T conversionBytesWithCatching(final DE4ACoreMarshaller<T> marshaller, final InputStream obj,
      final ConnectorException ex) {
    T returnObj;
    final ConnectorException baseEx =
        ex.withFamily(EFamilyErrorType.CONVERSION_ERROR).withLayer(ELayerError.INTERNAL_FAILURE);
    marshaller.readExceptionCallbacks().set(e -> {
      if (e.getLinkedException() != null)
        ex.withMessageArg(e.getLinkedException().getMessage());
    });

    try {
      returnObj = marshaller.read(obj);
    } catch (final Exception e) {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Object received is not valid, check the structure", e);
      throw baseEx.withMessageArg(e.getMessage());
    }
    if (returnObj == null) {
      baseEx.withMessageArg(ex.getArgs());
      throw baseEx;
    }
    return returnObj;
  }
}
