package eu.de4a.connector.error.model;

public enum ExternalModuleError {
    IDK("01", "IDK"), 
    SMP("02", "SMP"), 
    CONNECTOR_DR("03", "CONNECTOR DR"), 
    CONNECTOR_DT("04", "CONNECTOR DT"),
    DATA_OWNER("04", "DATA OWNER"), 
    DATA_EVALUATOR("05", "DATA EVALUATOR"), 
    NONE("00", "NONE");

    private String id;
    private String label;

    ExternalModuleError(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

}
