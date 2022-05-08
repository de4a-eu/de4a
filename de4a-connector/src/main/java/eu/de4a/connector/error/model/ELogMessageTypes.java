package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;
import com.helger.commons.annotation.Nonempty;

public enum ELogMessageTypes {
  SERVICES("Services", "01"),
  CLIENT("Client", "02"),
  AS4("AS4", "03"),
  ERROR("Error", "04");

  private final String name;
  private final String code;

  ELogMessageTypes(@Nonnull @Nonempty final String name, @Nonnull @Nonempty final String code) {
    this.name = name;
    this.code = code;
  }

  @Nonnull
  @Nonempty
  public String getCode() {
    return this.code;
  }
}
