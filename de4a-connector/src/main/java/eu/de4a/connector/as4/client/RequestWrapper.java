package eu.de4a.connector.as4.client;

/**
 * Encapsulates the request to the AS4 gateway and its identifier
 * */
public class RequestWrapper {
	private String id;
	private String receiverId;
	private String returnServiceUri;
	private String senderId;
	private Object request;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Object getRequest() {
		return request;
	}
	public void setRequest(Object request) {
		this.request = request;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public String getReturnServiceUri() {
		return returnServiceUri;
	}
	public void setReturnServiceUri(String returnServiceUri) {
		this.returnServiceUri = returnServiceUri;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
}
