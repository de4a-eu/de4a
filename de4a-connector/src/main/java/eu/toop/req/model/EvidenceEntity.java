package eu.toop.req.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="evidence_entity")
public class EvidenceEntity {
	@Id
	private String idEvidence;
	private String ownerGateway; 
	private boolean usi;
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
	public boolean isUsi() {
		return usi;
	}
	public void setUsi(boolean usi) {
		this.usi = usi;
	}  
	
}
