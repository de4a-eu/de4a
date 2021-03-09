package eu.de4a.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="evidence_entity")
public class EvidenceEntity {
	@Id
	private String idEvidence;
	private String ownerGateway; 
	private String endpoint;
	private String xpathResponse;
	public String getIdEvidence() {
		return idEvidence;
	}
	public void setIdEvidence(String idEvidence) {
		this.idEvidence = idEvidence;
	}
	public String getOwnerGateway() {
		return ownerGateway;
	}
	public void setOwnerGateway(String ownerGateway) {
		this.ownerGateway = ownerGateway;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getXpathResponse() {
		return xpathResponse;
	}
	public void setXpathResponse(String xpathResponse) {
		this.xpathResponse = xpathResponse;
	}  
	
}
