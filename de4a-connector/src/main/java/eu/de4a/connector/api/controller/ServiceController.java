package eu.de4a.connector.api.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.string.StringHelper;
import com.helger.dcng.core.api.DcngApiHelper;
import com.helger.dcng.core.http.DcngHttpClientSettings;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.response.ResponseHandlerByteArray;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.ial.api.IALMarshaller;
import eu.de4a.ial.api.jaxb.ResponseLookupRoutingInformationType;

@Controller
@RequestMapping ("/service")
@Validated
public class ServiceController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ServiceController.class);

  @Value ("${mor.file.endpoint}")
  private String morFileEndpoint;

  @GetMapping (value = "/ial/{cot}", produces = MediaType.APPLICATION_XML_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> lookupRoutingInformation (@Valid @PathVariable @NotNull final String cot)
  {
    LOGGER.info ("Request to API /service/ial/" + cot + " received");

    // Main query
    final ResponseLookupRoutingInformationType aResponse = DcngApiHelper.queryIAL (StringHelper.getExplodedToOrderedSet (",", cot));
    if (aResponse == null)
    {
      // Error case
      throw new ConnectorException ().withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                     .withLayer (ELayerError.INTERNAL_FAILURE)
                                     .withModule (EExternalModuleError.IAL)
                                     .withMessageArg ("Error querying IAL")
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
    LOGGER.info ("Request to API /service/ial/" + cot + "/" + atu + " received");

    // Main query
    final ResponseLookupRoutingInformationType aResponse = DcngApiHelper.queryIAL (StringHelper.getExplodedToOrderedSet (",", cot), atu);
    if (aResponse == null)
    {
      // Error case
      throw new ConnectorException ().withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                     .withLayer (ELayerError.INTERNAL_FAILURE)
                                     .withModule (EExternalModuleError.IAL)
                                     .withMessageArg ("Error querying IAL with ATU code")
                                     .withHttpStatus (HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Success case
    return ResponseEntity.status (HttpStatus.OK)
                         .body (IALMarshaller.idkResponseLookupRoutingInformationMarshaller ().getAsBytes (aResponse));
  }

  @GetMapping (value = "/mor/{lang}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity <byte []> getMorFile (@Valid @PathVariable @NotNull final String lang)
  {
    LOGGER.info ("Request to API /service/mor/" + lang + " received");
    try
    {
      // TODO - Potential changes around the file name pattern and
      // implementation of this API
      final String sDestURL = FilenameHelper.getCleanConcatenatedUrlPath (this.morFileEndpoint, "/mor_" + lang + ".json");

      try (final HttpClientManager aHCM = HttpClientManager.create (new DcngHttpClientSettings ()))
      {
        final HttpGet aGet = new HttpGet (sDestURL);
        // Query
        final byte [] aResult = aHCM.execute (aGet, new ResponseHandlerByteArray ());
        // Pass through
        return ResponseEntity.ok (aResult);
      }
    }
    catch (final Exception e)
    {
      throw new ConnectorException ().withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                     .withLayer (ELayerError.INTERNAL_FAILURE)
                                     .withModule (EExternalModuleError.MOR)
                                     .withMessageArg ("Error accessing/processing to remote MOR file from: " + this.morFileEndpoint)
                                     .withHttpStatus (HttpStatus.NOT_FOUND);
    }
  }
}
