package eu.de4a.exception;

public enum ExceptionMapping {
  
    UNDEFINED("9999","Undefined error");

    private final String message;
    private final String code;

    ExceptionMapping(String code, String message) {
        this.code = code;
        this.message = message;
    }

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}
 
}