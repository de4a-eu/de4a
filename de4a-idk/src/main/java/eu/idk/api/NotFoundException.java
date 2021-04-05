package eu.idk.api;


public class NotFoundException extends ApiException {
	private static final long serialVersionUID = 255014679909735573L;
	private final int code;
    public NotFoundException (int code, String msg) {
        super(code, msg);
        this.code = code;
    }
    @Override
	public int getCode() {
		return code;
	}
}
