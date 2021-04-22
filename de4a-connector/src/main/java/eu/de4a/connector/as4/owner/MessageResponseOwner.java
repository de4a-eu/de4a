package eu.de4a.connector.as4.owner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.w3c.dom.Element;

public class MessageResponseOwner extends ContextRefreshedEvent {
	private static final long serialVersionUID = 1L;
	private transient Element message;
	private String id;
	private String requestorId;
	public MessageResponseOwner(ApplicationContext context) {
	        super(context);
	}

	public Element getMessage() {
		return message;
	}

	public void setMessage(Element message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestorId() {
		return requestorId;
	}

	public void setRequestorId(String evaluatorId) {
		this.requestorId = evaluatorId;
	}


}
