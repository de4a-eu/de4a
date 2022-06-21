package eu.de4a.connector.api.legacy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;

import eu.de4a.iem.core.CIEM;
import eu.de4a.iem.core.jaxb.common.DomesticEvidenceType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import eu.de4a.iem.jaxb.common.types.CanonicalEvidenceType;
import eu.de4a.iem.jaxb.common.types.DomesticsEvidencesType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import un.unece.uncefact.codelist.specification.ianamimemediatype._2003.BinaryObjectMimeCodeContentType;

public final class LegacyAPIHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LegacyAPIHelper.class);
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  private static final ICommonsMap <String, RequestTransferEvidenceUSIIMDRType> LEGACY_REQUESTS = new CommonsHashMap <> ();
  private static final ICommonsMap <String, Document> FINALIZED_REQUESTS = new CommonsHashMap <> ();

  private LegacyAPIHelper ()
  {}

  @Nonnull
  public static RequestExtractMultiEvidenceIMType convertOldToNewRequest (@Nonnull final RequestTransferEvidenceUSIIMDRType aOldRequest)
  {
    ValueEnforcer.notNull (aOldRequest, "OldRequest");

    final RequestExtractMultiEvidenceIMType aNewRequest = new RequestExtractMultiEvidenceIMType ();
    aNewRequest.setRequestId (aOldRequest.getRequestId ());
    aNewRequest.setSpecificationId (CIEM.SPECIFICATION_ID);
    aNewRequest.setTimeStamp (aOldRequest.getTimeStamp ());
    aNewRequest.setProcedureId (aOldRequest.getProcedureId ());

    final Function <eu.de4a.iem.jaxb.common.types.AgentType, eu.de4a.iem.core.jaxb.common.AgentType> aAgentConverter = o -> {
      final eu.de4a.iem.core.jaxb.common.AgentType ret = new eu.de4a.iem.core.jaxb.common.AgentType ();
      if (o.getAgentNameValue () != null)
        ret.setAgentName (o.getAgentNameValue ());
      ret.setAgentUrn (o.getAgentUrn ());
      ret.setRedirectURL (o.getRedirectURL ());
      return ret;
    };
    aNewRequest.setDataEvaluator (aAgentConverter.apply (aOldRequest.getDataEvaluator ()));
    aNewRequest.setDataOwner (aAgentConverter.apply (aOldRequest.getDataOwner ()));
    final RequestEvidenceItemType aItem = new RequestEvidenceItemType ();
    final Function <eu.de4a.iem.jaxb.common.idtypes.NaturalPersonIdentifierType, eu.de4a.iem.core.jaxb.common.NaturalPersonIdentifierType> aNPConverter = o -> {
      final eu.de4a.iem.core.jaxb.common.NaturalPersonIdentifierType ret = new eu.de4a.iem.core.jaxb.common.NaturalPersonIdentifierType ();
      ret.setPersonIdentifier (o.getPersonIdentifier ());
      if (o.getFirstNameValue () != null)
        ret.setFirstName (o.getFirstNameValue ());
      if (o.getFamilyNameValue () != null)
        ret.setFamilyName (o.getFamilyNameValue ());
      ret.setDateOfBirth (o.getDateOfBirth ());
      if (o.getGender () != null)
        ret.setGender (eu.de4a.iem.core.jaxb.eidas.np.GenderType.fromValue (o.getGender ().value ()));
      if (o.getBirthNameValue () != null)
        ret.setBirthName (o.getBirthNameValue ());
      if (o.getPlaceOfBirthValue () != null)
        ret.setPlaceOfBirth (o.getPlaceOfBirthValue ());
      ret.setCurrentAddress (o.getCurrentAddress ());
      return ret;
    };
    final Function <eu.de4a.iem.jaxb.common.idtypes.LegalPersonIdentifierType, eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType> aLPConverter = o -> {
      final eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType ret = new eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType ();
      ret.setLegalPersonIdentifier (o.getLegalPersonIdentifier ());
      ret.setLegalName (o.getLegalNameValue ());
      ret.setLegalAddress (o.getLegalAddress ());
      ret.setVATRegistration (o.getVATRegistration ());
      ret.setTaxReference (o.getTaxReference ());
      ret.setD201217EUIdentifier (o.getD201217EUIdentifier ());
      ret.setLEI (o.getLEI ());
      ret.setEORI (o.getEORI ());
      ret.setSEED (o.getSEED ());
      ret.setSIC (o.getSIC ());
      return ret;
    };
    final Function <eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType, eu.de4a.iem.core.jaxb.common.DataRequestSubjectCVType> aDRSConverter = o -> {
      final eu.de4a.iem.core.jaxb.common.DataRequestSubjectCVType ret = new eu.de4a.iem.core.jaxb.common.DataRequestSubjectCVType ();
      if (o.getDataSubjectPerson () != null)
        ret.setDataSubjectPerson (aNPConverter.apply (o.getDataSubjectPerson ()));
      if (o.getDataSubjectCompany () != null)
        ret.setDataSubjectCompany (aLPConverter.apply (o.getDataSubjectCompany ()));
      if (o.getDataSubjectRepresentative () != null)
        ret.setDataSubjectRepresentative (aNPConverter.apply (o.getDataSubjectRepresentative ()));
      return ret;
    };
    aItem.setRequestItemId (UUID.randomUUID ().toString ());
    if (aOldRequest.getDataRequestSubject () != null)
      aItem.setDataRequestSubject (aDRSConverter.apply (aOldRequest.getDataRequestSubject ()));
    // Hack to create new ID
    aItem.setCanonicalEvidenceTypeId (aOldRequest.getCanonicalEvidenceTypeId () + ":1.0");
    final Function <eu.de4a.iem.jaxb.common.types.RequestGroundsType, eu.de4a.iem.core.jaxb.common.RequestGroundsType> aRGConverter = o -> {
      final eu.de4a.iem.core.jaxb.common.RequestGroundsType ret = new eu.de4a.iem.core.jaxb.common.RequestGroundsType ();
      ret.setLawELIPermanentLink (o.getLawELIPermanentLink ());
      if (o.getExplicitRequest () != null)
        ret.setExplicitRequest (eu.de4a.iem.core.jaxb.common.ExplicitRequestType.fromValue (o.getExplicitRequest ().value ()));
      return ret;
    };
    if (aOldRequest.getRequestGrounds () != null)
      aItem.setRequestGrounds (aRGConverter.apply (aOldRequest.getRequestGrounds ()));
    // Additional parameters are ignored
    aNewRequest.addRequestEvidenceIMItem (aItem);
    return aNewRequest;
  }

  @Nonnull
  public static ResponseTransferEvidenceType convertNewToOldResponse (@Nonnull final RequestTransferEvidenceUSIIMDRType aOldRequest,
                                                                      @Nonnull final ResponseExtractMultiEvidenceType aNewResponse)
  {
    ValueEnforcer.notNull (aOldRequest, "OldRequest");
    ValueEnforcer.notNull (aNewResponse, "NewResponse");
    ValueEnforcer.isTrue (aNewResponse.getResponseExtractEvidenceItemCount () == 1,
                          "NewResponse does not have exactly 1 ResponseExtractEvidenceItem");

    // Copy as much as possible from the old request
    final ResponseTransferEvidenceType aOldResponse = DE4AResponseDocumentHelper.createResponseTransferEvidence (aOldRequest);
    // Only canonical evidence and domestic evidence is missing
    final ResponseExtractEvidenceItemType aNewItem = aNewResponse.getResponseExtractEvidenceItemAtIndex (0);
    // Canonical Evidences
    if (aNewItem.getCanonicalEvidence ().getAny () != null)
    {
      final CanonicalEvidenceType aOldCE = new CanonicalEvidenceType ();
      // TODO Do we need to clone???
      aOldCE.setAny (aNewItem.getCanonicalEvidence ().getAny ());
      aOldResponse.setCanonicalEvidence (aOldCE);
    }

    // Domestic Evidences
    final Function <eu.de4a.iem.core.jaxb.common.DomesticEvidenceType, eu.de4a.iem.jaxb.common.types.DomesticEvidenceType> aDEConverter = n -> {
      final eu.de4a.iem.jaxb.common.types.DomesticEvidenceType ret = new eu.de4a.iem.jaxb.common.types.DomesticEvidenceType ();
      if (n.getIssuingType () != null)
        ret.setIssuingType (eu.de4a.iem.jaxb.common.types.IssuingTypeType.fromValue (n.getIssuingType ().value ()));
      if (n.getMimeType () != null)
      {
        BinaryObjectMimeCodeContentType eOldMime = null;
        try
        {
          eOldMime = BinaryObjectMimeCodeContentType.fromValue (n.getMimeType ());
        }
        catch (final IllegalArgumentException ex)
        {
          // Ignore
        }
        ret.setMimeType (eOldMime);
        ret.setDataLanguage (n.getDataLanguage ());
        ret.setEvidenceData (n.getEvidenceData ());
        ret.setAdditionalInfo (n.getAdditionalInfo ());
      }
      return ret;
    };
    if (aNewItem.hasDomesticEvidenceEntries ())
    {
      final DomesticsEvidencesType aOldDEs = new DomesticsEvidencesType ();
      for (final DomesticEvidenceType aNewDE : aNewItem.getDomesticEvidence ())
        aOldDEs.addDomesticEvidence (aDEConverter.apply (aNewDE));
      aOldResponse.setDomesticEvidenceList (aOldDEs);
    }
    return aOldResponse;
  }

  public static void rememberLegacyRequest (@Nonnull final RequestTransferEvidenceUSIIMDRType aOldRequest)
  {
    ValueEnforcer.notNull (aOldRequest, "OldRequest");

    final String sID = aOldRequest.getRequestId ();
    LOGGER.info ("Remembering Legacy Request with ID '" + sID + "'");

    RW_LOCK.writeLocked ( () -> {
      if (LEGACY_REQUESTS.containsKey (sID))
        throw new IllegalArgumentException ("The legacy request ID '" + sID + "' is already present");
      LEGACY_REQUESTS.put (sID, aOldRequest);
    });
  }

  public static void rememberFinalized (@Nonnull @Nonempty final String sRequestID, @Nonnull final Document aResponseDoc)
  {
    ValueEnforcer.notEmpty (sRequestID, "RequestID");
    ValueEnforcer.notNull (aResponseDoc, "ResponseDoc");

    LOGGER.info ("Remembering Request with ID '" + sRequestID + "' was finalized");

    RW_LOCK.writeLocked ( () -> {
      // Only add, if it is a legacy ID
      if (LEGACY_REQUESTS.containsKey (sRequestID))
        if (FINALIZED_REQUESTS.put (sRequestID, aResponseDoc) != null)
          throw new IllegalArgumentException ("The legacy request ID '" + sRequestID + "' is already marked as finalized");
    });
  }

  public static Document isFinalized (@Nonnull final RequestTransferEvidenceUSIIMDRType aOldRequest)
  {
    ValueEnforcer.notNull (aOldRequest, "OldRequest");

    final String sID = aOldRequest.getRequestId ();
    final Document aResponseDoc = RW_LOCK.writeLockedGet ( () -> {
      final Document aDoc = FINALIZED_REQUESTS.remove (sID);
      if (aDoc != null)
      {
        // It was a finalized request
        LEGACY_REQUESTS.remove (sID);
        return aDoc;
      }
      return null;
    });

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Request with ID '" + sID + "' " + (aResponseDoc != null ? "was finalized" : "is not yet finalized"));

    return aResponseDoc;
  }

	public static RequestExtractEvidenceIMType convertNewToOldRequest(RequestExtractMultiEvidenceIMType aDRRequest) {
		final RequestExtractEvidenceIMType aOldRequest = new RequestExtractEvidenceIMType();
		aOldRequest.setRequestId(aDRRequest.getRequestId());
		aOldRequest.setSpecificationId(aDRRequest.getSpecificationId());
		aOldRequest.setTimeStamp(aDRRequest.getTimeStamp());
		aOldRequest.setProcedureId(aDRRequest.getProcedureId());
		
		final Function <eu.de4a.iem.core.jaxb.common.AgentType, eu.de4a.iem.jaxb.common.types.AgentType> aAgentConverter = o -> {
		      final eu.de4a.iem.jaxb.common.types.AgentType ret = new eu.de4a.iem.jaxb.common.types.AgentType ();
		      if (o.getAgentNameValue () != null)
		        ret.setAgentName (o.getAgentNameValue ());
		      ret.setAgentUrn (o.getAgentUrn ());
		      ret.setRedirectURL (o.getRedirectURL ());
		      return ret;
		    };
		aOldRequest.setDataEvaluator (aAgentConverter.apply (aDRRequest.getDataEvaluator ()));
		aOldRequest.setDataOwner (aAgentConverter.apply (aDRRequest.getDataOwner ()));
		
		final Function <eu.de4a.iem.core.jaxb.common.NaturalPersonIdentifierType, eu.de4a.iem.jaxb.common.idtypes.NaturalPersonIdentifierType> aNPConverter = o -> {
		      final eu.de4a.iem.jaxb.common.idtypes.NaturalPersonIdentifierType ret = new eu.de4a.iem.jaxb.common.idtypes.NaturalPersonIdentifierType();
		      ret.setPersonIdentifier (o.getPersonIdentifier ());
		      if (o.getFirstNameValue () != null)
		        ret.setFirstName (o.getFirstNameValue ());
		      if (o.getFamilyNameValue () != null)
		        ret.setFamilyName (o.getFamilyNameValue ());
		      ret.setDateOfBirth (o.getDateOfBirth ());
		      if (o.getGender () != null)
		        ret.setGender (eu.de4a.iem.jaxb.eidas.np.GenderType.fromValue (o.getGender ().value ()));
		      if (o.getBirthNameValue () != null)
		        ret.setBirthName (o.getBirthNameValue ());
		      if (o.getPlaceOfBirthValue () != null)
		        ret.setPlaceOfBirth (o.getPlaceOfBirthValue ());
		      ret.setCurrentAddress (o.getCurrentAddress ());
		      return ret;
		    };
	    final Function <eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType, eu.de4a.iem.jaxb.common.idtypes.LegalPersonIdentifierType> aLPConverter = o -> {
	      final eu.de4a.iem.jaxb.common.idtypes.LegalPersonIdentifierType ret = new eu.de4a.iem.jaxb.common.idtypes.LegalPersonIdentifierType ();
	      ret.setLegalPersonIdentifier (o.getLegalPersonIdentifier ());
	      ret.setLegalName (o.getLegalNameValue ());
	      ret.setLegalAddress (o.getLegalAddress ());
	      ret.setVATRegistration (o.getVATRegistration ());
	      ret.setTaxReference (o.getTaxReference ());
	      ret.setD201217EUIdentifier (o.getD201217EUIdentifier ());
	      ret.setLEI (o.getLEI ());
	      ret.setEORI (o.getEORI ());
	      ret.setSEED (o.getSEED ());
	      ret.setSIC (o.getSIC ());
	      return ret;
	    };
	    final Function <eu.de4a.iem.core.jaxb.common.DataRequestSubjectCVType, eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType> aDRSConverter = o -> {
	      final eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType ret = new eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType ();
	      if (o.getDataSubjectPerson () != null)
	        ret.setDataSubjectPerson (aNPConverter.apply (o.getDataSubjectPerson ()));
	      if (o.getDataSubjectCompany () != null)
	        ret.setDataSubjectCompany (aLPConverter.apply (o.getDataSubjectCompany ()));
	      if (o.getDataSubjectRepresentative () != null)
	        ret.setDataSubjectRepresentative (aNPConverter.apply (o.getDataSubjectRepresentative ()));
	      return ret;
	    };
		if (aDRRequest.getRequestEvidenceIMItemAtIndex(0).getDataRequestSubject () != null)
			aOldRequest.setDataRequestSubject (aDRSConverter.apply (aDRRequest.getRequestEvidenceIMItemAtIndex(0).getDataRequestSubject ()));
		final Function < eu.de4a.iem.core.jaxb.common.RequestGroundsType, eu.de4a.iem.jaxb.common.types.RequestGroundsType> aRGConverter = o -> {
		      final eu.de4a.iem.jaxb.common.types.RequestGroundsType ret = new eu.de4a.iem.jaxb.common.types.RequestGroundsType ();
		      ret.setLawELIPermanentLink (o.getLawELIPermanentLink ());
		      if (o.getExplicitRequest () != null)
		        ret.setExplicitRequest (eu.de4a.iem.jaxb.common.types.ExplicitRequestType.fromValue (o.getExplicitRequest ().value ()));
		      return ret;
		    };
		if (aDRRequest.getRequestEvidenceIMItemAtIndex(0).getRequestGrounds () != null)
			aOldRequest.setRequestGrounds (aRGConverter.apply (aDRRequest.getRequestEvidenceIMItemAtIndex(0).getRequestGrounds ()));
		aOldRequest.setCanonicalEvidenceTypeId(aDRRequest.getRequestEvidenceIMItemAtIndex(0).getCanonicalEvidenceTypeId().replace(":1.0",""));
		return aOldRequest;
	}

	public static ResponseExtractMultiEvidenceType convertOldToNewResponse(ResponseExtractEvidenceType aOldResponse, RequestExtractMultiEvidenceIMType aNewRequest) {
	    ValueEnforcer.notNull (aOldResponse, "OldResponse");
	    ValueEnforcer.notNull (aNewRequest, "NewRequest");
	    
	    ResponseExtractMultiEvidenceType aNewResponse = new ResponseExtractMultiEvidenceType();

	    aNewResponse.setRequestId (aNewRequest.getRequestId ());
	    aNewResponse.setTimeStamp (aNewRequest.getTimeStamp ());

	    aNewResponse.setDataEvaluator(aNewRequest.getDataEvaluator());
	    aNewResponse.setDataOwner(aNewRequest.getDataOwner());
	    
	    ResponseExtractEvidenceItemType item = new ResponseExtractEvidenceItemType();
	    item.setRequestItemId(aNewRequest.getRequestId ());
	    item.setDataRequestSubject(aNewRequest.getRequestEvidenceIMItemAtIndex(0).getDataRequestSubject());
	    item.setCanonicalEvidenceTypeId(aNewRequest.getRequestEvidenceIMItemAtIndex(0).getCanonicalEvidenceTypeId());
	    
	    if (aOldResponse.getCanonicalEvidence().getAny () != null)
	    {
	      final eu.de4a.iem.core.jaxb.common.CanonicalEvidenceType aNewCE = new eu.de4a.iem.core.jaxb.common.CanonicalEvidenceType ();
	      
	      aNewCE.setAny (aOldResponse.getCanonicalEvidence().getAny ());
	      item.setCanonicalEvidence (aNewCE);
	    }

	    List<ResponseExtractEvidenceItemType> aList = new ArrayList<>();
	    aList.add(item);
	    aNewResponse.setResponseExtractEvidenceItem(aList);
		
	    return aNewResponse;
	}

	public static ResponseExtractMultiEvidenceType convertOldToNewResponse(ResponseTransferEvidenceType aOldResponse2,
			RequestExtractMultiEvidenceIMType aNewRequest) {
		ValueEnforcer.notNull (aOldResponse2, "OldResponse");
	    ValueEnforcer.notNull (aNewRequest, "NewRequest");
	    
	    ResponseExtractMultiEvidenceType aNewResponse = new ResponseExtractMultiEvidenceType();

	    aNewResponse.setRequestId (aNewRequest.getRequestId ());
	    aNewResponse.setTimeStamp (aNewRequest.getTimeStamp ());

	    aNewResponse.setDataEvaluator(aNewRequest.getDataEvaluator());
	    aNewResponse.setDataOwner(aNewRequest.getDataOwner());
	    
	    ResponseExtractEvidenceItemType item = new ResponseExtractEvidenceItemType();
	    item.setRequestItemId(aNewRequest.getRequestId ());
	    item.setDataRequestSubject(aNewRequest.getRequestEvidenceIMItemAtIndex(0).getDataRequestSubject());
	    item.setCanonicalEvidenceTypeId(aNewRequest.getRequestEvidenceIMItemAtIndex(0).getCanonicalEvidenceTypeId());
	    
	    if (aOldResponse2.getCanonicalEvidence().getAny () != null)
	    {
	      final eu.de4a.iem.core.jaxb.common.CanonicalEvidenceType aNewCE = new eu.de4a.iem.core.jaxb.common.CanonicalEvidenceType ();
	      
	      aNewCE.setAny (aOldResponse2.getCanonicalEvidence().getAny ());
	      item.setCanonicalEvidence (aNewCE);
	    }

	    List<ResponseExtractEvidenceItemType> aList = new ArrayList<>();
	    aList.add(item);
	    aNewResponse.setResponseExtractEvidenceItem(aList);
		
	    return aNewResponse;
	}
}
