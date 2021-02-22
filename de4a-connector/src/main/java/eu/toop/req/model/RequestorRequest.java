package eu.toop.req.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="requestor_request")
public class RequestorRequest {
	@Id
	private String idrequest;
	private String evidenceServiceUri;
	private String returnServiceUri;
	private String senderId; 
	private boolean done;
	public String getIdrequest() {
		return idrequest;
	}
	public void setIdrequest(String idrequest) {
		this.idrequest = idrequest;
	}
	public String getEvidenceServiceUri() {
		return evidenceServiceUri;
	}
	public void setEvidenceServiceUri(String evidenceServiceUri) {
		this.evidenceServiceUri = evidenceServiceUri;
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
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	 
	
}
