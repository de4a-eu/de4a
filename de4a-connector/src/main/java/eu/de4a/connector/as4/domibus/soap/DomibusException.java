package eu.de4a.connector.as4.domibus.soap;

public class DomibusException extends Exception {

	public DomibusException(String err, Exception e) {
		super(err, e);
	}

	private static final long serialVersionUID = 1L;

}
