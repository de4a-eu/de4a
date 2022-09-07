package eu.de4a.connector.api.controller;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.string.StringHelper;
import com.helger.dcng.api.DcngConfig;
import com.helger.dcng.core.api.DcngApiHelper;
import com.helger.peppolid.IParticipantIdentifier;

import eu.de4a.connector.EDE4ARuntimeEnvironment;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.connector.utils.ServiceUtils;
import eu.de4a.ial.api.IALMarshaller;
import eu.de4a.ial.api.jaxb.ErrorType;
import eu.de4a.ial.api.jaxb.ResponseItemType;
import eu.de4a.ial.api.jaxb.ResponseLookupRoutingInformationType;
import eu.de4a.ial.api.jaxb.ResponsePerCountryType;
import eu.de4a.kafkaclient.model.EExternalModule;

@Controller
@RequestMapping ("/service")
@Validated
public class ServiceController
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ServiceController.class);

  @Autowired
  private ServiceUtils serviceUtils;

  @Nonnull
  private static ErrorType _createError (@Nonnull final String sCode, @Nonnull final String sMsg)
  {
    final ErrorType ret = new ErrorType ();
    ret.setCode (sCode);
    ret.setText (sMsg);
    return ret;
  }

  private static void _filterDOs (@Nonnull final ResponseLookupRoutingInformationType aResponse,
                                  @Nullable final String environment,
                                  @Nullable final String sCOTIDs,
                                  @Nullable final String sATUCode)
  {
    // Filter only successful responses
    if (aResponse.hasResponseItemEntries ())
    {
      // Check if the environment is OKAY
      final EDE4ARuntimeEnvironment rtEnv = EDE4ARuntimeEnvironment.getFromIDOrNull (environment);
      if (rtEnv != null)
      {
        if (LOGGER.isInfoEnabled ())
          LOGGER.info ("Filtering allowed DOs returned by /service/ial/* query to " + rtEnv);

        for (final ResponseItemType aRI : new CommonsArrayList <> (aResponse.getResponseItem ()))
        {
          for (final ResponsePerCountryType aRPC : new CommonsArrayList <> (aRI.getResponsePerCountry ()))
          {
            aRPC.getProvision ().removeIf (aProv -> {
              final IParticipantIdentifier aPI = DcngConfig.getIdentifierFactory ()
                                                           .parseParticipantIdentifier (aProv.getDataOwnerId ());
              if (rtEnv.isAllowedParticipantID (aPI))
                return false;
              if (LOGGER.isInfoEnabled ())
                LOGGER.info ("Ignoring non-" + rtEnv + " DataOwner ID " + aProv.getDataOwnerId ());
              return true;
            });
            if (aRPC.hasNoProvisionEntries ())
              aRI.getResponsePerCountry ().remove (aRPC);
          }
          if (aRI.hasNoResponsePerCountryEntries ())
            aResponse.getResponseItem ().remove (aRI);
        }

        // We now might have an empty response
        if (aResponse.hasNoResponseItemEntries ())
        {
          aResponse.addError (_createError ("no-match",
                                            "Found NO matches searching for '" +
                                                        sCOTIDs +
                                                        "'" +
                                                        (sATUCode != null ? " and ATU code '" + sATUCode + "'" : "") +
                                                        " after filtering for " +
                                                        rtEnv));
        }
      }
      else
      {
        if (StringHelper.hasText (environment))
          if (LOGGER.isWarnEnabled ())
            LOGGER.warn ("Unsupported environment '" + environment + "' provided to /service/ial/* query");
      }
    }
  }

  @GetMapping (value = "/ial/{cot}", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> callIalCot (@Valid @PathVariable("cot") @NotNull final String cot,
                                              @RequestParam (name = "environment", required = false) final String environment)
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Request to API '/service/ial/" +
                   cot +
                   "' " +
                   (StringHelper.hasText (environment) ? "for environment '" + environment + "' " : "") +
                   "received");

    // Main query
    final ResponseLookupRoutingInformationType aResponse = DcngApiHelper.queryIAL (StringHelper.getExplodedToOrderedSet (",",
                                                                                                                         cot));
    if (aResponse == null)
    {
      final String errorMsg = "Error querying IAL without ATU code";
      KafkaClientWrapper.sendError (EFamilyErrorType.CONNECTION_ERROR,
                                    EExternalModule.CONNECTOR_DR,
                                    EExternalModule.IAL.getLabel (),
                                    errorMsg);

      // Error case
      throw new ConnectorException ().withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                     .withLayer (ELayerError.INTERNAL_FAILURE)
                                     .withModule (EExternalModule.IAL)
                                     .withMessageArg (errorMsg)
                                     .withHttpStatus (HttpStatus.INTERNAL_SERVER_ERROR);
    }

    _filterDOs (aResponse, environment, cot, null);

    // Success case
    return ResponseEntity.status (HttpStatus.OK)
                         .body (IALMarshaller.responseLookupRoutingInformationMarshaller ().getAsBytes (aResponse));
  }

  @GetMapping (value = "/ial/{cot}/{atu}", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity <byte []> callIalCotAtu (@Valid @PathVariable("cot") @NotNull final String cot,
                                                 @Valid @PathVariable("atu") @NotNull final String atu,
                                                 @RequestParam (name = "environment", required = false) final String environment)
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Request to API '/service/ial/" +
                   cot +
                   "/" +
                   atu +
                   "' " +
                   (StringHelper.hasText (environment) ? "for environment '" + environment + "' " : "") +
                   "received");

    // Main query
    final ResponseLookupRoutingInformationType aResponse = DcngApiHelper.queryIAL (StringHelper.getExplodedToOrderedSet (",",
                                                                                                                         cot),
                                                                                   atu);
    if (aResponse == null)
    {
      final String errorMsg = "Error querying IAL with ATU code";
      KafkaClientWrapper.sendError (EFamilyErrorType.CONNECTION_ERROR,
                                    EExternalModule.CONNECTOR_DR,
                                    EExternalModule.IAL.getLabel (),
                                    errorMsg);

      // Error case
      throw new ConnectorException ().withFamily (EFamilyErrorType.CONNECTION_ERROR)
                                     .withLayer (ELayerError.INTERNAL_FAILURE)
                                     .withModule (EExternalModule.IAL)
                                     .withMessageArg (errorMsg)
                                     .withHttpStatus (HttpStatus.INTERNAL_SERVER_ERROR);
    }

    _filterDOs (aResponse, environment, cot, atu);

    // Success case
    return ResponseEntity.status (HttpStatus.OK)
                         .body (IALMarshaller.responseLookupRoutingInformationMarshaller ().getAsBytes (aResponse));
  }

  @GetMapping (value = "/reload-addresses", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity <String> reloadAddresses ()
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Request to API '/service/reload-addresses' received");

    serviceUtils.reloadParticipantAddresses ();

    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Finished reloading addresses");

    return ResponseEntity.ok ("done");
  }
}
