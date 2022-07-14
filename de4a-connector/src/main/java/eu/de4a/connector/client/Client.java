package eu.de4a.connector.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.commons.url.URLHelper;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.peppol.smp.ISMPTransportProfile;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.smpclient.bdxr1.BDXRClientReadOnly;
import com.helger.smpclient.config.SMPClientConfiguration;
import com.helger.smpclient.exception.SMPClientException;
import com.helger.smpclient.url.BDXLURLProvider;
import com.helger.smpclient.url.SMPDNSResolutionException;
import com.helger.xsds.bdxr.smp1.EndpointType;
import com.helger.xsds.bdxr.smp1.SignedServiceMetadataType;

import eu.de4a.connector.as4.client.DomibusGatewayClient;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseExtractEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseExtractEvidenceUSIException;
import eu.de4a.connector.error.exceptions.ResponseForwardEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseLookupRoutingInformationException;
import eu.de4a.connector.error.exceptions.SMPLookingMetadataInformationException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.error.utils.KafkaClientWrapper;
import eu.de4a.connector.error.utils.ResponseErrorFactory;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.iem.jaxb.common.types.AckType;
import eu.de4a.iem.jaxb.common.types.AvailableSourcesType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceUSIType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.MessagesUtils;
import eu.toop.connector.api.TCIdentifierFactory;

@Component
public class Client
{
  @Autowired
  private RestTemplate restTemplate;
  @Value ("#{'${idk.endpoint.jvm:${idk.endpoint:}}'}")
  private String idkEndpoint;
  @Value ("#{'${smp.endpoint.jvm:${smp.endpoint:}}'}")
  private String smpEndpoint;
  @Value ("${as4.gateway.implementation.bean}")
  private String as4ClientBean;
  @Value ("${de4a.smp.tls.trustall:false}")
  private boolean smpTlsTrustAll;

  @Autowired
  private ISMLInfo smlConfig;

  private static final Logger logger = LoggerFactory.getLogger (Client.class);

