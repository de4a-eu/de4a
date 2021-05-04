package eu.de4a.connector.error.model;

public enum DetailMessageError { 
	RESOURCE_UNAVAILABLE("01","error.resource.unavailable"), 
	SCHEMA_FAILED("02","error.schema.failed"),
	MISSING_REQUIRED_ARGUMENTS("03","error.missing.arguments") ,
	EMPTY_RESPONSE("05","error.empty.response"),
	CONNECTION_TIMEOUT("06","error.timeout"),
	UNABLE_RESOLVE_HOST("07","error.unable.resolve.host"),
	SSL_HANDSHAKE_FAIL("08","error.ssl.handshake"),
	SMP_DATA_NOT_FOUND("09","error.smp.data.not.found"), 
	TRANSFORMATION_MESSAGE_ERR ("23","error.message.transformation"), 
	CONFIGURATION ("25","error.configuration"), 
	REGREP_ERROR("26","error.regrep.building");
	private String id;
	private String label;
	DetailMessageError(String id,String label){
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
