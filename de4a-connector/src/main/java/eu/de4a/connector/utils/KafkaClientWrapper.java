package eu.de4a.connector.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.helger.commons.error.level.EErrorLevel;

import eu.de4a.connector.error.model.ELogMessage;
import eu.de4a.kafkaclient.DE4AKafkaClient;

@Component
public class KafkaClientWrapper {

    // Must be non-static to get auto-wired
    @Value("#{'${log.metrics.prefix:DE4A METRICS}'}")
    private static String metricsPrefix;

    private static final String ORIGIN_TAG = "origin";
    private static final String DESTINY_TAG = "destiny";
    private static final String LOG_CODE_TAG = "logcode";
    private static final String METRICS_TAG = "metrics";
    private static final String METRICS_ENABLED_TAG = "metrics.enabled";

    private KafkaClientWrapper (){}

    public static void sendInfo(final ELogMessage logMessage, final Object...params) {
        _send(logMessage, EErrorLevel.INFO, params);
    }

    public static void sendSuccess(final ELogMessage logMessage, final Object...params) {
        _send(logMessage, EErrorLevel.SUCCESS, params);
    }

    public static void sendWarn(final ELogMessage logMessage, final Object...params) {
        _send(logMessage, EErrorLevel.WARN, params);
    }

    public static void sendError(final ELogMessage logMessage, final Object...params) {
        _send(logMessage, EErrorLevel.ERROR, params);
    }

    private static void _send(final ELogMessage logMessage, final EErrorLevel level, Object...params) {
    	ArrayList<Object> listParams = new ArrayList<Object>(Arrays.asList(params));
    	listParams.add(0,  "[" + logMessage.getLogCode() + "]");
        final String msg = MessageUtils.format(logMessage.getKey(), listParams.toArray());

        ThreadContext.put(ORIGIN_TAG, logMessage.getOrigin().getLabel());
        ThreadContext.put(DESTINY_TAG, logMessage.getDestiny().getLabel());
        ThreadContext.put(METRICS_TAG, metricsPrefix + " >> ");
        ThreadContext.put(METRICS_ENABLED_TAG, "true");
        ThreadContext.put(LOG_CODE_TAG, logMessage.getLogCode());

        CompletableFuture.runAsync(() -> DE4AKafkaClient.send(level, msg))
            .thenRun(() -> {
                ThreadContext.clearAll();
                ThreadContext.put(METRICS_ENABLED_TAG, "false");
            });
    }

}
