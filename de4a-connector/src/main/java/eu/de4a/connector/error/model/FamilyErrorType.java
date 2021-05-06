package eu.de4a.connector.error.model;

public enum FamilyErrorType {
    SERVICE_NOT_FOUND("01", MessageKeys.ERROR_SERVICE_NOT_FOUND),
	MISSING_REQUIRED_ARGUMENTS("02", MessageKeys.ERROR_MISSING_ARGS),
	CONNECTION_ERROR("03", MessageKeys.ERROR_CONNECTION),
	ERROR_RESPONSE("04", MessageKeys.ERROR_RESPONSE),
	CONVERSION_ERROR("05", MessageKeys.ERROR_CONVERSION),
	SAVING_DATA_ERROR("06", MessageKeys.ERROR_DATABASE),
	SCHEMA_VALIDATION_FAILED("07", MessageKeys.ERROR_SCHEME_VALIDATION),
	AS4_ERROR_COMMUNICATION("08", MessageKeys.ERROR_AS4_COMMUNICATION);

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
