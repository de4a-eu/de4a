package eu.idk.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParamSets", propOrder = { "paramSet" })
@CodingStyleguideUnaware
@Entity
public class ParamSets {

	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_param", nullable = false)
	private Param param;

	@XmlElement(name = "paramValue", required = true)
	@Column
	private String paramValue;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Param getParam() {
		return param;
	}

	public void setParam(Param param) {
		this.param = param;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !getClass().equals(o.getClass()))
			return false;
		ParamSets rhs = (ParamSets) o;
		return EqualsHelper.equalsCollection(this.paramValue, rhs.paramValue);
	}

	public int hashCode() {
		return (new HashCodeGenerator(this)).append(this.paramValue).getHashCode();
	}

	public String toString() {
		return (new ToStringGenerator(this)).append("paramSet", this.paramValue).getToString();
	}

	public void setParam(@Nullable String value) {
		this.paramValue = value;
	}

	public boolean hasParamSetEntries() {
		return !getParamValue().isEmpty();
	}

	public boolean hasNoParamSetEntries() {
		return getParamValue().isEmpty();
	}

	public void cloneTo(@Nonnull ParamSets ret) {
		if (this.paramValue == null) {
			ret.paramValue = null;
		} else {
			ret.paramValue = this.paramValue;
		}
	}
}
