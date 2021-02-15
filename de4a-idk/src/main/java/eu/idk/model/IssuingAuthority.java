package eu.idk.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * IssuingAuthority
 */
@Validated
@Entity
@Table(name = "IssuingAuthority")
public class IssuingAuthority extends InlineResponse200 {
	private static final long serialVersionUID = -3352737906437012L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@JsonProperty("canonicalEvidenceType")	
	@Enumerated(EnumType.STRING)
	@Transient
	private EvidenceTypeIds evidenceTypeId = null;
	
	@Column
	private String evidenceType;

	@JsonProperty("countryCode")
	@Column
	private String countryCode = null;

	@JsonProperty("iaLevelPath")
	@Column
	private String iaLevelPath = null;

	@JsonProperty("iaTotalNum")
	@Transient
	private Integer iaTotalNum = null;

	@JsonProperty("iaOrganisationalStructure")
	@Valid
	@OneToMany(mappedBy = "issuingAuthority", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AtuItem> iaOrganisationalStructure = new ArrayList<>();

	public IssuingAuthority evidenceTypeId(EvidenceTypeIds evidenceTypeId) {
		this.evidenceTypeId = evidenceTypeId;
		return this;
	}
	
	@PostLoad
    void fillTransient() {
        this.evidenceTypeId = EvidenceTypeIds.fromValue(this.evidenceType);
    }

	/**
	 * Get evidenceTypeId
	 * 
	 * @return evidenceTypeId
	 **/
	@Schema(required = true, description = "")
	@NotNull

	@Valid
	public EvidenceTypeIds getEvidenceTypeId() {
		return evidenceTypeId;
	}

	public void setEvidenceTypeId(EvidenceTypeIds evidenceTypeId) {
		this.evidenceTypeId = evidenceTypeId;
	}

	public IssuingAuthority countryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	/**
	 * Get countryCode
	 * 
	 * @return countryCode
	 **/
	@Schema(required = true, description = "")
	@NotNull

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public IssuingAuthority iaLevelPath(String iaLevelPath) {
		this.iaLevelPath = iaLevelPath;
		return this;
	}

	/**
	 * Path of administrative territorial level of the issuing authority
	 * 
	 * @return iaLevelPath
	 **/
	@Schema(required = true, description = "Path of administrative territorial level of the issuing authority")
	@NotNull

	public String getIaLevelPath() {
		return iaLevelPath;
	}

	public void setIaLevelPath(String iaLevelPath) {
		this.iaLevelPath = iaLevelPath;
	}

	public IssuingAuthority iaTotalNum(Integer iaTotalNum) {
		this.iaTotalNum = iaTotalNum;
		return this;
	}

	/**
	 * Number of potential issuing authorities in the same administrative
	 * territorial level
	 * 
	 * @return iaTotalNum
	 **/
	@Schema(required = true, description = "Number of potential issuing authorities in the same administrative territorial level")
	@NotNull

	public Integer getIaTotalNum() {
		return iaTotalNum;
	}

	public void setIaTotalNum(Integer iaTotalNum) {
		this.iaTotalNum = iaTotalNum;
	}

	public IssuingAuthority iaOrganisationalStructure(List<AtuItem> iaOrganisationalStructure) {
		this.iaOrganisationalStructure = iaOrganisationalStructure;
		return this;
	}

	public IssuingAuthority addIaOrganisationalStructureItem(AtuItem iaOrganisationalStructureItem) {
		if (this.iaOrganisationalStructure == null) {
			this.iaOrganisationalStructure = new ArrayList<AtuItem>();
		}
		this.iaOrganisationalStructure.add(iaOrganisationalStructureItem);
		return this;
	}

	/**
	 * Get iaOrganisationalStructure
	 * 
	 * @return iaOrganisationalStructure
	 **/
	@Schema(description = "")
	@Valid
	public List<AtuItem> getIaOrganisationalStructure() {
		return iaOrganisationalStructure;
	}

	public void setIaOrganisationalStructure(List<AtuItem> iaOrganisationalStructure) {
		this.iaOrganisationalStructure = iaOrganisationalStructure;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		IssuingAuthority issuingAuthority = (IssuingAuthority) o;
		return Objects.equals(this.evidenceTypeId, issuingAuthority.evidenceTypeId)
				&& Objects.equals(this.countryCode, issuingAuthority.countryCode)
				&& Objects.equals(this.iaLevelPath, issuingAuthority.iaLevelPath)
				&& Objects.equals(this.iaTotalNum, issuingAuthority.iaTotalNum)
				&& Objects.equals(this.iaOrganisationalStructure, issuingAuthority.iaOrganisationalStructure)
				&& super.equals(o);
	}

	@Override
	public int hashCode() {
		return Objects.hash(evidenceTypeId, countryCode, iaLevelPath, iaTotalNum, iaOrganisationalStructure,
				super.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("    evidenceTypeId: ").append(toIndentedString(evidenceTypeId)).append("\n");
		sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
		sb.append("    iaLevelPath: ").append(toIndentedString(iaLevelPath)).append("\n");
		sb.append("    iaTotalNum: ").append(toIndentedString(iaTotalNum)).append("\n");
		sb.append("    iaOrganisationalStructure: ").append(toIndentedString(iaOrganisationalStructure)).append("\n");
		return sb.toString();
	}
}
