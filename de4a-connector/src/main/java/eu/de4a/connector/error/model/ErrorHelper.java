package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;
import eu.de4a.kafkaclient.model.EExternalModule;
import eu.de4a.kafkaclient.model.ELogMessageLevel;

import com.helger.commons.annotation.Nonempty;

public class ErrorHelper
{
  private ErrorHelper ()
  {}

  @Nonnull
  @Nonempty
  public static String createCode (@Nonnull final EExternalModule eExtMod,
		  						   @Nonnull final ELogMessageLevel level,
                                   @Nonnull final EFamilyErrorType eFamily)
  {
    return eExtMod.getID () + level.getCode() + eFamily.getID ();
  }
}
