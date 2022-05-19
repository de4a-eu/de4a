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
    return DE4ACoreMarshaller.defResponseErrorMarshaller ().getAsBytes (DE4AResponseDocumentHelper.createResponseError (true));
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
    return DE4ACoreMarshaller.defResponseErrorMarshaller ().getAsBytes (response);
  }

  @Nonnull
  public static byte [] getGenericResponseError (@Nonnull final Exception ex)
  {
    final ResponseErrorType responseError = DE4AResponseDocumentHelper.createResponseError (false);
    final String msg = StringHelper.hasNoText (ex.getMessage ()) ? "Internal Connector Error"
                                                                 : "[" + ex.getClass ().getSimpleName () + "] " + ex.getMessage ();
    responseError.addError (DE4AResponseDocumentHelper.createError ("99999", msg));
    return DE4ACoreMarshaller.defResponseErrorMarshaller ().getAsBytes (responseError);
  }
}
