package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

public enum ELogMessageType
{
  SERVICES ("Services", "01"),
  CLIENT ("Client", "02"),
  AS4 ("AS4", "03"),
  ERROR ("Error", "04");

  private final String name;
  private final String code;

  ELogMessageType (@Nonnull @Nonempty final String name, @Nonnull @Nonempty final String code)
  {
    this.name = name;
    this.code = code;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return this.name;
  }

  @Nonnull
  @Nonempty
  public String getCode ()
  {
    return this.code;
  }
}
