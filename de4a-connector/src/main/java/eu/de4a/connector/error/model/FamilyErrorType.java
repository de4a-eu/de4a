package eu.de4a.connector.error.model;

public enum FamilyErrorType { 
	MISSING_REQUIRED_ARGUMENTS("2", MessageKeys.ERROR_MISSING_ARGS),
	CONNECTION_ERROR("3", MessageKeys.ERROR_CONNECTION),
	ERROR_RESPONSE("4", MessageKeys.ERROR_RESPONSE),
	CONVERSION_ERROR("5", MessageKeys.ERROR_CONVERSION),
	SAVING_DATA_ERROR("6", MessageKeys.ERROR_DATABASE),
	SCHEMA_VALIDATION_FAILED("7", MessageKeys.ERROR_SCHEME_VALIDATION),
	AS4_ERROR_COMMUNICATION("8", MessageKeys.ERROR_AS4_COMMUNICATION);

    private String id;
    private String label;

    FamilyErrorType(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getID() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
