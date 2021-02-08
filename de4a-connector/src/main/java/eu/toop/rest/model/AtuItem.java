package eu.toop.rest.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

/**
 * AtuItem
 */
@Validated
public class AtuItem implements Serializable {
	private static final long serialVersionUID = -8119060927437493496L;

	@JsonProperty("atuLevel")
	private String atuLevel = null;

	@JsonProperty("atuPath")
	private String atuPath = null;

	@JsonProperty("atuCode")
	private String atuCode = null;

	@JsonProperty("atuName")
	private String atuName = null;

	@JsonProperty("atuLatinName")
	private String atuLatinName = null;



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

	public String getAtuLatinName() {
		return atuLatinName;
	}

	public void setAtuLatinName(String atuLatinName) {
		this.atuLatinName = atuLatinName;
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

		sb.append("    atuLevel: ").append(atuLevel).append("\n");
		sb.append("    atuPath: ").append(atuPath).append("\n");
		sb.append("    atuCode: ").append(atuCode).append("\n");
		sb.append("    atuName: ").append(atuName).append("\n");
		sb.append("    atuLatinName: ").append(atuLatinName).append("\n");
		sb.append("}");
		return sb.toString();
	}
}
