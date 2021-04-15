package eu.idk.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionItem", propOrder = { "atuCode", "atuLatinName", "dataOwnerId", "dataOwnerPrefLabel",
		"provision" })
@CodingStyleguideUnaware
@Entity
public class ProvisionItem {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_source", nullable = false)
	private Source source;
	
	@XmlElement(name = "AtuCode")
	@Column
	private String atuCode;

	@XmlElement(name = "AtuLatinName")
	@Column
	private String atuLatinName;

	@XmlElement(name = "DataOwnerId")
	@XmlSchemaType(name = "anyURI")
	@Column
	private String dataOwnerId;

	@XmlElement(name = "DataOwnerPrefLabel")
	@Column
	private String dataOwnerPrefLabel;

	@XmlElement(name = "Provision", required = true)
	@OneToOne(mappedBy = "provisionItem", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id_provisionItem", nullable = false)
	private Provision provision;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	@Nullable
	public String getAtuCode() {
		return this.atuCode;
	}

	public void setAtuCode(@Nullable String value) {
		this.atuCode = value;
	}

	@Nullable
	public String getAtuLatinName() {
		return this.atuLatinName;
	}

	public void setAtuLatinName(@Nullable String value) {
		this.atuLatinName = value;
	}

	@Nullable
	public String getDataOwnerId() {
		return this.dataOwnerId;
	}

	public void setDataOwnerId(@Nullable String value) {
		this.dataOwnerId = value;
	}

	@Nullable
	public String getDataOwnerPrefLabel() {
		return this.dataOwnerPrefLabel;
	}

	public void setDataOwnerPrefLabel(@Nullable String value) {
		this.dataOwnerPrefLabel = value;
	}

	@Nullable
	public Provision getProvision() {
		return this.provision;
	}

	public void setProvision(@Nullable Provision value) {
		this.provision = value;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !getClass().equals(o.getClass()))
			return false;
		ProvisionItem rhs = (ProvisionItem) o;
		if (!EqualsHelper.equals(this.atuCode, rhs.atuCode))
			return false;
		if (!EqualsHelper.equals(this.atuLatinName, rhs.atuLatinName))
			return false;
		if (!EqualsHelper.equals(this.dataOwnerId, rhs.dataOwnerId))
			return false;
		if (!EqualsHelper.equals(this.dataOwnerPrefLabel, rhs.dataOwnerPrefLabel))
			return false;
		if (!EqualsHelper.equals(this.provision, rhs.provision))
			return false;
		return true;
	}

	public int hashCode() {
		return (new HashCodeGenerator(this)).append(this.atuCode).append(this.atuLatinName).append(this.dataOwnerId)
				.append(this.dataOwnerPrefLabel).append(this.provision).getHashCode();
	}

	public String toString() {
		return (new ToStringGenerator(this)).append("atuCode", this.atuCode).append("atuLatinName", this.atuLatinName)
				.append("dataOwnerId", this.dataOwnerId).append("dataOwnerPrefLabel", this.dataOwnerPrefLabel)
				.append("provision", this.provision).getToString();
	}

	public void cloneTo(@Nonnull ProvisionItem ret) {
		ret.atuCode = this.atuCode;
		ret.atuLatinName = this.atuLatinName;
		ret.dataOwnerId = this.dataOwnerId;
		ret.dataOwnerPrefLabel = this.dataOwnerPrefLabel;
		ret.provision = (this.provision == null) ? null : new Provision(provision);
	}
}