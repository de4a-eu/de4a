package eu.toop.req.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="evaluator_request_data") 
@IdClass(value = EvaluatorRequestDataPK.class)
public class EvaluatorRequestData {
	@Id
	private String idrequest;
	@ManyToOne(fetch = FetchType.LAZY) 
	private EvaluatorRequest request;
	@Id 
	private String iddata; 
	@Lob
	@Column(name = "photo", columnDefinition="BLOB")
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
	public EvaluatorRequest getRequest() {
		return request;
	}
	public void setRequest(EvaluatorRequest request) {
		idrequest=request.getIdrequest();
		this.request = request;
	}
	public String getIddata() {
		return iddata;
	}
	public void setIddata(String iddata) {
		this.iddata = iddata;
	}
	
}
