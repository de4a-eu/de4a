package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;
import com.helger.commons.annotation.Nonempty;

public enum ELogMessages {

  LOG_IM_REQ_RECEIPT("log.request.receipt", "01", ELogMessageTypes.SERVICES, EExternalModuleError.DATA_EVALUATOR,
      EExternalModuleError.CONNECTOR_DR),
  LOG_REQ_RECEIPT("log.request.receipt", "02", ELogMessageTypes.SERVICES, EExternalModuleError.DATA_EVALUATOR,
      EExternalModuleError.CONNECTOR_DR),
  LOG_LU_REQ_RECEIPT("log.request.receipt", "03", ELogMessageTypes.SERVICES, EExternalModuleError.DATA_EVALUATOR,
      EExternalModuleError.CONNECTOR_DR),
  LOG_SUBSC_REQ_RECEIPT("log.request.receipt", "04", ELogMessageTypes.SERVICES, EExternalModuleError.DATA_EVALUATOR,
      EExternalModuleError.CONNECTOR_DR),
  LOG_IM_REQ_PROC("log.request.receipt.imusi", "01", ELogMessageTypes.SERVICES, EExternalModuleError.CONNECTOR_DR,
      EExternalModuleError.CONNECTOR_DT),
  LOG_USI_REQ_PROC("log.request.receipt.imusi", "02", ELogMessageTypes.SERVICES, EExternalModuleError.CONNECTOR_DR,
      EExternalModuleError.CONNECTOR_DT),
  LOG_USI_RED_USER("log.request.receipt.imusi", "03", ELogMessageTypes.SERVICES, EExternalModuleError.CONNECTOR_DR,
      EExternalModuleError.CONNECTOR_DT),
  LOG_USI_DT_REQ_RECEIPT("log.request.receipt.usidt", "03", ELogMessageTypes.SERVICES, EExternalModuleError.DATA_OWNER,
      EExternalModuleError.CONNECTOR_DT),
  LOG_IDK_REQ_RECEIPT("log.request.receipt.idk", "04", ELogMessageTypes.SERVICES, EExternalModuleError.DATA_EVALUATOR,
      EExternalModuleError.CONNECTOR_DR),
  LOG_IDK_REQ_SENT("log.request.sent.idk", "05", ELogMessageTypes.SERVICES, EExternalModuleError.CONNECTOR_DR,
      EExternalModuleError.IDK),
  LOG_SMP_REQ_SENT("log.request.sent.smp", "01", ELogMessageTypes.CLIENT, EExternalModuleError.CONNECTOR_DR,
      EExternalModuleError.SMP),
  LOG_PARTICIPANT_LOOKUP("log.participant.lookup", "06", ELogMessageTypes.SERVICES, EExternalModuleError.NONE,
      EExternalModuleError.NONE),
  LOG_ERROR_PARTICIPANT_LOOKUP("log.error.participant.lookup", "07", ELogMessageTypes.SERVICES,
      EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DT),
  LOG_REQ_DE("log.request.sent.de", "02", ELogMessageTypes.CLIENT, EExternalModuleError.CONNECTOR_DR,
      EExternalModuleError.DATA_EVALUATOR),
  LOG_REQ_DO("log.request.sent.do", "03", ELogMessageTypes.CLIENT, EExternalModuleError.CONNECTOR_DT,
      EExternalModuleError.DATA_OWNER),
  LOG_AS4_MSG_SENT("log.message.sent.as4", "01", ELogMessageTypes.AS4, EExternalModuleError.AS4,
      EExternalModuleError.AS4),
  LOG_AS4_RESP_SENT("log.response.sent.as4", "02", ELogMessageTypes.AS4, EExternalModuleError.CONNECTOR_DT,
      EExternalModuleError.CONNECTOR_DR),
  LOG_AS4_REQ_RECEIPT("log.request.receipt.as4", "03", ELogMessageTypes.AS4, EExternalModuleError.CONNECTOR_DR,
      EExternalModuleError.CONNECTOR_DT),
  LOG_AS4_RESP_RECEIPT("log.response.receipt.as4", "04", ELogMessageTypes.AS4, EExternalModuleError.CONNECTOR_DT,
      EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_UNEXPECTED("log.error.unexpected", "01", ELogMessageTypes.ERROR, EExternalModuleError.NONE,
      EExternalModuleError.NONE),
  LOG_ERROR_AS4_MSG_INVALID("log.error.as4.msg.invalid", "02", ELogMessageTypes.ERROR,
      EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_ERROR_AS4_REQ_INCOMING("log.error.as4.req.incoming", "03", ELogMessageTypes.ERROR,
      EExternalModuleError.CONNECTOR_DR, EExternalModuleError.CONNECTOR_DT),
  LOG_ERROR_AS4_RESP_RECEIPT("log.error.response.missmatch", "04", ELogMessageTypes.ERROR,
      EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_UNKNOWN_DE("log.error.unknown.de", "05", ELogMessageTypes.ERROR, EExternalModuleError.CONNECTOR_DR,
      EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_AS4_RESP_SENDING("log.error.as4.sending.response", "06", ELogMessageTypes.ERROR,
      EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR),
  LOG_ERROR_AS4_MSG_SENDING("log.error.as4.sending.message", "07", ELogMessageTypes.ERROR,
      EExternalModuleError.CONNECTOR_DT, EExternalModuleError.CONNECTOR_DR);

  private final String key;
  private final String code;
  private final ELogMessageTypes type;
  private final EExternalModuleError origin;
  private final EExternalModuleError destiny;

  ELogMessages(@Nonnull @Nonempty final String key, @Nonnull @Nonempty final String code,
      @Nonnull final ELogMessageTypes type, @Nonnull final EExternalModuleError origin,
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
  @Nonempty
  public String getCode() {
    return code;
  }

  @Nonnull
  public ELogMessageTypes getType() {
    return type;
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
