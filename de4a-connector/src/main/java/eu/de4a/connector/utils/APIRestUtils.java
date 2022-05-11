package eu.de4a.connector.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.http.CHttpHeader;
import com.helger.commons.mime.CMimeType;
import com.helger.dcng.core.http.DcngHttpClientSettings;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.response.ExtendedHttpResponseException;
import com.helger.httpclient.response.ResponseHandlerByteArray;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.iem.core.DE4ACoreMarshaller;

@Immutable
public final class APIRestUtils
{
  private static final Logger LOGGER = LoggerFactory.getLogger (APIRestUtils.class);

  private APIRestUtils ()
  {}

  @Nonnull
  public static ResponseEntity <byte []> postRestObjectWithCatching (final String url,
                                                                     final byte [] request,
                                                                     final ConnectorException aBaseEx)
  {
    LOGGER.info ("Sending HTTP POST request to '" + url + "' with " + request.length + " bytes");

    // Use global HTTP settings
    try (final HttpClientManager aHCM = HttpClientManager.create (new DcngHttpClientSettings ()))
    {
      final HttpPost aPost = new HttpPost (url);
      aPost.addHeader (CHttpHeader.CONTENT_TYPE, CMimeType.APPLICATION_XML.getAsString ());
      aPost.setEntity (new ByteArrayEntity (request));
      final byte [] aResult = aHCM.execute (aPost, new ResponseHandlerByteArray ());
      if (aResult == null)
      {
        LOGGER.warn ("HTTP POST to '" + url + "' - received an empty response");
        return ResponseEntity.status (HttpStatus.NO_CONTENT).body (ArrayHelper.EMPTY_BYTE_ARRAY);
      }
      return ResponseEntity.ok (aResult);
    }
    catch (final ExtendedHttpResponseException ex)
    {
      LOGGER.error ("There was an error on HTTP client POST connection to '" + url + "'", ex);

      final ConnectorException exception = aBaseEx.withLayer (ELayerError.COMMUNICATIONS)
                                                  .withFamily (EFamilyErrorType.ERROR_RESPONSE)
                                                  .withMessageArg (ex.getMessage ());
      return new ResponseEntity <> (ConnectorExceptionHandler.getResponseErrorObjectBytes (exception),
                                    HttpStatus.resolve (ex.getStatusCode ()));
    }
    catch (final IOException ex)
    {
      LOGGER.error ("There was an error on HTTP client POST connection", ex);

      final ConnectorException exception = aBaseEx.withLayer (ELayerError.COMMUNICATIONS)
                                                  .withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                                  .withMessageArg (ex.getMessage ());
      return new ResponseEntity <> (ConnectorExceptionHandler.getResponseErrorObjectBytes (exception), HttpStatus.BAD_REQUEST);
    }
  }

  @Nonnull
  public static <T> T conversionBytesWithCatching (final InputStream obj,
                                                   final DE4ACoreMarshaller <T> marshaller,
                                                   final ConnectorException ex)
  {
    T returnObj;
    final ConnectorException baseEx = ex.withFamily (EFamilyErrorType.CONVERSION_ERROR).withLayer (ELayerError.INTERNAL_FAILURE);
    marshaller.readExceptionCallbacks ().set (e -> {
      if (e.getLinkedException () != null)
        baseEx.withMessageArg (e.getLinkedException ().getMessage ());
    });

    try
    {
      returnObj = marshaller.read (obj);
    }
    catch (final Exception e)
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Object received is not valid, check the structure", e);
      throw baseEx.withMessageArg (e.getMessage ());
    }
    if (returnObj == null)
      throw baseEx.withMessageArg (ex.getArgs ());

    return returnObj;
  }
}
