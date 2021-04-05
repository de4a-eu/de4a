package eu.idk.model;

import java.util.HashSet;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.IExplicitlyCloneable;
import com.helger.commons.string.ToStringGenerator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Param", propOrder = { "title", "paramsSet" })
@CodingStyleguideUnaware
@Entity
public class Param implements IExplicitlyCloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_provision", nullable = false)
	private Provision provision;
	
	@XmlElement(name = "Title", required = true)
	@Column
	private String title;

	@XmlElement(name = "ParamsSet", required = true)
	@OneToMany(mappedBy = "param", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ParamsSet> paramsSet = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	@Nullable
	public String getTitle() {
		return this.title;
	}

	public void setTitle(@Nullable String value) {
		this.title = value;
	}

	@Nullable
	public Set<ParamsSet> getParamsSet() {
		return this.paramsSet;
	}

	public void setParamsSet(@Nullable Set<ParamsSet> value) {
		this.paramsSet = value;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !getClass().equals(o.getClass()))
			return false;
		Param rhs = (Param) o;
		if (!EqualsHelper.equals(this.paramsSet, rhs.paramsSet))
			return false;
		if (!EqualsHelper.equals(this.title, rhs.title))
			return false;
		return true;
	}

	public int hashCode() {
		return (new HashCodeGenerator(this)).append(this.paramsSet).append(this.title).getHashCode();
	}

	public String toString() {
		return (new ToStringGenerator(this)).append("paramsSet", this.paramsSet).append("title", this.title)
				.getToString();
	}

	public void cloneTo(@Nonnull Param ret) {
		ret.paramsSet = (this.paramsSet == null) ? null : this.paramsSet;
		ret.title = this.title;
	}

	@Nonnull
	@ReturnsMutableCopy
	public Param clone() {
		Param ret = new Param();
		cloneTo(ret);
		return ret;
	}
}