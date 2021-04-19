package eu.de4a.evaluator.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.de4a.iem.jaxb.common.idtypes.LegalPersonIdentifierType;
import eu.de4a.iem.jaxb.common.idtypes.NaturalPersonIdentifierType;
import eu.de4a.iem.jaxb.common.types.AgentType;
import eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType;
import eu.de4a.iem.jaxb.common.types.RequestGroundsType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.eidas.lp.LegalNameType;
import eu.de4a.iem.jaxb.eidas.np.BirthNameType;
import eu.de4a.iem.jaxb.eidas.np.CurrentFamilyNameType;
import eu.de4a.iem.jaxb.eidas.np.CurrentGivenNameType;
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
	@Value("${de4a.return.service.id}")
	private String returnUrl;
	@Value("${de4a.procedure.id}")
	private String procedureId;

	public DataRequestSubjectCVType buildSubject(String eidasId, String birthDate, String name, String ap1,
			String fullName) {
		DataRequestSubjectCVType subject = new DataRequestSubjectCVType();
		NaturalPersonIdentifierType person = new NaturalPersonIdentifierType();
		person.setPersonIdentifier(eidasId);
		if (birthDate != null) {
			try {
				person.setDateOfBirth(
						gimmeLocalDate(new SimpleDateFormat(DE4AConstants.BIRTH_DATE_PATTERN).parse(birthDate)));
			} catch (ParseException e) {
				// no worries
			}
		}
		CurrentFamilyNameType ap1Type = new CurrentFamilyNameType();
		ap1Type.setValue(ap1);
		person.setFamilyName(ap1Type);
		CurrentGivenNameType nameType = new CurrentGivenNameType();
		nameType.setValue(name);
		person.setFirstName(nameType);
		BirthNameType birthName = new BirthNameType();
		birthName.setValue(fullName);
		person.setBirthName(birthName);
		subject.setDataSubjectPerson(person);
		return subject;
	}

	public DataRequestSubjectCVType buildSubjectLegal(String eidasId) {
		DataRequestSubjectCVType subject = new DataRequestSubjectCVType();
		LegalPersonIdentifierType legal = new LegalPersonIdentifierType();
		legal.setLegalPersonIdentifier(eidasId);
		LegalNameType legalName = new LegalNameType();
		legalName.setValue("Name of " + eidasId);
		legal.setLegalName(legalName);
		subject.setDataSubjectCompany(legal);
		return subject;
	}

	public RequestTransferEvidenceUSIIMDRType buildRequest(String requestId, AgentType dataOwner, String canonicalEvidenceTypeId, 
			String eidasId, String birthDate, 
			String name, String ap1, String fullName) {
		RequestTransferEvidenceUSIIMDRType request = new RequestTransferEvidenceUSIIMDRType();
		request.setDataEvaluator(buildAgent(meId, meName, returnUrl));
		request.setSpecificationId(specification);
		request.setDataOwner(dataOwner);
		request.setProcedureId(procedureId);
		RequestGroundsType grounds = new RequestGroundsType();
		grounds.setLawELIPermanentLink(groundsLink);
		request.setRequestGrounds(grounds);
		request.setRequestId(requestId);
		request.setTimeStamp(gimmeLocalDateTime(Calendar.getInstance().getTime()));
		StringBuilder canonicalEvidenceId = new StringBuilder(DE4AConstants.URN_SCHEME)
		        .append(DE4AConstants.CANONICAL_EVIDENCE_TYPE)
		        .append(DE4AConstants.DOUBLE_SEPARATOR)
		        .append(canonicalEvidenceTypeId);
		request.setCanonicalEvidenceTypeId(canonicalEvidenceId.toString());
		if (ap1 != null) {
			request.setDataRequestSubject(buildSubject(eidasId, birthDate, name, ap1, fullName));
		} else {
			request.setDataRequestSubject(buildSubjectLegal(eidasId));
		}

		return request;
	}

	private LocalDate gimmeLocalDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private LocalDateTime gimmeLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	private AgentType buildAgent(String id, String name, String urlRedirect) {
		AgentType agent = new AgentType();
		agent.setAgentUrn(id);
		agent.setAgentName(name);
		agent.setRedirectURL(urlRedirect);
		return agent;
	}
}
