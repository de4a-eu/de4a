package eu.toop.as4.domibus.soap;

public class DomibusException extends Throwable {

	public DomibusException(String err, Exception e) {
		super(err,e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
