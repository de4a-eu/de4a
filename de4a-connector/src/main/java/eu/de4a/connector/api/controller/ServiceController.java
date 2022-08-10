package eu.de4a.connector.api.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.helger.commons.string.StringHelper;
import com.helger.dcng.core.api.DcngApiHelper;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.connector.utils.ServiceUtils;
import eu.de4a.ial.api.IALMarshaller;
import eu.de4a.ial.api.jaxb.ResponseLookupRoutingInformationType;
import eu.de4a.kafkaclient.model.EExternalModule;

@Controller
@RequestMapping ("/service")
@Validated
public class ServiceController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ServiceController.class);

  @Autowired
  private ServiceUtils serviceUtils;

  @Value ("${mor.file.endpoint}")
  private String morFileEndpoint;

  @GetMapping (value = "/ial/{cot}", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> lookupRoutingInformation (@Valid @PathVariable @NotNull final String cot)
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Request to API /service/ial/" + cot + " received");

    // Main query
    final ResponseLookupRoutingInformationType aResponse = DcngApiHelper.queryIAL (StringHelper.getExplodedToOrderedSet (",", cot));
    if (aResponse == null)
    {
    	final String errorMsg = "Error querying IAL";
    	KafkaClientWrapper.sendError(EFamilyErrorType.CONNECTION_ERROR, EExternalModule.CONNECTOR_DR, EExternalModule.IAL.getLabel(), errorMsg);

    	// Error case
    	throw new ConnectorException ().withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                     .withLayer (ELayerError.INTERNAL_FAILURE)
                                     .withModule (EExternalModule.IAL)
                                     .withMessageArg (errorMsg)
                                     .withHttpStatus (HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Success case
    return ResponseEntity.status (HttpStatus.OK)
                         .body (IALMarshaller.idkResponseLookupRoutingInformationMarshaller ().getAsBytes (aResponse));
  }

  @GetMapping (value = "/ial/{cot}/{atu}", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> lookupRoutingInformation (@Valid @PathVariable @NotNull final String cot,
                                                            @Valid @PathVariable @NotNull final String atu)
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Request to API /service/ial/" + cot + "/" + atu + " received");

    // Main query
    final ResponseLookupRoutingInformationType aResponse = DcngApiHelper.queryIAL (StringHelper.getExplodedToOrderedSet (",", cot), atu);
    if (aResponse == null)
    {
    	final String errorMsg = "Error querying IAL with ATU code";
    	KafkaClientWrapper.sendError(EFamilyErrorType.CONNECTION_ERROR, EExternalModule.CONNECTOR_DR, EExternalModule.IAL.getLabel(), errorMsg);

    	// Error case
    	throw new ConnectorException ().withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                     .withLayer (ELayerError.INTERNAL_FAILURE)
                                     .withModule (EExternalModule.IAL)
                                     .withMessageArg (errorMsg)
                                     .withHttpStatus (HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Success case
    return ResponseEntity.status (HttpStatus.OK)
                         .body (IALMarshaller.idkResponseLookupRoutingInformationMarshaller ().getAsBytes (aResponse));
  }

  @GetMapping (value = "/reload-addresses", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity <String> reloadAddresses ()
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Request to API /service/reload-addresses received");

    serviceUtils.reloadParticipantAddresses ();

    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Finished reloading addresses");

    return ResponseEntity.ok ("done");
  }
}
