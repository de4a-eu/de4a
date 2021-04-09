package eu.de4a.conn.api.canonical;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.xsds.xml.CXML_XSD;

import eu.de4a.iem.xml.de4a.CDE4AJAXB;

public enum EvidencesResources {

	BIRTH_CERTIFICATE(new ClassPathResource("xsds/canonicalEvidences/BirthCertificate.xsd",
			EvidencesResources.class.getClassLoader()));

	private ClassPathResource classPathResource;

	EvidencesResources(ClassPathResource resource) {
		this.classPathResource = resource;
	}

	public ClassPathResource getClassPathResource() {
		return this.classPathResource;
	}

	public ICommonsList<ClassPathResource> getXSDs() {
		final ICommonsList<ClassPathResource> a = new CommonsArrayList<>();
		a.add(CXML_XSD.getXSDResource());
		a.addAll(CDE4AJAXB.XSDS);
		a.add(this.classPathResource);
		return a;
	}
	
	public static final Class <?> [] aClasses = new Class <?> [] { com.helger.xsds.ccts.cct.schemamodule.ObjectFactory.class,
        com.helger.xsds.xlink.ObjectFactory.class,
        eu.de4a.iem.jaxb.common.idtypes.ObjectFactory.class,
        eu.de4a.iem.jaxb.common.types.ObjectFactory.class,
        eu.de4a.iem.jaxb.cv.dt.ObjectFactory.class,
        eu.de4a.iem.jaxb.de_usi.ObjectFactory.class,
        eu.de4a.iem.jaxb.do_im.ObjectFactory.class,
        eu.de4a.iem.jaxb.do_usi.ObjectFactory.class,
        eu.de4a.iem.jaxb.dr_im.ObjectFactory.class,
        eu.de4a.iem.jaxb.dr_usi.ObjectFactory.class,
        eu.de4a.iem.jaxb.do_usi.ObjectFactory.class,
        eu.de4a.iem.jaxb.eidas.lp.ObjectFactory.class,
        eu.de4a.iem.jaxb.eidas.np.ObjectFactory.class,
        eu.de4a.iem.jaxb.idk.ObjectFactory.class,
        eu.de4a.iem.jaxb.w3.cv.bc.ObjectFactory.class,
        ObjectFactory.class};
}
