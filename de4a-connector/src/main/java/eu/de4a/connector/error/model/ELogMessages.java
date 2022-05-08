package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;
import com.helger.commons.annotation.Nonempty;

public enum ELogMessages {
  LOG_IM_REQ_RECEIPT("log.request.receipt", ELogMessageTypes.SERVICES, "01", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_REQ_RECEIPT("log.request.receipt", ELogMessageTypes.SERVICES, "02", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_LU_REQ_RECEIPT("log.request.receipt", ELogMessageTypes.SERVICES, "03", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_SUBSC_REQ_RECEIPT("log.request.receipt", ELogMessageTypes.SERVICES, "04", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_IM_REQ_PROC("log.request.receipt.imusi", ELogMessageTypes.SERVICES, "01", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_USI_REQ_PROC("log.request.receipt.imusi", ELogMessageTypes.SERVICES, "02", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_USI_RED_USER("log.request.receipt.imusi", ELogMessageTypes.SERVICES, "03", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_USI_DT_REQ_RECEIPT("log.request.receipt.usidt", ELogMessageTypes.SERVICES, "03", EExternalModuleError.DATA_OWNER, EExternalModuleError.CONNECTOR_DT),
  LOG_IDK_REQ_RECEIPT("log.request.receipt.idk", ELogMessageTypes.SERVICES, "04", EExternalModuleError.DATA_EVALUATOR, EExternalModuleError.CONNECTOR_DR),
  LOG_IDK_REQ_SENT("log.request.sent.idk", ELogMessageTypes.SERVICES, "05", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.IDK),
  LOG_SMP_REQ_SENT("log.request.sent.smp", ELogMessageTypes.CLIENT, "01", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.SMP),
  LOG_PARTICIPANT_LOOKUP("log.participant.lookup", ELogMessageTypes.SERVICES, "06", EExternalModuleError.NONE, EExternalModuleError.NONE),
  LOG_ERROR_PARTICIPANT_LOOKUP("log.error.participant.lookup", ELogMessageTypes.SERVICES, "07", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DT),
  LOG_REQ_DE("log.request.sent.de", ELogMessageTypes.CLIENT, "02", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.DATA_EVALUATOR),
  LOG_REQ_DO("log.request.sent.do", ELogMessageTypes.CLIENT, "03", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.DATA_OWNER),
  LOG_AS4_MSG_SENT("log.message.sent.as4", ELogMessageTypes.AS4, "01", EExternalModuleError.AS4, EExternalModuleError.AS4),
  LOG_AS4_RESP_SENT("log.response.sent.as4", ELogMessageTypes.AS4, "02", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_AS4_REQ_RECEIPT("log.request.receipt.as4", ELogMessageTypes.AS4, "03", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_AS4_RESP_RECEIPT("log.response.receipt.as4", ELogMessageTypes.AS4, "04", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_UNEXPECTED("log.error.unexpected", ELogMessageTypes.ERROR, "01", EExternalModuleError.NONE, EExternalModuleError.NONE),
  LOG_ERROR_AS4_MSG_INVALID("log.error.as4.msg.invalid", ELogMessageTypes.ERROR, "02", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_ERROR_AS4_REQ_INCOMING("log.error.as4.req.incoming", ELogMessageTypes.ERROR, "03", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_ERROR_AS4_RESP_RECEIPT("log.error.response.missmatch", ELogMessageTypes.ERROR, "04", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_UNKNOWN_DE("log.error.unknown.de", ELogMessageTypes.ERROR, "05", EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_AS4_RESP_SENDING("log.error.as4.sending.response", ELogMessageTypes.ERROR, "06", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_AS4_MSG_SENDING("log.error.as4.sending.message", ELogMessageTypes.ERROR, "07", EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR);

  private final String key;
  private final String code;
  private final ELogMessageTypes type;
  private final EExternalModuleError origin;
  private final EExternalModuleError destiny;

  ELogMessages(@Nonnull @Nonempty final String key, @Nonnull final ELogMessageTypes type,
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
