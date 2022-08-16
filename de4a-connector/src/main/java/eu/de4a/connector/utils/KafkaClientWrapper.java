package eu.de4a.connector.utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.error.level.EErrorLevel;

import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import eu.de4a.kafkaclient.model.EExternalModule;
import eu.de4a.kafkaclient.model.ELogMessage;
import eu.de4a.kafkaclient.model.ELogMessageLevel;

@Component
public class KafkaClientWrapper
{

  // Must be non-static to get auto-wired
  @Value ("#{'${log.metrics.prefix:DE4A METRICS}'}")
  private static String metricsPrefix;

  @Value ("${de4a.connector.id}")
  private String connectorIdentifier;

  private static String CONNECTOR_ID;

  @Value ("${de4a.connector.id}")
  public void setNameStatic (final String connectorIdentifier)
  {
    KafkaClientWrapper.CONNECTOR_ID = connectorIdentifier;
  }

  // private static final String ORIGIN_TAG = "origin";
  // private static final String DESTINY_TAG = "destiny";
  private static final String LOG_CODE_TAG = "logcode";
  private static final String METRICS_TAG = "metrics";
  private static final String METRICS_ENABLED_TAG = "metrics.enabled";

  private KafkaClientWrapper ()
  {}

  public static void sendInfo (final ELogMessage logMessage, final Object... params)
  {
    _send (logMessage, EErrorLevel.INFO, params);
  }

  public static void sendSuccess (final ELogMessage logMessage, final Object... params)
  {
    _send (logMessage, EErrorLevel.SUCCESS, params);
  }

  public static void sendWarn (final ELogMessage logMessage, final Object... params)
  {
    _send (logMessage, EErrorLevel.WARN, params);
  }

  public static void sendError (final ELogMessage logMessage, final Object... params)
  {
    _send (logMessage, EErrorLevel.ERROR, params);
  }

  public static void sendError (final EFamilyErrorType errorType, final EExternalModule module, final Object... params)
  {
    _sendError (errorType, module, EErrorLevel.ERROR, params);
  }

  private static void _send (final ELogMessage logMessage, final EErrorLevel level, final Object... params)
  {
    // A mutable list is needed
    final List <Object> listParams = new CommonsArrayList <> (params);
    listParams.add (0, "[" + logMessage.getLogCode () + "] [" + KafkaClientWrapper.CONNECTOR_ID + "]");
    final String msg = MessageUtils.format (logMessage.getKey (), listParams.toArray ());

    // ThreadContext.put(ORIGIN_TAG, logMessage.getOrigin().getLabel());
    // ThreadContext.put(DESTINY_TAG, logMessage.getDestiny().getLabel());
    ThreadContext.put (METRICS_TAG, metricsPrefix + " >> ");
    ThreadContext.put (METRICS_ENABLED_TAG, "true");
    ThreadContext.put (LOG_CODE_TAG, logMessage.getLogCode ());

    CompletableFuture.runAsync ( () -> DE4AKafkaClient.send (level, msg)).thenRun ( () -> {
      ThreadContext.clearAll ();
      ThreadContext.put (METRICS_ENABLED_TAG, "false");
    });
  }

  private static void _sendError (final EFamilyErrorType errorType,
                                  final EExternalModule module,
                                  final EErrorLevel level,
                                  final Object... params)
  {
    // A mutable list is needed
    final List <Object> listParams = new CommonsArrayList <> (params);
    final String code = module.getID () + ELogMessageLevel.ERROR.getCode () + errorType.getID ();
    listParams.add (0, "[" + code + "] [" + KafkaClientWrapper.CONNECTOR_ID + "]");
    final String msg = MessageUtils.format (errorType.getLabel (), listParams.toArray ());

    ThreadContext.put (METRICS_TAG, metricsPrefix + " >> ");
    ThreadContext.put (METRICS_ENABLED_TAG, "true");
    ThreadContext.put (LOG_CODE_TAG, code);

    CompletableFuture.runAsync ( () -> DE4AKafkaClient.send (level, msg)).thenRun ( () -> {
      ThreadContext.clearAll ();
      ThreadContext.put (METRICS_ENABLED_TAG, "false");
    });
  }
}
