package eu.toop.controller;

public class User {
	private String eidas;
	private String name;
	private String ap1;
	private String ap2;
	private String birthDate;
	private String id;
	private String response;
	private String nationalResponse;
	public String getEidas() {
		return eidas;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAp1() {
		return ap1;
	}

	public void setAp1(String ap1) {
		this.ap1 = ap1;
	}

	 
	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public void setEidas(String eidas) {
		this.eidas = eidas;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getAp2() {
		return ap2;
	}

	public void setAp2(String ap2) {
		this.ap2 = ap2;
	}

	public String getNationalResponse() {
		return nationalResponse;
	}

	public void setNationalResponse(String nationalResponse) {
		this.nationalResponse = nationalResponse;
	}
	
}
