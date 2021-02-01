package eu.toop.smp.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
 
@Entity
@Table(name="node")
public class Node implements Serializable{
  
	private static final long serialVersionUID = -5810540273674392449L;
	@Id
	private String id;
	@Column(length=1024)
	private String endpoint;
	@Column
	@Lob
	private String x509; 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	} 
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	} 
	public String getX509() {
		return x509;
	}
	public void setX509(String x509) {
		this.x509 = x509;
	}
	@Override	
	public String toString() {
		return String.format("id=%s, endopoint=%s", id,endpoint);
	}
}
