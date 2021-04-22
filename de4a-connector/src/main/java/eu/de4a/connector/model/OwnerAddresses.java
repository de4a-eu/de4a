package eu.de4a.connector.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="owner_addresses")
public class OwnerAddresses {
	@Id
	private String agentUrn;
	private String endpoint;
	public String getAgentUrn() {
		return agentUrn;
	}
	public void setAgentUrn(String agentUrn) {
		this.agentUrn = agentUrn;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

}
