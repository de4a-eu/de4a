package eu.de4a.exception;

public class ConfigurationException extends Throwable{
	private static final long serialVersionUID = 1L;
	public ConfigurationException(String err) {
		super(err);
	}
}
