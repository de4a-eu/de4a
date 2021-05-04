package eu.de4a.connector.client;

import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.commons.url.URLHelper;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.sml.SMLInfo;
import com.helger.peppol.smp.ESMPTransportProfile;
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

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseErrorException;
import eu.de4a.connector.error.exceptions.ResponseExtractEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseLookupRoutingInformationException;
import eu.de4a.connector.error.exceptions.SMPLookingMetadataInformationException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.iem.jaxb.common.types.AckType;
import eu.de4a.iem.jaxb.common.types.AvailableSourcesType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceUSIType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.MessagesUtils;

@Component
public class Client {
	@Autowired
	private RestTemplate restTemplate;
	@Value("#{'${idk.endpoint.jvm:${idk.endpoint:}}'}")
	private String idkEndpoint;
	@Value("#{'${smp.endpoint.jvm:${smp.endpoint:}}'}")
	private String smpEndpoint;
	private static final Logger logger = LoggerFactory.getLogger(Client.class);

	private static final ISMLInfo SML_DE4A = new SMLInfo("de4a", "SML [DE4A]", "de4a.acc.edelivery.tech.ec.europa.eu.",
			"https://acc.edelivery.tech.ec.europa.eu/edelivery-sml", true);

	/**
	 * Obtain service metadata info from SMP by participantId and envidenceTypeId
	 * <p>
	 * scheme : service :: participantId : EvicendeTypeId</br>
	 * eg.: iso6523-actorid-upis:service::9921:ESS2833002E:BirthCertificate
	 * </p>
	 *
	 * @param participantId   participant ID
	 * @param documentTypeId  document type ID
	 * @param isReturnService Determine if the process looked for it is a response
	 *                        or request
	 * @return NodeInfo Service metadata
	 */
	public NodeInfo getNodeInfo(String participantId, String documentTypeId, boolean isReturnService, Element userMessage) {
		logger.debug("Request SMP {}, {}", participantId, documentTypeId);

		NodeInfo nodeInfo = new NodeInfo();
		try {
			// Requires the form iso6523-actorid-upis::9915:demo
			final IParticipantIdentifier aPI = SimpleIdentifierFactory.INSTANCE
					.parseParticipantIdentifier(participantId.toLowerCase(Locale.ROOT));
			// Requires the form urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration
			final IDocumentTypeIdentifier aDTI = SimpleIdentifierFactory.INSTANCE
					.parseDocumentTypeIdentifier(documentTypeId);
			// Use explicit SMP or use DNS to resolve
			final BDXRClientReadOnly aSMPClient = smpEndpoint == null
					? new BDXRClientReadOnly(BDXLURLProvider.INSTANCE, aPI, SML_DE4A)
					: new BDXRClientReadOnly(URLHelper.getAsURI(smpEndpoint));
					
		    logger.info("Configured SMP type: '{}'", SMPClientConfiguration.getTrustStoreType());
            logger.info("Configured SMP truststore: '{}'", SMPClientConfiguration.getTrustStorePath());
            logger.info("Configured SMP password: '{}'", SMPClientConfiguration.getTrustStorePassword());
			
			final SignedServiceMetadataType signedServiceMetadata = aSMPClient.getServiceMetadataOrNull(aPI, aDTI);

			if (signedServiceMetadata == null) {
				String error="It is not possible to retrieve data from the SMP, either because of a connection problem or because it does not exist.";
				logger.error(error);
				throw new SMPLookingMetadataInformationException( )
                 .withUserMessage(userMessage)
                 .withLayer(LayerError.COMMUNICATIONS)
                 .withFamily(FamilyErrorType.CONNECTION_ERROR) 
                 .withModule(ExternalModuleError.SMP)
                 .withMessageArg(error).withHttpStatus(HttpStatus.OK);
			}
				 
			nodeInfo.setParticipantIdentifier(signedServiceMetadata.getServiceMetadata()
			        .getServiceInformation().getParticipantIdentifierValue());
            nodeInfo.setDocumentIdentifier(signedServiceMetadata.getServiceMetadata()
                    .getServiceInformation().getDocumentIdentifierValue());
            
			final IProcessIdentifier aProcID = SimpleIdentifierFactory.INSTANCE
					.createProcessIdentifier(DE4AConstants.PROCESS_SCHEME, isReturnService ? 
							DE4AConstants.MESSAGE_TYPE_RESPONSE : DE4AConstants.MESSAGE_TYPE_REQUEST);
			final EndpointType endpoint = BDXRClientReadOnly.getEndpoint(signedServiceMetadata, aProcID,
					ESMPTransportProfile.TRANSPORT_PROFILE_BDXR_AS4);
			if (endpoint != null) {
				nodeInfo.setEndpointURI(endpoint.getEndpointURI());
				nodeInfo.setCertificate(endpoint.getCertificate());
				nodeInfo.setProcessIdentifier(aProcID.getURIEncoded());
			} 
		} catch (final SMPClientException | SMPDNSResolutionException ex) {
            logger.error("Service metadata not found on SMP", ex); 
            throw new SMPLookingMetadataInformationException( )
                        .withUserMessage(userMessage)
                        .withLayer(LayerError.COMMUNICATIONS)
                        .withFamily(FamilyErrorType.CONNECTION_ERROR) 
                        .withModule(ExternalModuleError.SMP)
                        .withMessageArg(ex.getMessage()).withHttpStatus(HttpStatus.OK);
        }
		return nodeInfo;
	}

