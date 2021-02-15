package eu.toop.req.model;

import java.io.Serializable;

public class EvaluatorRequestDataPK implements Serializable{ 
	private static final long serialVersionUID = 1L;
	private String idrequest; 
	private String iddata; 

	@Override
	public int hashCode() { 
		return idrequest.hashCode()+iddata.hashCode();
	}
 
	 

	public String getIddata() {
		return iddata;
	}

	public void setIddata(String iddata) {
		this.iddata = iddata;
	}

	public String getIdrequest() {
		return idrequest;
	}



	public void setIdrequest(String idrequest) {
		this.idrequest = idrequest;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EvaluatorRequestDataPK other = (EvaluatorRequestDataPK) obj; 
		return idrequest.equals(other.getIdrequest()) && iddata.equals(other.getIddata());
	}
}