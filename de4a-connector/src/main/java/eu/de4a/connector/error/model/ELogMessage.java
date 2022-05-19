package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;
import com.helger.commons.annotation.Nonempty;

public enum ELogMessage {
  LOG_IM_REQ_RECEIPT("log.request.receipt", ELogMessageType.SERVICES, "01", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_REQ_RECEIPT("log.request.receipt", ELogMessageType.SERVICES, "02", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_LU_REQ_RECEIPT("log.request.receipt", ELogMessageType.SERVICES, "03", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_SUBSC_REQ_RECEIPT("log.request.receipt", ELogMessageType.SERVICES, "04", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_IM_REQ_PROC("log.request.receipt.imusi", ELogMessageType.SERVICES, "01", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_USI_REQ_PROC("log.request.receipt.imusi", ELogMessageType.SERVICES, "02", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_USI_RED_USER("log.request.receipt.imusi", ELogMessageType.SERVICES, "03", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_USI_DT_REQ_RECEIPT("log.request.receipt.usidt", ELogMessageType.SERVICES, "03", EExternalModuleError.DATA_OWNER, EExternalModuleError.CONNECTOR_DT),
  LOG_IDK_REQ_RECEIPT("log.request.receipt.idk", ELogMessageType.SERVICES, "04", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_IDK_REQ_SENT("log.request.sent.idk", ELogMessageType.SERVICES, "05", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.IDK),
  LOG_SMP_REQ_SENT("log.request.sent.smp", ELogMessageType.CLIENT, "01", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.SMP),
  LOG_PARTICIPANT_LOOKUP("log.participant.lookup", ELogMessageType.SERVICES, "06", EExternalModuleError.NONE, EExternalModuleError.NONE),
  LOG_ERROR_PARTICIPANT_LOOKUP("log.error.participant.lookup", ELogMessageType.SERVICES, "07", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DT),
  LOG_REQ_DE("log.request.sent.de", ELogMessageType.CLIENT, "02", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.DATA_EVALUATOR),
  LOG_REQ_DO("log.request.sent.do", ELogMessageType.CLIENT, "03", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.DATA_OWNER),
  LOG_AS4_MSG_SENT("log.message.sent.as4", ELogMessageType.AS4, "01", EExternalModuleError.AS4, EExternalModuleError.AS4),
  LOG_AS4_RESP_SENT("log.response.sent.as4", ELogMessageType.AS4, "02", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_AS4_REQ_RECEIPT("log.request.receipt.as4", ELogMessageType.AS4, "03", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_AS4_RESP_RECEIPT("log.response.receipt.as4", ELogMessageType.AS4, "04", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_UNEXPECTED("log.error.unexpected", ELogMessageType.ERROR, "01", EExternalModuleError.NONE, EExternalModuleError.NONE),
  LOG_ERROR_AS4_MSG_INVALID("log.error.as4.msg.invalid", ELogMessageType.ERROR, "02", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_ERROR_AS4_REQ_INCOMING("log.error.as4.req.incoming", ELogMessageType.ERROR, "03", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_ERROR_AS4_RESP_RECEIPT("log.error.response.missmatch", ELogMessageType.ERROR, "04", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_UNKNOWN_DE("log.error.unknown.de", ELogMessageType.ERROR, "05", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_AS4_RESP_SENDING("log.error.as4.sending.response", ELogMessageType.ERROR, "06", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_AS4_MSG_SENDING("log.error.as4.sending.message", ELogMessageType.ERROR, "07", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR);

  private final String key;
  private final String code;
  private final ELogMessageType type;
  private final EExternalModuleError origin;
  private final EExternalModuleError destiny;

  ELogMessage(@Nonnull @Nonempty final String key, @Nonnull final ELogMessageType type,
      @Nonnull @Nonempty final String code, @Nonnull final EExternalModuleError origin,
      @Nonnull final EExternalModuleError destiny) {
    this.key = key;
    this.code = code;
    this.type = type;
    this.origin = origin;
    this.destiny = destiny;
  }

  @Nonnull
  @Nonempty
  public String getLogCode() {
    return this.type.getCode() + this.code;
  }

  @Nonnull
  @Nonempty
  public String getKey() {
    return key;
  }

  @Nonnull
  public EExternalModuleError getOrigin() {
    return this.origin;
  }

  @Nonnull
  public EExternalModuleError getDestiny() {
    return this.destiny;
  }
}
