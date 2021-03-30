package eu.de4a.util;

import java.util.Arrays;

import org.springframework.lang.NonNull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;

import eu.de4a.iem.jaxb.common.types.CanonicalEvidenceidkType;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;

public enum XDE4ACanonicalEvidenceType implements IDE4ACanonicalEvidenceType {

	T42_COMPANY_INFO_V04 ("T4.2 Company Info v0.4", CanonicalEvidenceidkType.COMPANY_REGISTRATION, eu.de4a.iem.xml.de4a.t42.v0_4.CT42.getAllXSDs ()),
	T42_COMPANY_INFO_V05 ("T4.2 Company Info v0.5", CanonicalEvidenceidkType.COMPANY_REGISTRATION, eu.de4a.iem.xml.de4a.t42.v0_5.CT42.getAllXSDs ()),
	T42_COMPANY_INFO_V06 ("T4.2 Company Info v0.6", CanonicalEvidenceidkType.COMPANY_REGISTRATION, eu.de4a.iem.xml.de4a.t42.v0_6.CT42.getAllXSDs ()),
	BIRTH_CERTIFICATE("Birth Certificate", CanonicalEvidenceidkType.BIRTH_CERTIFICATE, eu.de4a.util.DE4AConstants.getBirthCertificateXSDs());

	private final String m_sDisplayName;
	private final ICommonsList<ClassPathResource> m_aXSDs;
	private final CanonicalEvidenceidkType m_canonicalEvidenceIdType;

	XDE4ACanonicalEvidenceType (@Nonempty final String sDisplayName, @Nonempty CanonicalEvidenceidkType canonicalEvidenceIdType, 
			final ICommonsList <ClassPathResource> aXSDs)
	  {
	    m_sDisplayName = sDisplayName;
	    m_aXSDs = aXSDs;
	    m_canonicalEvidenceIdType = canonicalEvidenceIdType;
	  }

	@Nonempty
	public String getDisplayName() {
		return m_sDisplayName;
	}

	@ReturnsMutableCopy
	public ICommonsList<? extends ClassPathResource> getAllXSDs() {
		return m_aXSDs;
	}
	
	public CanonicalEvidenceidkType getCanonicalEvidenceIdType() {
		return m_canonicalEvidenceIdType;
	}
	
	public static XDE4ACanonicalEvidenceType getXDE4CanonicalEvidenceType(CanonicalEvidenceidkType canonicalEvidenceIdType) {
		return Arrays.asList(values()).stream().filter(x ->
			x.getCanonicalEvidenceIdType().equals(canonicalEvidenceIdType)).findFirst().orElse(null);
	}
	
	public static XDE4ACanonicalEvidenceType getXDE4CanonicalEvidenceType(@NonNull String canonicalEvidenceId) {
		return getXDE4CanonicalEvidenceType(CanonicalEvidenceidkType.fromValue(canonicalEvidenceId));
	}

}
