package eu.de4a.evaluator.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
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
import eu.de4a.conn.api.requestor.ExplicitRequestType;
import eu.de4a.conn.api.requestor.NaturalPersonIdentifierType;
import eu.de4a.conn.api.requestor.RequestGroundsType;
import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.util.DE4AConstants;

@Component
public class RequestBuilder { 
	@Value("${de4a.me.evaluator}")
	private String meId;
	@Value("${de4a.me.evaluator.name}")
	private String meName;
	@Value("${de4a.specificationId}")
	private String specification;
	@Value("${de4a.request.grounds.link}")
	private String groundsLink; 
	@Value("${de4a.canonical.evidence.id}")
	private String canonicalId;
	@Value("${de4a.evidence.service.uri}")
	private String serviceUri;
	@Value("${de4a.return.service.id}")
	private String returnService;
	@Value("${de4a.procedure.id}")
	private String procedureId;
	public DataRequestSubjectCVType buildSubject(String eidasId,String birthDate,String name,String ap1,String fullName) {
		DataRequestSubjectCVType subject=new DataRequestSubjectCVType();
		NaturalPersonIdentifierType person=new NaturalPersonIdentifierType();
		person.setIdentifier(eidasId);
		try {
			person.setDateOfBirth(gimmeGregorian(new SimpleDateFormat(DE4AConstants.BIRTH_DATE_PATTERN).parse(birthDate)));
		} catch (ParseException e) {
			//no worries
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
	public RequestTransferEvidence buildRequest(String requestId,String eidasId,String birthDate,String name,String ap1,String fullName) {
		RequestTransferEvidence request = new RequestTransferEvidence();
		request.setDataEvaluator(buildAgent(meId, meName));
		request.setSpecificationId(specification);
		request.setDataOwner(buildAgent("fakeId", "fakeName"));
		request.setProcedureId(procedureId);
		RequestGroundsType grounds=new RequestGroundsType();
		grounds.setExplicitRequest(ExplicitRequestType.SDGR_14);
		grounds.setLawELIPermanentLink(groundsLink);
		request.setRequestGrounds(grounds);
		request.setRequestId(requestId);
		request.setReturnServiceId(returnService);
		request.setTimeStamp(gimmeGregorian(Calendar.getInstance().getTime()));
		EvidenceServiceDataType evidenceSevice=new EvidenceServiceDataType();
		evidenceSevice.setEvidenceServiceURI(serviceUri);
		request.setEvidenceServiceData(evidenceSevice);
		request.setDataRequestSubject(buildSubject(eidasId, birthDate, name, ap1, fullName));
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
	private AgentCVType buildAgent(String id,String name) {
		AgentCVType agent=new AgentCVType();
		agent.setId(id);
		agent.setName(name);
		return agent;
	}
}
