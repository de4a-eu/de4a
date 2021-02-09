package eu.idk.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * AtuItem
 */
@Validated
@Entity
@Table(name = "AtuItem")
public class AtuItem extends InlineResponse200 {
	private static final long serialVersionUID = -8119060927437493496L;

	@Id
	@Column
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_issuingAuthority", nullable = false)
	@JsonIgnore
	private IssuingAuthority issuingAuthority;

	@JsonProperty("atuLevel")
	@Column
	private String atuLevel = null;

	@JsonProperty("atuPath")
	@Column
	private String atuPath = null;

	@JsonProperty("atuCode")
	@Column
	private String atuCode = null;

	@JsonProperty("atuName")
	@Column
	private String atuName = null;

	@JsonProperty("atuLatinName")
	@Column
	private String atuLatinName = null;

	public AtuItem atuLevel(String atuLevel) {
		this.atuLevel = atuLevel;
		return this;
	}

	/**
	 * Get atuLevel
	 * 
	 * @return atuLevel
	 **/
	@Schema(required = true, description = "")
	@NotNull

	@Valid
	public String getAtuLevel() {
		return atuLevel;
	}

	public void setAtuLevel(String atuLevel) {
		this.atuLevel = atuLevel;
	}

	public AtuItem atuPath(String atuPath) {
		this.atuPath = atuPath;
		return this;
	}

	/**
	 * Get atuPath
	 * 
	 * @return atuPath
	 **/
	@Schema(required = true, description = "")
	@NotNull

	public String getAtuPath() {
		return atuPath;
	}

	public void setAtuPath(String atuPath) {
		this.atuPath = atuPath;
	}

	public AtuItem atuCode(String atuCode) {
		this.atuCode = atuCode;
		return this;
	}

	/**
	 * Get atuCode
	 * 
	 * @return atuCode
	 **/
	@Schema(required = true, description = "")
	@NotNull

	public String getAtuCode() {
		return atuCode;
	}

	public void setAtuCode(String atuCode) {
		this.atuCode = atuCode;
	}

	public AtuItem atuName(String atuName) {
		this.atuName = atuName;
		return this;
	}

	/**
	 * Get atuName
	 * 
	 * @return atuName
	 **/
	@Schema(required = true, description = "")
	@NotNull

	public String getAtuName() {
		return atuName;
	}

	public void setAtuName(String atuName) {
		this.atuName = atuName;
	}

	public AtuItem atuLatinName(String atuLatinName) {
		this.atuLatinName = atuLatinName;
		return this;
	}

	/**
	 * Get atuLatinName
	 * 
	 * @return atuLatinName
	 **/
	@Schema(description = "")

	public String getAtuLatinName() {
		return atuLatinName;
	}

	public void setAtuLatinName(String atuLatinName) {
		this.atuLatinName = atuLatinName;
	}

	public IssuingAuthority getIssuingAuthority() {
		return issuingAuthority;
	}

	public void setIssuingAuthority(IssuingAuthority issuingAuthority) {
		this.issuingAuthority = issuingAuthority;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AtuItem atuItem = (AtuItem) o;
		return Objects.equals(this.atuLevel, atuItem.atuLevel) && Objects.equals(this.atuPath, atuItem.atuPath)
				&& Objects.equals(this.atuCode, atuItem.atuCode) && Objects.equals(this.atuName, atuItem.atuName)
				&& Objects.equals(this.atuLatinName, atuItem.atuLatinName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(atuLevel, atuPath, atuCode, atuName, atuLatinName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");

		sb.append("    atuLevel: ").append(toIndentedString(atuLevel)).append("\n");
		sb.append("    atuPath: ").append(toIndentedString(atuPath)).append("\n");
		sb.append("    atuCode: ").append(toIndentedString(atuCode)).append("\n");
		sb.append("    atuName: ").append(toIndentedString(atuName)).append("\n");
		sb.append("    atuLatinName: ").append(toIndentedString(atuLatinName)).append("\n");
		sb.append("}");
		return sb.toString();
	}
}
