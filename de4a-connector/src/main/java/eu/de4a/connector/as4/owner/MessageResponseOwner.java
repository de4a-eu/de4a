package eu.de4a.connector.as4.owner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.w3c.dom.Element;

public class MessageResponseOwner extends ContextRefreshedEvent {
	private static final long serialVersionUID = 1L;
	private transient Element message;
	private String id;
	private String dataEvaluatorId;
	private String dataOwnerId;
	
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

	public String getDataEvaluatorId() {
		return dataEvaluatorId;
	}

	public void setDataEvaluatorId(String dataEvaluatorId) {
		this.dataEvaluatorId = dataEvaluatorId;
	}
	
	public String getDataOwnerId() {
        return dataOwnerId;
    }

    public void setDataOwnerId(String dataOwnerId) {
        this.dataOwnerId = dataOwnerId;
    }


}
