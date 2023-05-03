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
package eu.de4a.connector.error.handler;

import java.util.Locale;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import com.helger.commons.string.StringHelper;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.utils.MessageUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.DE4AResponseDocumentHelper;
import eu.de4a.iem.core.jaxb.common.ResponseErrorType;

@Component
public class ConnectorExceptionHandler
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConnectorExceptionHandler.class);

  private ConnectorExceptionHandler ()
  {}

  @Nonnull
  public static byte [] getSuccessResponseBytes ()
  {
    return DE4ACoreMarshaller.defResponseMarshaller ().getAsBytes (DE4AResponseDocumentHelper.createResponseError (true));
  }

  private static String _getMessage (@Nonnull final ConnectorException ex)
  {
    try
    {
      final String key = ex.getMessage ();
      ex.getArgs ().add (0, ex.getModule ().getLabel ());
      return MessageUtils.format (key, ex.getArgs ().toArray ());
    }
    catch (final NoSuchMessageException name)
    {
      LOGGER.error ("[internal] Bundle key '" + ex.getMessage () + "' is missing for locale " + Locale.getDefault ());
      return ex.getMessage ();
    }
  }

  @Nonnull
  public static byte [] getResponseErrorObjectBytes (@Nonnull final ConnectorException ex)
  {
    final ResponseErrorType response = DE4AResponseDocumentHelper.createResponseError (false);
    response.addError (DE4AResponseDocumentHelper.createError (ex.buildCode (), _getMessage (ex)));
    return DE4ACoreMarshaller.defResponseMarshaller ().getAsBytes (response);
  }

  @Nonnull
  public static byte [] getGenericResponseError (@Nonnull final Exception ex)
  {
    final ResponseErrorType responseError = DE4AResponseDocumentHelper.createResponseError (false);
    final String msg = StringHelper.hasNoText (ex.getMessage ()) ? "Internal Connector Error"
                                                                 : "[" + ex.getClass ().getSimpleName () + "] " + ex.getMessage ();
    responseError.addError (DE4AResponseDocumentHelper.createError ("99999", msg));
    return DE4ACoreMarshaller.defResponseMarshaller ().getAsBytes (responseError);
  }
}
