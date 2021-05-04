package eu.de4a.connector.error.model;

public enum FamilyErrorType { 
	MISSING_REQUIRED_ARGUMENTS("2","error.missing.arguments"),
	CONNECTION_ERROR("3","error.connection") ,
	ERROR_RESPONSE("4","error.response"),
	CONVERSION_ERROR("5","error.conversion"),
	SAVING_DATA_ERROR("6","error.database"),
	SCHEMA_VALIDATION_FAILED("7","error.schema.validation"),
	AS4_ERROR_COMMUNICATION("8","error.as4.communications") ;
	private String id;
	private String label;
	FamilyErrorType(String id,String label){
	    this.id = id;
	    this.label=label;
	}
	public String getID(){
	    return id;
	}
	public String getLabel() {
		return label;
	} 
}
