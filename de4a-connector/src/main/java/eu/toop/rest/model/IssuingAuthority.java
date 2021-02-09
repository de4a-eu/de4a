package eu.toop.rest.model;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.validation.annotation.Validated;

/**
 * IssuingAuthority
 */
@Validated
public class IssuingAuthority implements Serializable {
	private static final long serialVersionUID = -3352737906437012L;
	
	@JsonProperty("canonicalEvidenceType")
	private String canonicalEvidenceType;

	@JsonProperty("countryCode")
	private String countryCode = null;

	@JsonProperty("iaLevelPath")
	private String iaLevelPath = null;

	@JsonProperty("iaTotalNum")
	private Integer iaTotalNum = null;

	@JsonProperty("iaOrganisationalStructure")
	private List<AtuItem> iaOrganisationalStructure = new ArrayList<>();

	
	public String getCcanonicalEvidenceType() {
		return canonicalEvidenceType;
	}

	public void setEvidenceTypeId(String canonicalEvidenceType) {
		this.canonicalEvidenceType = canonicalEvidenceType;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

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

	public Integer getIaTotalNum() {
		return iaTotalNum;
	}

	public void setIaTotalNum(Integer iaTotalNum) {
		this.iaTotalNum = iaTotalNum;
	}

	public IssuingAuthority iaOrganisationalStructure(ArrayList<AtuItem> iaOrganisationalStructure) {
		this.iaOrganisationalStructure = iaOrganisationalStructure;
		return this;
	}

	public IssuingAuthority addIaOrganisationalStructureItem(AtuItem iaOrganisationalStructureItem) {
		if (this.iaOrganisationalStructure == null) {
			this.iaOrganisationalStructure = new ArrayList<>();
		}
		this.iaOrganisationalStructure.add(iaOrganisationalStructureItem);
		return this;
	}

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
		return Objects.equals(this.canonicalEvidenceType, issuingAuthority.canonicalEvidenceType)
				&& Objects.equals(this.countryCode, issuingAuthority.countryCode)
				&& Objects.equals(this.iaLevelPath, issuingAuthority.iaLevelPath)
				&& Objects.equals(this.iaTotalNum, issuingAuthority.iaTotalNum)
				&& Objects.equals(this.iaOrganisationalStructure, issuingAuthority.iaOrganisationalStructure)
				&& super.equals(o);
	}

	@Override
	public int hashCode() {
		return Objects.hash(canonicalEvidenceType, countryCode, iaLevelPath, iaTotalNum, iaOrganisationalStructure,
				super.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("    canonicalEvidenceType: ").append(canonicalEvidenceType).append("\n");
		sb.append("    countryCode: ").append(countryCode).append("\n");
		sb.append("    iaLevelPath: ").append(iaLevelPath).append("\n");
		sb.append("    iaTotalNum: ").append(iaTotalNum).append("\n");
		sb.append("    iaOrganisationalStructure: ").append(iaOrganisationalStructure).append("\n");
		return sb.toString();
	}
}
