package eu.de4a.connector.xml;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;


import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.XMLOffsetDateTime;
import com.helger.commons.math.MathHelper;
import com.helger.dcng.api.DcngIdentifierFactory;

import eu.de4a.iem.core.jaxb.common.AgentType;
import eu.de4a.iem.core.jaxb.common.DataRequestSubjectCVType;
import eu.de4a.iem.core.jaxb.common.EventNotificationItemType;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.EventSubscripRequestItemType;
import eu.de4a.iem.core.jaxb.common.ExplicitRequestType;
import eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType;
import eu.de4a.iem.core.jaxb.common.NaturalPersonIdentifierType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceLUItemType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceUSIItemType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceLUType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;
import eu.de4a.iem.core.jaxb.common.RequestGroundsType;
import eu.de4a.iem.core.jaxb.common.TimePeriodType;
import eu.de4a.iem.core.jaxb.eidas.np.GenderType;

/**
 * Helper class for generating RANDOM messages based on it2 schemas
 * final messages and sub-elements generation methods available
 *
 */
public class MessagesHelper {

    public static RequestExtractMultiEvidenceIMType createRequestExtractMultiEvidenceIM(int nItems) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RequestExtractMultiEvidenceIMType ret = new RequestExtractMultiEvidenceIMType();

        fillRequestExtractMultiEvidenceType(ret, aTLR);
        
        RequestEvidenceItemType item = new RequestEvidenceItemType();
        fillRequestEvidenceItemType(item, aTLR);

        ret.addRequestEvidenceIMItem(item);
        
        IntStream.range(1, nItems).forEach(i -> {
            RequestEvidenceItemType newItem = item.clone();
            newItem.setRequestItemId(UUID.randomUUID ().toString ());
            ret.addRequestEvidenceIMItem(newItem);
        });        
        
