package eu.de4a.evaluator.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.de4a.conn.api.requestor.AgentCVType;
import eu.de4a.conn.api.requestor.BirthNameType;
import eu.de4a.conn.api.requestor.CurrentFamilyNameType;
import eu.de4a.conn.api.requestor.CurrentGivenNameType;
import eu.de4a.conn.api.requestor.DataRequestSubjectCVType;
import eu.de4a.conn.api.requestor.EvidenceServiceDataType;
import eu.de4a.conn.api.requestor.EvidenceServiceType;
import eu.de4a.conn.api.requestor.ExplicitRequestType;
import eu.de4a.conn.api.requestor.LegalEntityIdentifierType;
import eu.de4a.conn.api.requestor.LegalNameType;
import eu.de4a.conn.api.requestor.NaturalPersonIdentifierType;
import eu.de4a.conn.api.requestor.RequestGroundsType;
import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.SMPUtils;

@Component
public class RequestBuilder { 
	@Value("${de4a.me.evaluator}")
	private String meId;
	@Value("${de4a.me.evaluator.name}")
	private String meName;
	@Value("#{'${de4a.requestor.participantID.jvm:${de4a.requestor.participantID:}}'}")
	private String requestorId;
	@Value("${de4a.specificationId}")
	private String specification;
	@Value("${de4a.request.grounds.link}")
	private String groundsLink;  
	@Value("${de4a.evidence.service.uri}")
	private String serviceUri;
	@Value("${de4a.return.service.id}")
	private String returnUrl;
	@Value("${de4a.procedure.id}")
	private String procedureId;
	public DataRequestSubjectCVType buildSubject(String eidasId,String birthDate,String name,String ap1,String fullName) {
		DataRequestSubjectCVType subject=new DataRequestSubjectCVType();
		NaturalPersonIdentifierType person=new NaturalPersonIdentifierType();
		person.setIdentifier(eidasId);
		if(birthDate!=null) {
			try {
				person.setDateOfBirth(gimmeGregorian(new SimpleDateFormat(DE4AConstants.BIRTH_DATE_PATTERN).parse(birthDate)));
			} catch (ParseException e) {
				//no worries
			}
		} 
		CurrentFamilyNameType ap1Type=new CurrentFamilyNameType();
		ap1Type.setValue(ap1);
		person.setFamilyName(ap1Type);
		CurrentGivenNameType nameType=new CurrentGivenNameType();
		nameType.setValue(name);
		person.setGivenName(nameType);
		BirthNameType  birthName=new BirthNameType();
		birthName.setValue(fullName);
		person.setBirthName(birthName); 
		subject.setDataSubjectPerson(person);
		return subject;
	}
	
	public DataRequestSubjectCVType buildSubjectLegal(String eidasId ) {
		DataRequestSubjectCVType subject=new DataRequestSubjectCVType();
		LegalEntityIdentifierType legal=new LegalEntityIdentifierType();
		legal.setLegalEntityIdentifier(eidasId);
		LegalNameType  legalName=new LegalNameType();
		legalName.setValue("Name of "+eidasId);
		legal.setLegalEntityName(legalName); 
		subject.setDataSubjectCompany(legal);
		return subject;
	}

	public RequestTransferEvidence buildRequest(String requestId, EvidenceServiceType evidenceServiceType,
			String eidasId, String birthDate, String name, String ap1, String fullName) {
		RequestTransferEvidence request = new RequestTransferEvidence();
		request.setDataEvaluator(buildAgent(meId, meName, returnUrl));
		request.setSpecificationId(specification);
		request.setDataOwner(buildAgent(evidenceServiceType.getDataOwner(), 
				evidenceServiceType.getDataOwner(),
				evidenceServiceType.getRedirectURL()));
		request.setProcedureId(procedureId);
		RequestGroundsType grounds = new RequestGroundsType();
		grounds.setExplicitRequest(ExplicitRequestType.SDGR_14);
		grounds.setLawELIPermanentLink(groundsLink);
		request.setRequestGrounds(grounds);
		request.setRequestId(requestId);
		request.setReturnServiceId(SMPUtils.getRequestorReturnService(
				evidenceServiceType.getService(), 
				requestorId));
		request.setTimeStamp(gimmeGregorian(Calendar.getInstance().getTime()));
		EvidenceServiceDataType evidenceSevice = new EvidenceServiceDataType();
		evidenceSevice.setEvidenceServiceURI(evidenceServiceType.getService());
		request.setEvidenceServiceData(evidenceSevice);
		request.setCanonicalEvidenceId(evidenceServiceType.getCanonicalEvidence());
		if (ap1 != null) {
			request.setDataRequestSubject(buildSubject(eidasId, birthDate, name, ap1, fullName));
		} else {
			request.setDataRequestSubject(buildSubjectLegal(eidasId));
		}

		return request;
	}
	
	private XMLGregorianCalendar gimmeGregorian(Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		XMLGregorianCalendar date2=null;
		try {
			date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c); 
			//date2.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		} catch (DatatypeConfigurationException e) {
		 //na tranqui;
		}
		return date2;
	}

	private AgentCVType buildAgent(String id, String name, String urlRedirect) {
		AgentCVType agent = new AgentCVType();
		agent.setId(id);
		agent.setName(name);
		agent.setUrlRedirect(urlRedirect);
		return agent;
	}
}
