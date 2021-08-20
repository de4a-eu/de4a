package eu.de4a.connector.error.model;

public enum LogMessages {
    
    LOG_IM_REQ_RECEIPT("log.request.receipt.imusi", "01", LogMessageTypes.SERVICES, 
            ExternalModuleError.DATA_EVALUATOR, ExternalModuleError.CONNECTOR_DR),
    LOG_USI_REQ_RECEIPT("log.request.receipt.imusi", "02", LogMessageTypes.SERVICES, 
            ExternalModuleError.DATA_EVALUATOR, ExternalModuleError.CONNECTOR_DR),
    LOG_IM_REQ_PROC("log.request.receipt.imusi", "01", LogMessageTypes.SERVICES, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.CONNECTOR_DT),
    LOG_USI_REQ_PROC("log.request.receipt.imusi", "02", LogMessageTypes.SERVICES, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.CONNECTOR_DT),
    LOG_USI_DT_REQ_RECEIPT("log.request.receipt.usidt", "03", LogMessageTypes.SERVICES, 
            ExternalModuleError.DATA_OWNER, ExternalModuleError.CONNECTOR_DT),
    LOG_IDK_REQ_RECEIPT("log.request.receipt.idk", "04", LogMessageTypes.SERVICES, 
            ExternalModuleError.DATA_EVALUATOR, ExternalModuleError.CONNECTOR_DR),
    LOG_IDK_REQ_SENT("log.request.sent.idk", "05", LogMessageTypes.SERVICES, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.IDK),
    LOG_SMP_REQ_SENT("log.request.sent.smp", "01", LogMessageTypes.CLIENT, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.SMP),
    LOG_OWNER_LOOKUP("log.owner.lookup", "06", LogMessageTypes.SERVICES, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.NONE),
    LOG_ERROR_OWNER_LOOKUP("log.error.owner.lookup", "07", LogMessageTypes.SERVICES, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.CONNECTOR_DT),
    LOG_REQ_DE("log.request.sent.de", "02", LogMessageTypes.CLIENT, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.DATA_EVALUATOR),
    LOG_REQ_IM_DO("log.request.sent.imusi.do", "03", LogMessageTypes.CLIENT, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.DATA_OWNER),
    LOG_REQ_USI_DO("log.request.sent.imusi.do", "04", LogMessageTypes.CLIENT, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.DATA_OWNER),
    LOG_AS4_REQ_SENT("log.request.sent.as4", "01", LogMessageTypes.AS4, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.CONNECTOR_DT),
    LOG_AS4_RESP_SENT("log.response.sent.as4", "02", LogMessageTypes.AS4, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.CONNECTOR_DR),
    LOG_AS4_REQ_RECEIPT("log.request.receipt.as4", "03", LogMessageTypes.AS4, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.CONNECTOR_DT),
    LOG_AS4_RESP_RECEIPT("log.response.receipt.as4", "04", LogMessageTypes.AS4, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.CONNECTOR_DR),
    LOG_ERROR_UNEXPECTED("log.error.unexpected", "01", LogMessageTypes.ERROR, 
            ExternalModuleError.NONE, ExternalModuleError.NONE),
    LOG_ERROR_AS4_MSG_INVALID("log.error.as4.msg.invalid", "02", LogMessageTypes.ERROR, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.CONNECTOR_DT),
    LOG_ERROR_AS4_REQ_INCOMING("log.error.as4.req.incoming", "03", LogMessageTypes.ERROR, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.CONNECTOR_DT),
    LOG_ERROR_AS4_RESP_RECEIPT("log.error.response.missmatch", "04", LogMessageTypes.ERROR, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.CONNECTOR_DR),
    LOG_ERROR_UNKNOWN_DE("log.error.unknown.de", "05", LogMessageTypes.ERROR, 
            ExternalModuleError.CONNECTOR_DR, ExternalModuleError.CONNECTOR_DR),
    LOG_ERROR_AS4_RESP_SENDING("log.error.as4.sending.response", "06", LogMessageTypes.ERROR, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.CONNECTOR_DR),
    LOG_ERROR_AS4_MSG_SENDING("log.error.as4.sending.message", "07", LogMessageTypes.ERROR, 
            ExternalModuleError.CONNECTOR_DT, ExternalModuleError.CONNECTOR_DR);

    private String key;
    private String code;
    LogMessageTypes type;
    ExternalModuleError origin;
    ExternalModuleError destiny;
    
    LogMessages(String key, String code, LogMessageTypes type, ExternalModuleError origin, 
            ExternalModuleError destiny) {
        this.key = key;
        this.code = code;
        this.type = type;
        this.origin = origin;
        this.destiny = destiny;
    }
    
    public String getLogCode() {
        return this.type.code + this.code;
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    public LogMessageTypes getType() {
        return type;
    }
    
    public ExternalModuleError getOrigin() {
        return this.origin;
    }
    
    public ExternalModuleError getDestiny() {
        return this.destiny;
    }
}
