package eu.de4a.connector.error.exceptions;

public class MessageException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String message;

    public MessageException(String err) {
        super(err);
        this.message = err;
    }

    public MessageException(String err, String code) {
        super(err);
        this.message = err;
    }

    public MessageException(String err, Throwable cause) {
        super(err, cause);
        this.message = err;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
