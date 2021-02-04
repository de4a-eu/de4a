package eu.de4a.scsp.owner.model;

import java.io.Serializable;
 
public class MunicipioPK implements Serializable{ 
	private static final long serialVersionUID = 1L;
	private String provincia; 
	private String municipio; 

	@Override
	public int hashCode() { 
		return provincia.hashCode()+municipio.hashCode();
	}
 
 

	public String getProvincia() {
		return provincia;
	}



	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}



	public String getMunicipio() {
		return municipio;
	}



	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MunicipioPK other = (MunicipioPK) obj; 
		return municipio.equals(other.municipio) && provincia.equals(other.provincia);
	}
}
