package eu.de4a.util;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;

import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;

import eu.de4a.conn.api.canonical.EvidencesResources;

public enum XDE4ACanonicalEvidenceType implements IDE4ACanonicalEvidenceType {

	BIRTH_CERTIFICATE ("Birth certificate", EvidencesResources.BIRTH_CERTIFICATE.getXSDs());

	private final String m_sDisplayName;
	private final ICommonsList<ClassPathResource> m_aXSDs;

	XDE4ACanonicalEvidenceType(@Nonempty final String sDisplayName, final ICommonsList<ClassPathResource> aXSDs) {
		m_sDisplayName = sDisplayName;
		m_aXSDs = aXSDs;
	}

	@Override
	public String getDisplayName() {
		return m_sDisplayName;
	}

	@Override
	public ICommonsList<? extends ClassPathResource> getAllXSDs() {
		return m_aXSDs;
	}
}
