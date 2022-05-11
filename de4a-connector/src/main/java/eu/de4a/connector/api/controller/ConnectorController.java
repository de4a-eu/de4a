package eu.de4a.connector.api.controller;

import java.io.InputStream;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.helger.dcng.api.DcngIdentifierFactory;
import com.helger.peppolid.CIdentifier;

import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.handler.ConnectorExceptionHandler;
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.iem.core.CIEM;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.eidas.np.GenderType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;

@Controller
public class ConnectorController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConnectorController.class);

  @GetMapping (value = "/")
  public String root ()
  {
    LOGGER.info ("Request to API / received");

    // Refers to WEB-INF/view/index.jsp
    return "index";
  }

  @Nonnull
  private static <T> T _conversionBytesWithCatching (final InputStream obj,
                                                     final DE4AMarshaller <T> marshaller,
                                                     final ConnectorException ex)
  {
    final ConnectorException baseEx = ex.withFamily (EFamilyErrorType.CONVERSION_ERROR).withLayer (ELayerError.INTERNAL_FAILURE);
    marshaller.readExceptionCallbacks ().set (e -> {
      if (e.getLinkedException () != null)
        baseEx.withMessageArg (e.getLinkedException ().getMessage ());
    });

    final T returnObj;
    try
    {
      returnObj = marshaller.read (obj);
    }
    catch (final Exception e)
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Object received is not valid, check the structure", e);
      throw baseEx.withMessageArg (e.getMessage ());
    }
    if (returnObj == null)
      throw baseEx.withMessageArg (ex.getArgs ());

    return returnObj;
  }

  @PostMapping (value = "/requestTransferEvidenceIM",
                produces = MediaType.APPLICATION_XML_VALUE,
                consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> iteration1LegacyIM (@Valid final InputStream request)
  {
    // Read the old format
    final var marshaller = DE4AMarshaller.drImRequestMarshaller ();

    // Unmarshalling and schema validation
    final RequestTransferEvidenceUSIIMDRType aOldRequest = _conversionBytesWithCatching (request,
                                                                                         marshaller,
                                                                                         new ConnectorException ().withModule (EExternalModuleError.CONNECTOR_DR));

    // Convert to the new format
    final RequestExtractMultiEvidenceIMType aNewRequest = new RequestExtractMultiEvidenceIMType ();
    aNewRequest.setRequestId (aOldRequest.getRequestId ());
    aNewRequest.setSpecificationId (CIEM.SPECIFICATION_ID);
    aNewRequest.setTimeStamp (aOldRequest.getTimeStamp ());
    aNewRequest.setProcedureId (aOldRequest.getProcedureId ());

    final Function <eu.de4a.iem.jaxb.common.types.AgentType, eu.de4a.iem.core.jaxb.common.AgentType> aAgentConverter = o -> {
      final eu.de4a.iem.core.jaxb.common.AgentType ret = new eu.de4a.iem.core.jaxb.common.AgentType ();
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
      ret.setFirstName (o.getFirstNameValue ());
      ret.setFamilyName (o.getFamilyNameValue ());
      ret.setDateOfBirth (o.getDateOfBirth ());
      if (o.getGender () != null)
        ret.setGender (GenderType.fromValue (o.getGender ().value ()));
      ret.setBirthName (o.getBirthNameValue ());
      ret.setPlaceOfBirth (o.getPlaceOfBirthValue ());
      ret.setCurrentAddress (o.getCurrentAddress ());
      return ret;
    };
    final Function <eu.de4a.iem.jaxb.common.idtypes.LegalPersonIdentifierType, eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType> aLPConverter = o -> {
      final eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType ret = new eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType ();
      ret.setPersonIdentifier (o.getPersonIdentifier ());
      ret.setFirstName (o.getFirstNameValue ());
      ret.setFamilyName (o.getFamilyNameValue ());
      ret.setDateOfBirth (o.getDateOfBirth ());
      if (o.getGender () != null)
        ret.setGender (GenderType.fromValue (o.getGender ().value ()));
      ret.setBirthName (o.getBirthNameValue ());
      ret.setPlaceOfBirth (o.getPlaceOfBirthValue ());
      ret.setCurrentAddress (o.getCurrentAddress ());
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
    aItem.setDataRequestSubject (aDRSConverter.apply (aOldRequest.getDataRequestSubject ()));
    aNewRequest.addRequestEvidenceIMItem (aItem);

    // Check if there are multiple evidence request
    final String docTypeID;
    if (aOldRequest.getRequestEvidenceUSIItemCount () > 1)
    {
      docTypeID = CIdentifier.getURIEncoded (DcngIdentifierFactory.DOCTYPE_SCHEME_CANONICAL_EVIDENCE, CIEM.MULTI_ITEM_TYPE);
    }
    else
    {
      docTypeID = aOldRequest.getRequestEvidenceUSIItemAtIndex (0).getCanonicalEvidenceTypeId ();
    }

    final AS4MessageDTO messageDTO = new AS4MessageDTO (aOldRequest.getDataEvaluator ().getAgentUrn (),
                                                        aOldRequest.getDataOwner ().getAgentUrn (),
                                                        docTypeID,
                                                        DE4AConstants.PROCESS_ID_REQUEST);

    this.apiManager.processIncomingMessage (aOldRequest, messageDTO, docTypeID, "USI Request", marshaller);

    return ResponseEntity.status (HttpStatus.OK).body (ConnectorExceptionHandler.getSuccessResponseBytes ());
  }
}
