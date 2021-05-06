package eu.de4a.exception;

public class MessageException extends Exception{
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
  public MessageException(String err) {
    super(err);
    this.message=err;
    this.code=ExceptionMapping.UNDEFINED.getCode();
  }
  public MessageException(String err, Throwable cause) {
    super(err, cause);
    this.message=err;
    this.code=ExceptionMapping.UNDEFINED.getCode();
  }
	public MessageException(ExceptionMapping exmapping) {
		super(exmapping.getMessage());
		this.message=exmapping.getMessage();
		this.code=exmapping.getCode();
	}
	public String getCode() {
		return code;
	}
	@Override
	public String getMessage() {
		return message;
	}
}
