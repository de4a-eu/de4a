package eu.de4a.connector.error.exceptions;

import org.w3c.dom.Element;

public class SMPLookingMetadataInformationException extends ConnectorException {  
	private static final long serialVersionUID = 1L; 
	private Element userMessage;
	public  SMPLookingMetadataInformationException withUserMessage(Element userMessage){
        this.userMessage=userMessage;
        return this;   
	}
	public Element getUserMessage() {
		return userMessage;
	}
	public void setUserMessage(Element userMessage) {
		this.userMessage = userMessage;
	}
	
}
