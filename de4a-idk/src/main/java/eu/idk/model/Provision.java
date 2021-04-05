package eu.idk.model;

import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.IExplicitlyCloneable;
import com.helger.commons.string.ToStringGenerator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Provision", propOrder = { "Provision", "redirectURL", "Param" })
@CodingStyleguideUnaware
@Entity
public class Provision implements IExplicitlyCloneable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_provisionItem", nullable = false)
	private ProvisionItem provisionItem;
	
	@XmlElement(name = "provisionType", required = true)
	@XmlSchemaType(name = "string")
	@Column
	private String provisionType;

	@XmlElement(name = "RedirectURL")
	@XmlSchemaType(name = "anyURI")
	@Column
	private String redirectURL;

	@XmlElement(name = "Param")
	@OneToMany(mappedBy = "provision", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Param> params = null;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProvisionItem getProvisionItem() {
		return provisionItem;
	}

	public void setProvisionItem(ProvisionItem provisionItem) {
		this.provisionItem = provisionItem;
	}

	@Nullable
	public String getProvisionType() {
		return this.provisionType;
	}

	public void setProvisionType(@Nullable String value) {
		this.provisionType = value;
	}

	@Nullable
	public String getRedirectURL() {
		return this.redirectURL;
	}

	public void setRedirectURL(@Nullable String value) {
		this.redirectURL = value;
	}

	@Nullable
	public Set<Param> getParams() {
		return this.params;
	}

	public void setParams(@Nullable Set<Param> value) {
		this.params = value;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !getClass().equals(o.getClass()))
			return false;
		Provision rhs = (Provision) o;
		if (!EqualsHelper.equals(this.params, rhs.params))
			return false;
		if (!EqualsHelper.equals(this.provisionType, rhs.provisionType))
			return false;
		if (!EqualsHelper.equals(this.redirectURL, rhs.redirectURL))
			return false;
		return true;
	}

	public int hashCode() {
		return (new HashCodeGenerator(this)).append(this.params).append(this.provisionType).append(this.redirectURL)
				.getHashCode();
	}

	public String toString() {
		return (new ToStringGenerator(this)).append("Param", this.params).append("Provision", this.provisionType)
				.append("redirectURL", this.redirectURL).getToString();
	}

	public void cloneTo(@Nonnull Provision ret) {
		ret.params = (this.params == null) ? null : this.params;
		ret.provisionType = this.provisionType;
		ret.redirectURL = this.redirectURL;
	}

	@Nonnull
	@ReturnsMutableCopy
	public Provision clone() {
		Provision ret = new Provision();
		cloneTo(ret);
		return ret;
	}
}