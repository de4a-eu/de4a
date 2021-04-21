package eu.de4a.connector.as4.owner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.w3c.dom.Element;

public class MessageOwner extends ContextRefreshedEvent {
	private static final long serialVersionUID = 1L;
	private transient Element message;
	private String id;
	private String senderId;
	private String receiverId;
	private String returnService;
	public MessageOwner(ApplicationContext context) {
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

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getReturnService() {
		return returnService;
	}

	public void setReturnService(String returnService) {
		this.returnService = returnService;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

}
