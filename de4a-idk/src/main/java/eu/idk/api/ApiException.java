package eu.idk.api;


public class ApiException extends Exception {
	private static final long serialVersionUID = -5984218053719353072L;
	protected final int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
	public int getCode() {
		return code;
	}
}