  /**
   * Obtain service metadata info from SMP by participantId and envidenceTypeId
   * <p>
   * scheme : service :: participantId : EvicendeTypeId</br>
   * eg.: iso6523-actorid-upis:service::9921:ESS2833002E:BirthCertificate
   * </p>
   *
   * @param participantId
   *        participant ID
   * @param documentTypeId
   *        document type ID
   * @param isReturnService
   *        Determine if the process looked for it is a response or request
   * @return NodeInfo Service metadata
   */
  public NodeInfo getNodeInfo (final String participantId,
                               final String documentTypeId,
                               final boolean isReturnService,
                               final Element userMessage)
  {
    final String messageType = (isReturnService ? DE4AConstants.MESSAGE_TYPE_RESPONSE
                                                : DE4AConstants.MESSAGE_TYPE_REQUEST);
    final NodeInfo nodeInfo = new NodeInfo ();

    if (DomibusGatewayClient.class.getSimpleName ().equalsIgnoreCase (as4ClientBean))
    {
      nodeInfo.setParticipantIdentifier (participantId);
      nodeInfo.setProcessIdentifier (messageType);
      nodeInfo.setDocumentIdentifier (documentTypeId);
      return nodeInfo;
    }

    KafkaClientWrapper.sendInfo (LogMessages.LOG_SMP_REQ_SENT, participantId, documentTypeId, messageType);
    try
    {
      // Requires the form iso6523-actorid-upis::9915:demo
      final IParticipantIdentifier aPI = SimpleIdentifierFactory.INSTANCE.createParticipantIdentifier (TCIdentifierFactory.PARTICIPANT_SCHEME,
                                                                                                       participantId);
      // Requires the form
      // urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration
      final IDocumentTypeIdentifier aDTI = SimpleIdentifierFactory.INSTANCE.parseDocumentTypeIdentifier (documentTypeId);

      logger.info ("Trying SMP lookup for PI '" +
                   aPI.getURIEncoded () +
                   "' and document type ID '" +
                   aDTI.getURIEncoded () +
                   "'");
      logger.info ("Predefined SMP endpoint: '" + smpEndpoint + "'");
      logger.info ("Configured SMP type: '{}'", SMPClientConfiguration.getTrustStoreType ());
      logger.info ("Configured SMP truststore: '{}'", SMPClientConfiguration.getTrustStorePath ());

      // Use explicit SMP or use DNS to resolve
      final BDXRClientReadOnly aSMPClient = ObjectUtils.isEmpty (smpEndpoint) ? new BDXRClientReadOnly (BDXLURLProvider.INSTANCE,
                                                                                                        aPI,
                                                                                                        this.smlConfig)
                                                                              : new BDXRClientReadOnly (URLHelper.getAsURI (smpEndpoint));

      if (this.smpTlsTrustAll)
      {
        try
        {
          aSMPClient.httpClientSettings ().setSSLContextTrustAll ();
          aSMPClient.httpClientSettings ().setHostnameVerifierVerifyAll ();
          logger.warn ("Trusting all TLS configurations for SMP client - not recommended for production");
        }
        catch (final GeneralSecurityException ex)
        {
          throw new IllegalStateException ("Failed to set SSL Context or Hostname verifier for SMP client", ex);
        }
      }

      final SignedServiceMetadataType signedServiceMetadata = aSMPClient.getServiceMetadataOrNull (aPI, aDTI);
      if (signedServiceMetadata == null ||
          signedServiceMetadata.getServiceMetadata () == null ||
          signedServiceMetadata.getServiceMetadata ().getServiceInformation () == null)
      {
        final String error = "It is not possible to retrieve data from the SMP, either because of a " +
                             "connection problem or because it does not exist.";
        logger.error (error);
        throw new SMPLookingMetadataInformationException ().withUserMessage (userMessage)
                                                           .withLayer (LayerError.COMMUNICATIONS)
                                                           .withFamily (FamilyErrorType.CONNECTION_ERROR)
                                                           .withModule (ExternalModuleError.SMP)
                                                           .withMessageArg (error);
      }

      logger.info ("Successfully resolved SMP service information: " +
                   signedServiceMetadata.getServiceMetadata ().getServiceInformation ());
      nodeInfo.setParticipantIdentifier (signedServiceMetadata.getServiceMetadata ()
                                                              .getServiceInformation ()
                                                              .getParticipantIdentifierValue ());
      nodeInfo.setDocumentIdentifier (signedServiceMetadata.getServiceMetadata ()
                                                           .getServiceInformation ()
                                                           .getDocumentIdentifierValue ());

      final IProcessIdentifier aProcID = SimpleIdentifierFactory.INSTANCE.createProcessIdentifier (DE4AConstants.PROCESS_SCHEME,
                                                                                                   messageType);
      final ISMPTransportProfile aTransportProfile = ESMPTransportProfile.TRANSPORT_PROFILE_BDXR_AS4;
      logger.info ("Trying to find SMP endpoint '" +
                   aProcID.getURIEncoded () +
                   "' and TP '" +
                   aTransportProfile.getID () +
                   "'");

      final EndpointType endpoint = BDXRClientReadOnly.getEndpoint (signedServiceMetadata.getServiceMetadata (),
                                                                    aProcID,
                                                                    aTransportProfile);
      if (endpoint != null)
      {
        logger.info ("Successfully resolved SMP endpoint: " + endpoint);
        nodeInfo.setEndpointURI (endpoint.getEndpointURI ());
        nodeInfo.setCertificate (endpoint.getCertificate ());
        nodeInfo.setProcessIdentifier (messageType);
      }
      else
      {
        throw new SMPClientException (MessageFormat.format ("Endpoint data not found for ParticipantID: {0}, MessageType: {1}",
                                                            aPI.getURIEncoded (),
                                                            aProcID.getURIEncoded ()));
      }
    }
    catch (final SMPClientException | SMPDNSResolutionException ex)
    {
      logger.error ("Service metadata not found on SMP", ex);
      throw new SMPLookingMetadataInformationException ().withUserMessage (userMessage)
                                                         .withLayer (LayerError.COMMUNICATIONS)
                                                         .withFamily (FamilyErrorType.CONNECTION_ERROR)
                                                         .withModule (ExternalModuleError.SMP)
                                                         .withMessageArg (ex.getMessage ());
    }
    return nodeInfo;
  }

  public ResponseLookupRoutingInformationType getSources (final RequestLookupRoutingInformationType request)
  {

    final List <String> paths = new ArrayList <> ();
    paths.add ("ial");
    paths.add (request.getCanonicalEvidenceTypeId ());
    if (!ObjectUtils.isEmpty (request.getCountryCode ()))
    {
      paths.add (request.getCountryCode ());
    }
    final URIBuilder uriBuilder = buildURI (idkEndpoint,
                                            "There was an error creating URI from IDK endpoint",
                                            paths.toArray (new String [0]),
                                            new String [] {},
                                            new String [] {});

    KafkaClientWrapper.sendInfo (LogMessages.LOG_IDK_REQ_SENT, uriBuilder.toString ());

    final byte [] response = ErrorHandlerUtils.getRestObjectWithCatching (uriBuilder.toString (),
                                                                          true,
                                                                          new ResponseLookupRoutingInformationException ().withModule (ExternalModuleError.IDK),
                                                                          this.restTemplate);

    return parseLookupResponse (response);
  }

