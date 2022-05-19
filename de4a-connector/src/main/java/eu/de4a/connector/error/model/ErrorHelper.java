package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

public class ErrorHelper
{
  private ErrorHelper ()
  {}

  @Nonnull
  @Nonempty
  public static String createCode (@Nonnull final ELayerError eLayer,
                                   @Nonnull final EExternalModuleError eExtMod,
                                   @Nonnull final EFamilyErrorType eFamily)
  {
    return eLayer.getID () + eExtMod.getID () + eFamily.getID ();
  }
}
