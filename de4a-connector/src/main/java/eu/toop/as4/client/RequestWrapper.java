package eu.toop.as4.client;

/**
 * Encapsulates the request to the AS4 gateway and its identifier
 * */
public class RequestWrapper {
	private String id;
	private String evidenceServiceUri;
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
	public String getEvidenceServiceUri() {
		return evidenceServiceUri;
	}
	public void setEvidenceServiceUri(String evidenceServiceUri) {
		this.evidenceServiceUri = evidenceServiceUri;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
}