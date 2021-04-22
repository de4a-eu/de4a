package eu.idk.model;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@XmlType(name = "Source", propOrder = { "countryCode", "atuLevel", "numProvisions", "organisation", "provisionsItem" })
@CodingStyleguideUnaware
@Entity
@Table(name = "Source")
public class Source {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@XmlElement(name = "canonicalEvidenceTypeId")
	@Column
	private String canonicalEvidenceTypeId;

	@XmlElement(name = "CountryCode")
	@Column
	private String countryCode;

	@XmlElement(name = "AtuLevel")
	@XmlSchemaType(name = "string")
	@Column
	private String atuLevel;

	@XmlElement(name = "NumProvisions")
	@XmlSchemaType(name = "integer")
	@Column
	private Integer numProvisions;

	@XmlElement(name = "Organisation")
	@Column
	private String organisation;

	@XmlElement(name = "ProvisionsItem")
	@OneToMany(mappedBy = "source", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProvisionItem> provisionItems;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Nonnull
	public String getCanonicalEvidenceTypeId() {
		return this.canonicalEvidenceTypeId;
	}

	public void setCanonicalEvidenceTypeId(String value) {
		this.canonicalEvidenceTypeId = value;
	}

	@Nullable
	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(@Nullable String value) {
		this.countryCode = value;
	}

	@Nullable
	public String getAtuLevel() {
		return this.atuLevel;
	}

	public void setAtuLevel(@Nullable String value) {
		this.atuLevel = value;
	}

	@Nullable
	public Integer getNumProvisions() {
		return this.numProvisions;
	}

	public void setNumProvisions(@Nullable Integer value) {
		this.numProvisions = value;
	}

	@Nullable
	public String getOrganisation() {
		return this.organisation;
	}

	public void setOrganisation(@Nullable String value) {
		this.organisation = value;
	}

	@Nullable
	public List<ProvisionItem> getProvisionItems() {
		return this.provisionItems;
	}

	public void setProvisionsItem(@Nullable List<ProvisionItem> value) {
		this.provisionItems = value;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !getClass().equals(o.getClass()))
			return false;
		Source rhs = (Source) o;
		if (!EqualsHelper.equals(this.atuLevel, rhs.atuLevel))
			return false;
		if (!EqualsHelper.equals(this.countryCode, rhs.countryCode))
			return false;
		if (!EqualsHelper.equals(this.numProvisions, rhs.numProvisions))
			return false;
		if (!EqualsHelper.equals(this.organisation, rhs.organisation))
			return false;
		return EqualsHelper.equals(this.provisionItems, rhs.provisionItems);
	}

	public int hashCode() {
		return (new HashCodeGenerator(this)).append(this.atuLevel).append(this.countryCode)
				.append(this.numProvisions).append(this.organisation).append(this.provisionItems).getHashCode();
	}

	public String toString() {
		return (new ToStringGenerator(this)).append("atuLevel", this.atuLevel)
				.append("countryCode", this.countryCode).append("numProvisions", this.numProvisions)
				.append("organisation", this.organisation).append("provisionsItem", this.provisionItems).getToString();
	}

	public void cloneTo(@Nonnull Source ret) {
		ret.atuLevel = this.atuLevel;
		ret.countryCode = this.countryCode;
		ret.numProvisions = this.numProvisions;
		ret.organisation = this.organisation;
		ret.provisionItems = (this.provisionItems == null) ? null : this.provisionItems;
	}
}
