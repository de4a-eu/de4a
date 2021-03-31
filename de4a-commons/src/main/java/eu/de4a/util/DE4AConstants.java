package eu.de4a.util;


import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.xsds.xml.CXML_XSD;

import eu.de4a.conn.api.canonical.ObjectFactory;
import eu.de4a.iem.xml.de4a.CDE4AJAXB;

public class DE4AConstants {
	public static final String ID_MIMEPART="id";

	public static final String TAG_EVIDENCE_REQUEST="RequestTransferEvidence"; 
	public static final String TAG_EXTRACT_EVIDENCE_REQUEST="RequestExtractEvidence"; 
	public static final String TAG_EVIDENCE_RESPONSE="ResponseTransferEvidence"; 
	public static final String TAG_FORWARD_EVIDENCE_REQUEST="RequestForwardEvidence"; 
	public static final String TAG_EXTRACT_EVIDENCE_RESPONSE="ResponseExtractEvidence";
	public static final String XPATH_EVIDENCE_RESPONSE="//*[local-name()='ResponseTransferEvidence']";
	public static final String XPATH_EXTRACT_EVIDENCE_RESPONSE="//*[local-name()='ResponseExtractEvidence']";
	public static final String TAG_NATIONAL_EVIDENCE_RESPONSE="NationalEvidenceResponse"; 
	public static final String XPATH_REQUEST_ID="//*[local-name()='%s']/*[local-name()='RequestId']/text()"; 
	public static final String XPATH_ID="//*[local-name()='RequestId']";
	public static final String XPATH_EVIDENCE_DATA="//*[local-name()='EvidenceData']";
	public static final String XPATH_EVALUATOR_ID="//*[local-name()='DataEvaluator']/*[local-name()='AgentUrn']/text()";
	public static final String XPATH_EVALUATOR_NAME="//*[local-name()='DataEvaluator']/*[local-name()='AgentName']/text()";
	public static final String XPATH_EVALUATOR_ID_NODE="//*[local-name()='DataEvaluator']/*[local-name()='id']";
	public static final String XPATH_EVALUATOR_NAME_NODE="//*[local-name()='DataEvaluator']/*[local-name()='name']";
	public static final String XPATH_OWNER_ID="//*[local-name()='DataOwner']/*[local-name()='AgentUrn']/text()";
	public static final String XPATH_OWNER_NAME="//*[local-name()='DataOwner']/*[local-name()='Agent']/text()";
	public static final String XPATH_OWNER_ID_NODE="//*[local-name()='DataOwner']/*[local-name()='id']";
	public static final String XPATH_OWNER_NAME_NODE="//*[local-name()='DataOwner']/*[local-name()='name']";
	public static final String XPATH_CANONICAL_EVICENCE_ID="//*[local-name()='CanonicalEvidenceTypeId']/text()";
	public static final String XPATH_CANONICAL_EVICENCE="//*[local-name()='CanonicalEvidence']";
	public static final String XPATH_RETURN_SERVICE_ID="//*[local-name()='ReturnServiceId']";
	public static final String XPATH_SERVICE_URI="//*[local-name()='EvidenceServiceURI']";
	
	public static final String XPATH_EIDAS_DOC="//*[local-name()='PersonIdentifier']/text()";  
	public static final String XPATH_EIDAS_SURNAME="//*[local-name()='FamilyName']/text()";   
	public static final String XPATH_EIDAS_NAME="//*[local-name()='FirstName']/text()";    
	public static final String XPATH_EIDAS_FULLNAME="//*[local-name()='BirthName']/text()";   
	
	public static final String XPATH_EIDAS_BIRTHDATE="//*[local-name()='DateOfBirth']/text()"; 
	

	public static final String XPATH_EIDAS_DOC_NODE="//*[local-name()='PersonID']";  
	public static final String XPATH_EIDAS_SURNAME_NODE="//*[local-name()='PersonFamilyName']";   
	public static final String XPATH_EIDAS_NAME_NODE="//*[local-name()='PersonGivenName']";   
	public static final String XPATH_EIDAS_FULLNAME_NODE="//*[local-name()='PersonBirthName']";   
	public static final String XPATH_EIDAS_BIRTHDATE_NODE="//*[local-name()='PersonBirthDate']"; 
	
	//SMP request MessageType
	public static final String MESSAGE_TYPE_REQUEST = "request";
	
	//SMP response MessageType
	public static final String MESSAGE_TYPE_RESPONSE = "response";
	
	public static final String DOUBLE_SEPARATOR = "::";
	
	public static final String SERVICES_PATH = "/services/";
	
	// URN Scheme for SMP identifiers
	public static final String URN_SCHEME = "urn:de4a-eu:";
	
	public static final String CANONICAL_EVIDENCE_TYPE = "CanonicalEvidenceType";
	
	public static final String BIRTH_DATE_PATTERN="yyyy-MM-dd";
	
	public static final ClassPathResource XSD_BIRTH_CERTIFICATE = new ClassPathResource ("xsds/canonicalEvidences/BirthCertificate.xsd", 
			DE4AConstants.class.getClassLoader());
	
	public static ICommonsList <ClassPathResource> getBirthCertificateXSDs ()
	  {
	    final ICommonsList <ClassPathResource> a = new CommonsArrayList <> ();
	    a.add (CXML_XSD.getXSDResource ());
	    a.addAll (CDE4AJAXB.XSDS);
	    a.add (XSD_BIRTH_CERTIFICATE);
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
