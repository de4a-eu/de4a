package eu.de4a.connector.config;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DE4AConstants
{
  // Processes identifiers
  public static final String PROCESS_ID_REQUEST = "request";
  public static final String PROCESS_ID_RESPONSE = "response";
  public static final String PROCESS_ID_NOTIFICATION = "notification";

  private DE4AConstants ()
  {}
}