  public ResponseLookupRoutingInformationType getProvisions (final RequestLookupRoutingInformationType request)
  {

    final URIBuilder uriBuilder = buildURI (idkEndpoint,
                                            "There was an error creating URI from IDK endpoint",
                                            new String [] { "provision" },
                                            new String [] { "canonicalEvidenceTypeId", "dataOwnerId" },
                                            new String [] { request.getCanonicalEvidenceTypeId (),
                                                            request.getDataOwnerId ().toLowerCase (Locale.ROOT) });

    KafkaClientWrapper.sendInfo (LogMessages.LOG_IDK_REQ_SENT, uriBuilder.toString ());

    final byte [] response = ErrorHandlerUtils.getRestObjectWithCatching (URLDecoder.decode (uriBuilder.toString (),
                                                                                             StandardCharsets.UTF_8),
                                                                          true,
                                                                          new ResponseLookupRoutingInformationException ().withModule (ExternalModuleError.IDK),
                                                                          this.restTemplate);

    return parseLookupResponse (response);
  }

  public ResponseLookupRoutingInformationType parseLookupResponse (final byte [] response)
  {
    final ObjectMapper mapper = new ObjectMapper ().configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                                               false);
    final ResponseLookupRoutingInformationType responseLookup = new ResponseLookupRoutingInformationType ();
    try
    {
      final AvailableSourcesType availableSources = mapper.readValue (response, AvailableSourcesType.class);
      if (!CollectionUtils.isEmpty (availableSources.getSource ()))
      {
        responseLookup.setAvailableSources (availableSources);
      }
      else
      {
        throw new ResponseLookupRoutingInformationException ().withLayer (LayerError.CONFIGURATION)
                                                              .withFamily (FamilyErrorType.SAVING_DATA_ERROR)
                                                              .withModule (ExternalModuleError.IDK)
                                                              .withMessageArg ("Data not found for the request");
      }
    }
    catch (final IOException e)
    {
      logger.error ("Error processing IDK response", e);
      throw new ResponseLookupRoutingInformationException ().withLayer (LayerError.COMMUNICATIONS)
                                                            .withFamily (FamilyErrorType.SCHEMA_VALIDATION_FAILED)
                                                            .withModule (ExternalModuleError.IDK)
                                                            .withMessageArg (e.getMessage ());
    }
    return responseLookup;
  }

  public boolean pushEvidence (final String endpoint, final Document requestDoc)
  {
    final ConnectorException exception = new ResponseForwardEvidenceException ().withModule (ExternalModuleError.CONNECTOR_DR);

    final URIBuilder uriBuilder = buildURI (endpoint,
                                            "Error building URI from Data Evaluator endpoint: {}",
                                            new String [] { "requestForwardEvidence" },
                                            new String [] {},
                                            new String [] {});

    try
    {
      final RequestTransferEvidenceUSIDTType requestUSIDT = (RequestTransferEvidenceUSIDTType) ErrorHandlerUtils.conversionDocWithCatching (DE4AMarshaller.dtUsiRequestMarshaller (IDE4ACanonicalEvidenceType.NONE),
                                                                                                                                            requestDoc,
                                                                                                                                            false,
                                                                                                                                            true,
                                                                                                                                            exception);

      KafkaClientWrapper.sendInfo (LogMessages.LOG_REQ_DE,
                                   requestUSIDT.getRequestId (),
                                   requestUSIDT.getDataEvaluator ().getAgentUrn (),
                                   requestUSIDT.getDataOwner ().getAgentUrn (),
                                   endpoint);

      final RequestForwardEvidenceType requestForward = MessagesUtils.transformRequestTransferUSIDT (requestUSIDT);
      final byte [] request = (byte []) ErrorHandlerUtils.conversionBytesWithCatching (DE4AMarshaller.deUsiRequestMarshaller (IDE4ACanonicalEvidenceType.NONE),
                                                                                       requestForward,
                                                                                       true,
                                                                                       true,
                                                                                       exception);

      final byte [] response = ErrorHandlerUtils.postRestObjectWithCatching (uriBuilder.toString (),
                                                                             request,
                                                                             false,
                                                                             exception.withModule (ExternalModuleError.DATA_EVALUATOR),
                                                                             this.restTemplate);

      final ResponseErrorType responseObj = (ResponseErrorType) ErrorHandlerUtils.conversionBytesWithCatching (DE4AMarshaller.deUsiResponseMarshaller (),
                                                                                                               response,
                                                                                                               false,
                                                                                                               false,
                                                                                                               exception);

      return AckType.OK.equals (responseObj.getAck ());
    }
    catch (final ConnectorException e)
    {
      final String errorMsg = ResponseErrorFactory.getHandlerFromClassException (e.getClass ()).getMessage (e);
      KafkaClientWrapper.sendError (LogMessages.LOG_ERROR_UNEXPECTED, errorMsg);
    }
    return false;
  }

  public Object sendEvidenceRequest (final RequestTransferEvidenceUSIIMDRType evidenceRequest,
                                     final String endpoint,
                                     final boolean isUsi)
  {
    String requestType;
    LogMessages logMessage;
    if (isUsi)
    {
      requestType = "requestExtractEvidence" + "USI";
      logMessage = LogMessages.LOG_USI_REQ_PROC;
    }
    else
    {
      requestType = "requestExtractEvidence" + "IM";
      logMessage = LogMessages.LOG_IM_REQ_PROC;
    }
    KafkaClientWrapper.sendInfo (logMessage,
                                 "Processing " + requestType,
                                 evidenceRequest.getRequestId (),
                                 evidenceRequest.getCanonicalEvidenceTypeId (),
                                 evidenceRequest.getDataEvaluator ().getAgentUrn (),
                                 evidenceRequest.getDataOwner ().getAgentUrn (),
                                 endpoint);

    final URIBuilder uriBuilder = buildURI (endpoint,
                                            "There was an error creating URI from owner endpoint: {}",
                                            new String [] { requestType },
                                            new String [] {},
                                            new String [] {});

    ConnectorException exception;
    if (!isUsi)
    {
      final RequestExtractEvidenceIMType requestExtractEvidence = MessagesUtils.transformRequestToOwnerIM (evidenceRequest);
      exception = new ResponseExtractEvidenceException ().withModule (ExternalModuleError.DATA_OWNER)
                                                         .withRequest (evidenceRequest);

      final byte [] reqXML = DE4AMarshaller.doImRequestMarshaller ().getAsBytes (requestExtractEvidence);
      final byte [] response = ErrorHandlerUtils.postRestObjectWithCatching (uriBuilder.toString (),
                                                                             reqXML,
                                                                             false,
                                                                             exception,
                                                                             this.restTemplate);
      final ResponseExtractEvidenceType responseExtractEvidenceType = (ResponseExtractEvidenceType) ErrorHandlerUtils.conversionBytesWithCatching (DE4AMarshaller.doImResponseMarshaller (IDE4ACanonicalEvidenceType.NONE),
                                                                                                                                                   response,
                                                                                                                                                   false,
                                                                                                                                                   false,
                                                                                                                                                   exception);
      return MessagesUtils.transformResponseTransferEvidence (responseExtractEvidenceType, evidenceRequest);
    }
    else
    {
      final RequestExtractEvidenceUSIType requestExtractEvidence = MessagesUtils.transformRequestToOwnerUSI (evidenceRequest);

      exception = new ResponseExtractEvidenceUSIException ().withModule (ExternalModuleError.DATA_OWNER);
      final byte [] reqXML = (byte []) ErrorHandlerUtils.conversionBytesWithCatching (DE4AMarshaller.doUsiRequestMarshaller (),
                                                                                      requestExtractEvidence,
                                                                                      true,
                                                                                      true,
                                                                                      exception);

      final byte [] response = ErrorHandlerUtils.postRestObjectWithCatching (uriBuilder.toString (),
                                                                             reqXML,
                                                                             false,
                                                                             exception,
                                                                             this.restTemplate);

      return ErrorHandlerUtils.conversionBytesWithCatching (DE4AMarshaller.doUsiResponseMarshaller (),
                                                            response,
                                                            false,
                                                            false,
                                                            exception);
    }
  }

  private URIBuilder buildURI (final String endpoint,
                               final String errorMsg,
                               final String [] paths,
                               final String [] params,
                               final String [] values)
  {
    URIBuilder uriBuilder;
    try
    {
      uriBuilder = new URIBuilder (endpoint);

      if (!uriBuilder.toString ().endsWith ("/"))
        uriBuilder.setPath (uriBuilder.getPath () + "/");
      for (final String path : paths)
      {
        uriBuilder.setPath (uriBuilder.getPath () + path + "/");
      }
      if (params.length == values.length)
      {
        for (int i = 0; i < params.length; i++)
        {
          uriBuilder.addParameter (params[i], values[i]);
        }
      }
    }
    catch (NullPointerException | URISyntaxException e)
    {
      KafkaClientWrapper.sendError (LogMessages.LOG_ERROR_UNEXPECTED, MessageFormat.format (errorMsg, endpoint));
      return new URIBuilder ();
    }

    return uriBuilder;
  }
}