        return ret;
    }
    
    public static RequestExtractMultiEvidenceUSIType createRequestExtractMultiEvidenceUSI(int nItems) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RequestExtractMultiEvidenceUSIType ret = new RequestExtractMultiEvidenceUSIType();

        fillRequestExtractMultiEvidenceType(ret, aTLR);
        
        RequestEvidenceUSIItemType item = new RequestEvidenceUSIItemType();
        fillRequestEvidenceItemType(item, aTLR);
        item.setDataEvaluatorURL("http://localhost:8080/");

        ret.addRequestEvidenceUSIItem(item);
        
        IntStream.range(1, nItems).forEach(i -> {
            RequestEvidenceUSIItemType newItem = item.clone();
            newItem.setRequestItemId(UUID.randomUUID ().toString ());
            newItem.setDataEvaluatorURL("http://localhost:8080/");
            ret.addRequestEvidenceUSIItem(newItem);
        });        
        
        return ret;
    }
    
    public static RequestExtractMultiEvidenceLUType createRequestExtractMultiEvidenceLU(int nItems) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RequestExtractMultiEvidenceLUType ret = new RequestExtractMultiEvidenceLUType();

        fillRequestExtractMultiEvidenceType(ret, aTLR);
        
        RequestEvidenceLUItemType item = new RequestEvidenceLUItemType();
        fillRequestEvidenceItemType(item, aTLR);
        item.setEventNotificationRef(UUID.randomUUID ().toString ());

        ret.addRequestEvidenceLUItem(item);
        
        IntStream.range(1, nItems).forEach(i -> {
            RequestEvidenceLUItemType newItem = item.clone();
            newItem.setRequestItemId(UUID.randomUUID ().toString ());
            newItem.setEventNotificationRef(UUID.randomUUID ().toString ());
            ret.addRequestEvidenceLUItem(newItem);
        });        
        
        return ret;
    }
    
    public static RequestEventSubscriptionType createRequestEventSubscription(int nItems) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RequestEventSubscriptionType ret = new RequestEventSubscriptionType();

        fillRequestExtractMultiEvidenceType(ret, aTLR);
        
        EventSubscripRequestItemType item = new EventSubscripRequestItemType();
        fillRequestEventSubscriptionItemType(item);

        ret.addEventSubscripRequestItem(item);
        
        IntStream.range(1, nItems).forEach(i -> {
            EventSubscripRequestItemType newItem = item.clone();
            newItem.setRequestItemId(UUID.randomUUID ().toString ());
            ret.addEventSubscripRequestItem(newItem);
        });        
        
        return ret;
    }
    
    public static EventNotificationType createRequestEventNotification(int nItems) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final EventNotificationType ret = new EventNotificationType();

        fillEventNotificationType(ret, aTLR);
        
        EventNotificationItemType item = new EventNotificationItemType();
        fillEventNotificationItemType(item);

        ret.addEventNotificationItem(item);
        
        IntStream.range(1, nItems).forEach(i -> {
            EventNotificationItemType newItem = item.clone();
            newItem.setEventId(UUID.randomUUID ().toString ());
            ret.addEventNotificationItem(newItem);
        });        
        
        return ret;
    }
    
    public static RedirectUserType createRedirectUser() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RedirectUserType ret = new RedirectUserType();

        ret.setRequestId(UUID.randomUUID ().toString ());
        ret.setDataEvaluator(_createAgent ());
        ret.setDataOwner(_createAgent ());
        ret.setCanonicalEvidenceTypeId("CanonicalEvidence-" + MathHelper.abs (aTLR.nextInt ()));
        ret.setRedirectUrl("http://localhost:8080/redirect");
        ret.setSpecificationId("Specification-" + MathHelper.abs (aTLR.nextInt ()));
        ret.setTimeStamp(PDTFactory.getCurrentLocalDateTime ());
        
        return ret;
    }
    
    private static void fillRequestExtractMultiEvidenceType(RequestExtractMultiEvidenceType req,
            ThreadLocalRandom aTLR) {        
        req.setRequestId (UUID.randomUUID ().toString ());
        req.setSpecificationId ("Specification-" + MathHelper.abs (aTLR.nextInt ()));
        req.setTimeStamp (PDTFactory.getCurrentLocalDateTime ());
        req.setProcedureId ("Procedure-" + MathHelper.abs (aTLR.nextInt ()));
        req.setDataEvaluator (_createAgent ());
        req.setDataOwner (_createAgent ());
    }
    
    private static void fillEventNotificationType(EventNotificationType req,
            ThreadLocalRandom aTLR) {        
        req.setNotificationId (UUID.randomUUID ().toString ());
        req.setSpecificationId ("Specification-" + MathHelper.abs (aTLR.nextInt ()));
        req.setTimeStamp (PDTFactory.getCurrentLocalDateTime ());
        req.setDataEvaluator (_createAgent ());
        req.setDataOwner (_createAgent ());
        req.setTimeStamp(XMLOffsetDateTime.now());
    }
    
    private static void fillRequestEvidenceItemType(RequestEvidenceItemType item,
            ThreadLocalRandom aTLR) {
        item.setRequestItemId(UUID.randomUUID ().toString ());
        item.setDataRequestSubject (_createDRS ());
        item.setRequestGrounds (_createRequestGrounds ());
        item.setCanonicalEvidenceTypeId ("CanonicalEvidence-" + MathHelper.abs (aTLR.nextInt ()));
    }
    
    private static void fillRequestEventSubscriptionItemType(EventSubscripRequestItemType item) {
        item.setRequestItemId(UUID.randomUUID ().toString ());
        item.setDataRequestSubject (_createDRS ());
        item.setDataRequestSubject(_createDRS ());
        item.setSubscriptionPeriod(_createTimePeriod(2));
        item.setCanonicalEventCatalogUri("BusinessEvents");
    }
    
    private static void fillEventNotificationItemType(EventNotificationItemType item) {
        item.setEventId(UUID.randomUUID ().toString ());
        item.setNotificationItemId(UUID.randomUUID ().toString ());
        item.setEventSubject (_createDRS ());
        item.setEventDate(XMLOffsetDateTime.now());
        item.setCanonicalEventCatalogUri("BusinessEvents");
    }

    @Nonnull
    private static AgentType _createAgent() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final AgentType ret = new AgentType();
        ret.setAgentUrn(DcngIdentifierFactory.PARTICIPANT_SCHEME + "::" + MathHelper.abs(aTLR.nextInt()));
        ret.setAgentName("Maxi Musterfrau " + MathHelper.abs(aTLR.nextInt()));
        return ret;
    }

    @Nonnull
    static <T> T random(@Nonnull final T[] a) {
        return a[ThreadLocalRandom.current().nextInt(a.length)];
    }

    @Nonnull
    private static NaturalPersonIdentifierType _createNP() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final NaturalPersonIdentifierType ret = new NaturalPersonIdentifierType();
        ret.setPersonIdentifier("ID-" + MathHelper.abs(aTLR.nextInt()));
        ret.setFirstName("FirstName-" + MathHelper.abs(aTLR.nextInt()));
        ret.setFamilyName("FamilyName-" + MathHelper.abs(aTLR.nextInt()));
        ret.setDateOfBirth(PDTFactory.getCurrentLocalDate().minusYears(18 + aTLR.nextInt(50)));
        ret.setGender(random(GenderType.values()));
        // Ignore the optional stuff
        return ret;
    }

    @Nonnull
    private static LegalPersonIdentifierType _createLP() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final LegalPersonIdentifierType ret = new LegalPersonIdentifierType();
        ret.setLegalPersonIdentifier("LPI-ID-" + MathHelper.abs(aTLR.nextInt()));
        ret.setLegalName("LegalName-" + MathHelper.abs(aTLR.nextInt()));
        // Ignore the optional stuff
        return ret;
    }

    @Nonnull
    private static DataRequestSubjectCVType _createDRS() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final DataRequestSubjectCVType ret = new DataRequestSubjectCVType();
        if (aTLR.nextBoolean())
            ret.setDataSubjectPerson(_createNP());
        else {
            ret.setDataSubjectCompany(_createLP());
            if (aTLR.nextBoolean())
                ret.setDataSubjectRepresentative(_createNP());
        }
        return ret;
    }

    @Nonnull
    private static RequestGroundsType _createRequestGrounds() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final RequestGroundsType ret = new RequestGroundsType();
        if (aTLR.nextBoolean())
            ret.setLawELIPermanentLink("https://example.org/article/" + MathHelper.abs(aTLR.nextInt()));
        else
            ret.setExplicitRequest(random(ExplicitRequestType.values()));
        return ret;
    }
    
    private static TimePeriodType _createTimePeriod(final int months) {
        TimePeriodType timePeriod = new TimePeriodType();
        timePeriod.setStartDate(XMLOffsetDateTime.now());
        timePeriod.setStartDate(XMLOffsetDateTime.now().plusMonths(months));
        
        return timePeriod;
    }
}
