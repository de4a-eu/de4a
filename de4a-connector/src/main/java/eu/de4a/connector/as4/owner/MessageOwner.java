package eu.de4a.connector.as4.owner;

import org.w3c.dom.Element;

public class MessageOwner {
    private Element message;
    private String id;
    private String senderId;
    private String receiverId;

    public MessageOwner() {
        super();
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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

}
