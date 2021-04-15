package eu.de4a.connector.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "requestor_request_data")
@IdClass(value = RequestDataPK.class)
public class RequestorRequestData {
	@Id
	private String idrequest;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private RequestorRequest request;
	@Id
	private String iddata;
	@Lob
	@Column(name = "photo", columnDefinition = "BLOB")
	private byte[] data;
	private String mimetype;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public RequestorRequest getRequest() {
		return request;
	}

	public void setRequest(RequestorRequest request) {
		this.idrequest = request.getIdrequest();
		this.request = request;
	}

	public String getIddata() {
		return iddata;
	}

	public void setIddata(String iddata) {
		this.iddata = iddata;
	}

}
