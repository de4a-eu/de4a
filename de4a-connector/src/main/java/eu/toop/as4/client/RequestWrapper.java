package eu.toop.as4.client;

import org.w3c.dom.Document;

/**
 * Encapsulates the request to the AS4 gateway and its identifier
 * */
public class RequestWrapper {
	private String id;
	private String evidenceServiceUri;
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
	 
	
	
}
