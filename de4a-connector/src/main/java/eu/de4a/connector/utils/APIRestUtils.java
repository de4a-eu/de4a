/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Austrian Federal Computing Center (BRZ)
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.de4a.connector.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.helger.commons.collection.ArrayHelper;
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
      aPost.setEntity (new ByteArrayEntity (request, ContentType.APPLICATION_XML));
      final byte [] aResult = aHCM.execute (aPost, new ResponseHandlerByteArray ());
      if (aResult == null || aResult.length == 0)
      {
        LOGGER.warn ("HTTP POST to '" + url + "' - received an empty response");
        return ResponseEntity.status (HttpStatus.NO_CONTENT).body (ArrayHelper.EMPTY_BYTE_ARRAY);
      }
      LOGGER.info ("Received HTTP response from '" + url + "' with " + aResult.length + " bytes");
      return ResponseEntity.ok (aResult);
    }
    catch (final ExtendedHttpResponseException ex)
    {
      LOGGER.error ("There was an error on HTTP client POST connection to '" + url + "'", ex);

      final ConnectorException exception = aBaseEx.withLayer (ELayerError.COMMUNICATIONS)
                                                  .withFamily (EFamilyErrorType.ERROR_RESPONSE)
                                                  .withMessageArg (ex.getMessage ());

      KafkaClientWrapper.sendError (EFamilyErrorType.ERROR_RESPONSE,
                                    exception.getModule (),
                                    url,
                                    exception.getMessage ());

      return new ResponseEntity <> (ConnectorExceptionHandler.getResponseErrorObjectBytes (exception),
                                    HttpStatus.resolve (ex.getStatusCode ()));
    }
    catch (final IOException ex)
    {
      LOGGER.error ("There was an error on HTTP client POST connection", ex);

      final ConnectorException exception = aBaseEx.withLayer (ELayerError.COMMUNICATIONS)
                                                  .withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                                  .withMessageArg (ex.getMessage ());

      KafkaClientWrapper.sendError (EFamilyErrorType.CONNECTION_ERROR,
                                    exception.getModule (),
                                    url,
                                    exception.getMessage ());

      return new ResponseEntity <> (ConnectorExceptionHandler.getResponseErrorObjectBytes (exception),
                                    HttpStatus.BAD_REQUEST);
    }
  }

  @Nonnull
  public static <T> T conversionBytesWithCatching (final InputStream obj,
                                                   final DE4ACoreMarshaller <T> marshaller,
                                                   final ConnectorException ex)
  {
    final ConnectorException baseEx = ex.withFamily (EFamilyErrorType.CONVERSION_ERROR)
                                        .withLayer (ELayerError.INTERNAL_FAILURE);
    marshaller.readExceptionCallbacks ().set (e -> {
      if (e.getLinkedException () != null)
      {
        baseEx.withMessageArg (e.getLinkedException ().getMessage ());
        KafkaClientWrapper.sendError (EFamilyErrorType.CONVERSION_ERROR,
                                      ex.getModule (),
                                      e.getLinkedException ().getMessage ());
      }
    });

    final T returnObj;
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
