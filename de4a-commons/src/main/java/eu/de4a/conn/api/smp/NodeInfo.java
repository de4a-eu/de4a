package eu.de4a.conn.api.smp;
 

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="node")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER) 
public class NodeInfo { 
	private String id;
	private String endpoint;
	private String x509;
	@XmlElement(name = "id", required = true)
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlElement(name = "endpoint", required = true)
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	@XmlElement(name = "x509", required = true)
	
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