	public ResponseLookupRoutingInformationType getSources(RequestLookupRoutingInformationType request) {

	    URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(idkEndpoint);
        } catch (URISyntaxException e1) {
            logger.error("There was an error creating URI from IDK endpoint");
            return null;
        }
        StringBuilder path = new StringBuilder(uriBuilder.getPath());
        if(!uriBuilder.toString().endsWith("/"))path.append("/");
        path.append("ial/");
        path.append(request.getCanonicalEvidenceTypeId());
        if (!ObjectUtils.isEmpty(request.getCountryCode())) {
            path.append("/");
            path.append(request.getCountryCode());
        }
        uriBuilder.setPath(path.toString());

		String response = ErrorHandlerUtils.getRestObjectWithCatching(uriBuilder.toString(), true,
		        new ResponseLookupRoutingInformationException().withModule(ExternalModuleError.IDK), 
		        this.restTemplate);
		
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ResponseLookupRoutingInformationType responseLookup = new ResponseLookupRoutingInformationType();
        try {
            AvailableSourcesType availableSources = mapper.readValue(response, AvailableSourcesType.class);
            responseLookup.setAvailableSources(availableSources);
        } catch (JsonProcessingException e) {
            logger.error("Error processing idk  response", e);
            throw new ResponseLookupRoutingInformationException()
                    .withLayer(LayerError.COMMUNICATIONS)
                    .withFamily(FamilyErrorType.SCHEMA_VALIDATION_FAILED) 
                    .withModule(ExternalModuleError.IDK)
                    .withMessageArg(e.getMessage())
                    .withHttpStatus(HttpStatus.OK);
        }
		return responseLookup;
	}

	public ResponseLookupRoutingInformationType getProvisions(RequestLookupRoutingInformationType request) {

	    URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(idkEndpoint);
        } catch (URISyntaxException e1) {
            logger.error("There was an error creating URI from IDK endpoint");
            return null;
        }
        StringBuilder path = new StringBuilder(uriBuilder.getPath());
        if(!uriBuilder.toString().endsWith("/"))path.append("/");
        path.append("provision");
        uriBuilder.setParameter("canonicalEvidenceTypeId", request.getCanonicalEvidenceTypeId());
        uriBuilder.setParameter("dataOwnerId", request.getDataOwnerId());
        uriBuilder.setPath(path.toString());
        
        String response = ErrorHandlerUtils.getRestObjectWithCatching(URLDecoder.decode(uriBuilder.toString(), StandardCharsets.UTF_8), 
                true, new ResponseLookupRoutingInformationException().withModule(ExternalModuleError.IDK), this.restTemplate);
        
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ResponseLookupRoutingInformationType responseLookup = new ResponseLookupRoutingInformationType();
        try {
            AvailableSourcesType availableSources = mapper.readValue(response, AvailableSourcesType.class);
            responseLookup.setAvailableSources(availableSources);
        } catch (JsonProcessingException e) {
            logger.error("Error processing idk  response", e);
            throw new ResponseLookupRoutingInformationException()
                    .withLayer(LayerError.COMMUNICATIONS)
                    .withFamily(FamilyErrorType.SCHEMA_VALIDATION_FAILED) 
                    .withModule(ExternalModuleError.IDK)
                    .withMessageArg(e.getMessage()).withHttpStatus(HttpStatus.OK);
        }
        return responseLookup;
	}

	public boolean pushEvidence(String endpoint, Document requestForwardDoc) {
		logger.debug("Sending RequestForwardEvidence to evaluator {}", endpoint);
		
		URIBuilder uriBuilder;
		try {
            uriBuilder = new URIBuilder(endpoint);
        } catch (URISyntaxException e) {
            logger.error(MessageFormat.format("There was an error creating URI from evaluator endpoint: {}", endpoint));
            return false;
        }
		uriBuilder.setPath("/requestForwardEvidence");
		String request = DOMUtils.documentToString(requestForwardDoc);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		ResponseEntity<String> response = restTemplate.postForEntity(uriBuilder.toString(), new HttpEntity<>(request, headers),
				String.class);

		ResponseErrorType responseObj = null;
		if (!ObjectUtils.isEmpty(response.getBody()) && HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
			responseObj = DE4AMarshaller.deUsiResponseMarshaller().read(String.valueOf(response.getBody()));
		} else {
		    // TODO could be retries? 
			logger.error(MessageFormat.format("There was an error pushing RequestForwardEvidence to the evaluator: {}", endpoint));
			return false;
		}
		return AckType.OK.equals(responseObj.getAck());
	}

	public Object sendEvidenceRequest(RequestTransferEvidenceUSIIMDRType evidenceRequest, String endpoint,
            boolean isUsi) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sending request to owner - request ID: {}", evidenceRequest.getRequestId());
        }
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(endpoint);
        } catch (URISyntaxException e) {
            logger.error(MessageFormat.format("There was an error creating URI from owner endpoint: {}", endpoint));
            return false;
        }
        uriBuilder.setPath((isUsi ? "/requestExtractEvidenceUSI" : "/requestExtractEvidenceIM"));
        ConnectorException exception;
        if (!isUsi) {
            RequestExtractEvidenceIMType requestExtractEvidence = MessagesUtils
                    .transformRequestToOwnerIM(evidenceRequest);
            exception = new ResponseExtractEvidenceException()
                    .withModule(ExternalModuleError.DATA_OWNER)
                    .withRequest(evidenceRequest);
            
            String reqXML = DE4AMarshaller.doImRequestMarshaller().getAsString(requestExtractEvidence);
            String response = ErrorHandlerUtils.postRestObjectWithCatching(uriBuilder.toString(), reqXML, 
                    false, exception, this.restTemplate);
            ResponseExtractEvidenceType responseExtractEvidenceType = (ResponseExtractEvidenceType) ErrorHandlerUtils
                    .conversionStrWithCatching(DE4AMarshaller.doImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE), 
                            String.valueOf(response), false, false, exception);
            return MessagesUtils.transformResponseTransferEvidence(responseExtractEvidenceType, 
                    evidenceRequest);
        } else {
            RequestExtractEvidenceUSIType requestExtractEvidence = MessagesUtils
                    .transformRequestToOwnerUSI(evidenceRequest);
            
            exception = new ResponseErrorException()
                    .withModule(ExternalModuleError.DATA_OWNER);
            String reqXML = (String) ErrorHandlerUtils.conversionStrWithCatching(DE4AMarshaller.doUsiRequestMarshaller(), 
                    requestExtractEvidence, true, true, exception);
            
            String response = ErrorHandlerUtils.postRestObjectWithCatching(uriBuilder.toString(), reqXML, 
                    false, exception, this.restTemplate);
            
            return ErrorHandlerUtils.conversionStrWithCatching(DE4AMarshaller.doUsiRequestMarshaller(), 
                    String.valueOf(response), false, false, exception);
        }
    }
}
