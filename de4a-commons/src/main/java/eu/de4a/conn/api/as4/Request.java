package eu.de4a.conn.api.as4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="request")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER) 
public class Request {
	private String message;
	private String urlReturn;
	private String evaluatorId;
	private String evidenceServiceUri;
	private String requestId;
	@XmlElement(name = "message", required = true)
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@XmlElement(name = "urlReturn", required = true)
	public String getUrlReturn() {
		return urlReturn;
	}
	public void setUrlReturn(String urlReturn) {
		this.urlReturn = urlReturn;
	}
	@XmlElement(name = "evaluatorId", required = true)
	public String getEvaluatorId() {
		return evaluatorId;
	}
	public void setEvaluatorId(String evaluatorId) {
		this.evaluatorId = evaluatorId;
	}
	@XmlElement(name = "evidenceServiceUri", required = true)
	public String getEvidenceServiceUri() {
		return evidenceServiceUri;
	}
	public void setEvidenceServiceUri(String evidenceServiceUri) {
		this.evidenceServiceUri = evidenceServiceUri;
	}
	@XmlElement(name = "requestId", required = true)
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	 
	
}
