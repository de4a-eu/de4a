package eu.de4a.exception;

public class MessageException extends Throwable{ 
	private static final long serialVersionUID = 1L;
	public MessageException(String err) {
		super(err);
	}
}
