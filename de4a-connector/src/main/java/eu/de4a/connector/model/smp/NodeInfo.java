package eu.de4a.connector.model.smp;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SignedServiceMetadata", namespace = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class NodeInfo {
	@XmlElement(name = "ProcessIdentifier", required = true)
	private String processId;

	@XmlElement(name = "ParticipantIdentifier", required = true)
	private String participantId;

	@XmlElement(name = "DocumentIdentifier", required = true)
	private String documentId;

	@XmlElement(name = "EndpointURI", required = true)
	private String endpoint;

	@XmlElement(name = "Certificate", required = true)
	private byte[] x509;


	public String getProcessIdentifier() {
		return processId;
	}
	public void setProcessIdentifier(String processId) {
		this.processId = processId;
	}


	public String getParticipantIdentifier() {
		return participantId;
	}
	public void setParticipantIdentifier(String participantId) {
		this.participantId = participantId;
	}


	public String getDocumentIdentifier() {
		return documentId;
	}
	public void setDocumentIdentifier(String documentId) {
		this.documentId = documentId;
	}


	public String getEndpointURI() {
		return endpoint;
	}
	public void setEndpointURI(String endpoint) {
		this.endpoint = endpoint;
	}


	public byte[] getCertificate() {
		return x509;
	}
	public void setCertificate(byte[] x509) {
		this.x509 = x509;
	}

	@Override
	public String toString() {
		return String.format("processId=%s, participantId=%s, documentId=%s, endopoint=%s",
				processId, participantId, documentId, endpoint);
	}
}
