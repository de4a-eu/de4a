package eu.de4a.connector.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="domibus_request")
public class DomibusRequest {
	@Id
	private String idrequest;

	public String getIdrequest() {
		return idrequest;
	}

	public void setIdrequest(String idrequest) {
		this.idrequest = idrequest;
	}


}
