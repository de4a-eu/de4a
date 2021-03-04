package eu.de4a.conn.owner.model;

import org.w3c.dom.Document;

public class PreviewResponse {
	private Document response;
	private String requestId;
	private String returnUrl;
	
	
	public Document getResponse() {
		return response;
	}
	public void setResponse(Document response) {
		this.response = response;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
}
