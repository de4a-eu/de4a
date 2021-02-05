package eu.toop.rest.model;

import java.io.Serializable;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.validation.annotation.Validated;


@Validated
public class EvidenceService implements Serializable {
	private static final long serialVersionUID = 7394493730688081096L;
	
	@JsonProperty("countryCode")
	private String countryCode = null;

	@JsonProperty("atuCode")
	private String atuCode = null;
	
	@JsonProperty("canonicalEvidenceType")
	private String canonicalEvidenceType;

	@JsonProperty("service")
	private String service = null;

	@JsonProperty("dataOwner")
	private String dataOwner = null;

	@JsonProperty("dataTransferor")
	private String dataTransferor = null;

	@JsonProperty("redirectURL")
	private String redirectURL = null;

	public EvidenceService countryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	/**
	 * Get countryCode
	 * 
	 * @return countryCode
	 **/
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public EvidenceService atuCode(String atuCode) {
		this.atuCode = atuCode;
		return this;
	}

	/**
	 * Get atuCode
	 * 
	 * @return atuCode
	 **/
	public String getAtuCode() {
		return atuCode;
	}

	public void setAtuCode(String atuCode) {
		this.atuCode = atuCode;
	}

	/**
	 * Get canonicalEvidence
	 * 
	 * @return canonicalEvidence
	 **/
	public String getCanonicalEvidence() {
		return canonicalEvidenceType;
	}

	public void setCanonicalEvidence(String canonicalEvidence) {
		this.canonicalEvidenceType = canonicalEvidence;
	}

	public EvidenceService service(String service) {
		this.service = service;
		return this;
	}

	/**
	 * Get service
	 * 
	 * @return service
	 **/
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public EvidenceService dataOwner(String dataOwner) {
		this.dataOwner = dataOwner;
		return this;
	}

	/**
	 * Get dataOwner
	 * 
	 * @return dataOwner
	 **/
	public String getDataOwner() {
		return dataOwner;
	}

	public void setDataOwner(String dataOwner) {
		this.dataOwner = dataOwner;
	}

	public EvidenceService dataTransferor(String dataTransferor) {
		this.dataTransferor = dataTransferor;
		return this;
	}

	/**
	 * Get dataTransferor
	 * 
	 * @return dataTransferor
	 **/
	public String getDataTransferor() {
		return dataTransferor;
	}

	public void setDataTransferor(String dataTransferor) {
		this.dataTransferor = dataTransferor;
	}

	public EvidenceService redirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
		return this;
	}

	/**
	 * for the user supported intermediation pattern
	 * 
	 * @return redirectURL
	 **/
	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		EvidenceService evidenceService = (EvidenceService) o;
		return Objects.equals(this.countryCode, evidenceService.countryCode)
				&& Objects.equals(this.atuCode, evidenceService.atuCode)
				&& Objects.equals(this.canonicalEvidenceType, evidenceService.canonicalEvidenceType)
				&& Objects.equals(this.service, evidenceService.service)
				&& Objects.equals(this.dataOwner, evidenceService.dataOwner)
				&& Objects.equals(this.dataTransferor, evidenceService.dataTransferor)
				&& Objects.equals(this.redirectURL, evidenceService.redirectURL);
	}

	@Override
	public int hashCode() {
		return Objects.hash(countryCode, atuCode, canonicalEvidenceType, service, dataOwner, dataTransferor,
				redirectURL);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("    countryCode: ").append(countryCode).append("\n");
		sb.append("    atuCode: ").append(atuCode).append("\n");
		sb.append("    canonicalEvidenceType: ").append(canonicalEvidenceType).append("\n");
		sb.append("    service: ").append(service).append("\n");
		sb.append("    dataOwner: ").append(dataOwner).append("\n");
		sb.append("    dataTransferor: ").append(dataTransferor).append("\n");
		sb.append("    redirectURL: ").append(redirectURL).append("\n");
		return sb.toString();
	}
}
