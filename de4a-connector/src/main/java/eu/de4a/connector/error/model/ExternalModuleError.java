package eu.de4a.connector.error.model;

public enum ExternalModuleError {
    IDK("1", "IDK"), SMP("2", "SMP"), CONNECTOR_DR("3", "CONNECTOR DR"), CONNECTOR_DT("4", "CONNECTOR DT"),
    DATA_OWNER("4", "DATA OWNER"), DATA_EVALUATOR("5", "DATA EVALUATOR"), NONE("0", "NONE");

    private String id;
    private String label;

    ExternalModuleError(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
