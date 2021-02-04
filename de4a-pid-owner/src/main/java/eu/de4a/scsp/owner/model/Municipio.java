package eu.de4a.scsp.owner.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
 
@Entity
@Table(name="Municipio")
@IdClass(value = MunicipioPK.class)
public class Municipio implements Serializable{
  
	private static final long serialVersionUID = -5810540273674392449L;
	@Id
	@Column(name = "COD_PROV")
	private String provincia; 	
	@Id
	@Column(name = "COD_MUN")
	private String municipio; 
	@Column (name = "NOMBRE")
	private String nombre;
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
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}   
 
}
