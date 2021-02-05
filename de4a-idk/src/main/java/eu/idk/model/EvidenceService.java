package eu.idk.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.validation.annotation.Validated;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * the rest of the service metadata is located in the Central Directory
 */
@Schema(description = "the rest of the service metadata is located in the Central Directory")
@Validated
@Entity
@Table(name = "EvidenceService")
public class EvidenceService extends InlineResponse200 {
	private static final long serialVersionUID = 7394493730688081096L;
	
	@Id
	private Long id;

	@JsonProperty("countryCode")
	@Column
	private String countryCode = null;

	@JsonProperty("atuCode")
	@Column
	private String atuCode = null;

	@JsonProperty("canonicalEvidenceType")	
	@Enumerated(EnumType.STRING)
	@Transient
	private EvidenceTypeIds canonicalEvidenceType = null;
	
	@Column
	private String canonicalEvidence;

	@JsonProperty("service")
	@Column
	private String service = null;

	@JsonProperty("dataOwner")
	@Column
	private String dataOwner = null;

	@JsonProperty("dataTransferor")
	@Column
	private String dataTransferor = null;

	@JsonProperty("redirectURL")
	@Column
	private String redirectURL = null;

	public EvidenceService countryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}
	
	@PostLoad
    void fillTransient() {
        this.canonicalEvidenceType = EvidenceTypeIds.fromValue(this.canonicalEvidence);
    }

	/**
	 * Get countryCode
	 * 
	 * @return countryCode
	 **/
	@Schema(description = "")

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
	@Schema(description = "")

	public String getAtuCode() {
		return atuCode;
	}

	public void setAtuCode(String atuCode) {
		this.atuCode = atuCode;
	}

	public EvidenceService canonicalEvidenceType(EvidenceTypeIds canonicalEvidenceType) {
		this.canonicalEvidenceType = canonicalEvidenceType;
		return this;
	}

	/**
	 * Get canonicalEvidenceType
	 * 
	 * @return canonicalEvidenceType
	 **/
	@Schema(description = "")

	public EvidenceTypeIds getCanonicalEvidenceType() {
		return canonicalEvidenceType;
	}

	public void setCanonicalEvidenceType(EvidenceTypeIds canonicalEvidenceType) {
		this.canonicalEvidenceType = canonicalEvidenceType;
	}
	
	public void setCanonicalEvidenceType(String canonicalEvidenceType) {
		this.canonicalEvidenceType = EvidenceTypeIds.fromValue(canonicalEvidenceType);
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
	@Schema(description = "")

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
	@Schema(description = "")

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
	@Schema(description = "")

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
	@Schema(description = "for the user supported intermediation pattern")

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
		sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
		sb.append("    atuCode: ").append(toIndentedString(atuCode)).append("\n");
		sb.append("    canonicalEvidenceType: ").append(toIndentedString(canonicalEvidenceType)).append("\n");
		sb.append("    service: ").append(toIndentedString(service)).append("\n");
		sb.append("    dataOwner: ").append(toIndentedString(dataOwner)).append("\n");
		sb.append("    dataTransferor: ").append(toIndentedString(dataTransferor)).append("\n");
		sb.append("    redirectURL: ").append(toIndentedString(redirectURL)).append("\n");
		return sb.toString();
	}
}
