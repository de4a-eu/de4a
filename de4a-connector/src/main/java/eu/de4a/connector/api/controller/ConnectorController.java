package eu.de4a.connector.api.controller;

import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.w3c.dom.Document;

import com.helger.commons.concurrent.ThreadHelper;
import com.helger.commons.datetime.PDTFactory;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.XMLWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;

import eu.de4a.connector.api.legacy.LegacyAPIHelper;
import eu.de4a.connector.api.manager.APIManager;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.connector.utils.MessageUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.AdditionalParameterType;
import eu.de4a.iem.core.jaxb.common.AdditionalParameterTypeType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.kafkaclient.model.EExternalModule;
import eu.de4a.kafkaclient.model.ELogMessage;

@Controller
public class ConnectorController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConnectorController.class);

  @Autowired
  private APIManager apiManager;

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
      KafkaClientWrapper.sendError (EFamilyErrorType.CONVERSION_ERROR, ex.getModule (), e.getLinkedException ().getMessage ());
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
    final var aOldRequestMarshaller = DE4AMarshaller.drImRequestMarshaller ();

    // Unmarshalling and schema validation
    final RequestTransferEvidenceUSIIMDRType aOldRequest = _conversionBytesWithCatching (request,
                                                                                         aOldRequestMarshaller,
                                                                                         new ConnectorException ().withModule (EExternalModule.CONNECTOR_DR));

    // Convert to the new format
    LOGGER.info ("Converting old request to new request");
    final RequestExtractMultiEvidenceIMType aNewRequest = LegacyAPIHelper.convertOldToNewRequest_DR (aOldRequest);

    // additional parameter for it1 message identification
    {
      final AdditionalParameterType addParam = new AdditionalParameterType ();
      addParam.setLabel ("iteration");
      addParam.setValue ("1");
      addParam.setType (AdditionalParameterTypeType.YES_NO);
      aNewRequest.getRequestEvidenceIMItemAtIndex (0).addAdditionalParameter (addParam);
    }

    final String sNewDocTypeID = aNewRequest.getRequestEvidenceIMItemAtIndex (0).getCanonicalEvidenceTypeId ();
    final AS4MessageDTO messageDTO = new AS4MessageDTO (aNewRequest.getDataEvaluator ().getAgentUrn (),
                                                        aNewRequest.getDataOwner ().getAgentUrn (),
                                                        sNewDocTypeID,
                                                        DE4AConstants.PROCESS_ID_REQUEST);

    final var aNewRequestMarshaller = DE4ACoreMarshaller.drRequestTransferEvidenceIMMarshaller ();
    final String requestMetadata = MessageUtils.getLegacyRequestMetadata (aOldRequest.getRequestId (),
                                                                          aOldRequest.getCanonicalEvidenceTypeId ());
    this.apiManager.processIncomingMessage (ELogMessage.LOG_REQ_IM_LEGACY_DE_DR,
                                            aNewRequest,
                                            messageDTO,
                                            aNewRequestMarshaller,
                                            sNewDocTypeID,
                                            requestMetadata);

    // Remember request
    LegacyAPIHelper.rememberLegacyRequest_DR (aOldRequest);

    // Synchronously wait for response
    final long timeout = 60_000;
    final long init = PDTFactory.getCurrentMillis ();
    LOGGER.info ("Waiting for synchronous response on legacy IM request (" + timeout + " milliseconds)");
    Document aResponseDoc = LegacyAPIHelper.isFinalized_DR (aOldRequest);
    while (aResponseDoc == null)
    {
      ThreadHelper.sleep (500);
      aResponseDoc = LegacyAPIHelper.isFinalized_DR (aOldRequest);
      if (PDTFactory.getCurrentMillis () - init >= timeout)
      {
        LOGGER.warn ("Timeout waiting for synchronous response on legacy IM request");
        break;
      }
    }

    final ResponseTransferEvidenceType aOldResponse;
    if (aResponseDoc == null)
    {
      // Failed to wait - send error message back
      final String sErrorMsg = "Failed to wait for synchronous response on legacy IM request. Timeout after " + timeout + " milliseconds.";
      LOGGER.error (sErrorMsg);
      // Copy as much as possible from the old request
      aOldResponse = DE4AResponseDocumentHelper.createResponseTransferEvidence (aOldRequest);
      final ErrorListType aOldErrorList = new ErrorListType ();
      aOldErrorList.addError (DE4AResponseDocumentHelper.createError ("timeout", sErrorMsg));
      aOldResponse.setErrorList (aOldErrorList);
    }
    else
    {
      // Try to interpret response
      final var aNewResponseMarshaller = DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller (IDE4ACanonicalEvidenceType.NONE);
      final ResponseExtractMultiEvidenceType aNewResponse = aNewResponseMarshaller.read (aResponseDoc);
      if (aNewResponse == null)
      {
        LOGGER.warn ("Response received:\n" +
                     XMLWriter.getNodeAsString (aResponseDoc, new XMLWriterSettings ().setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN)));
        throw new IllegalStateException ("Failed to interprete Response as ResponseExtractMultiEvidenceType - see log for details");
      }

      // Convert new Response to old response
      LOGGER.info ("Converting new response to old format");
      aOldResponse = LegacyAPIHelper.convertNewToOldResponse_DR (aOldRequest, aNewResponse);
    }

    // Serialize result
    final byte [] aOldResponseBytes = DE4AMarshaller.drImResponseMarshaller (eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType.NONE)
                                                    .getAsBytes (aOldResponse);
    LOGGER.info ("Returning old response");
    return ResponseEntity.status (HttpStatus.OK).body (aOldResponseBytes);
  }
}
