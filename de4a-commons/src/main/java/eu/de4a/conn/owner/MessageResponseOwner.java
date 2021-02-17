package eu.de4a.conn.owner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.w3c.dom.Element;

public class MessageResponseOwner extends ContextRefreshedEvent { 
	private static final long serialVersionUID = 1L;
	private Element message;
	private String id; 
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

 
}
