package eu.de4a.connector.client;

import java.util.Locale;

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

import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.AckType;
import eu.de4a.iem.jaxb.common.types.AvailableSourcesType;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ErrorType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceUSIType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
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
	public NodeInfo getNodeInfo(String participantId, String documentTypeId, boolean isReturnService) {
		logger.debug("Request SMP {}, {}", participantId, documentTypeId);

		NodeInfo nodeInfo = new NodeInfo();
		try {
			// Requires the form iso6523-actorid-upis::9915:demo
			final IParticipantIdentifier aPI = SimpleIdentifierFactory.INSTANCE
					.parseParticipantIdentifier(participantId.toLowerCase(Locale.getDefault()));
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

			if (signedServiceMetadata == null)
				return nodeInfo;

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
			// TODO error handling
		} catch (final SMPClientException | SMPDNSResolutionException ex) {
			logger.error("Service metadata not found on SMP", ex);
			return new NodeInfo();
		}
		return nodeInfo;
	}

	public ResponseLookupRoutingInformationType getSources(RequestLookupRoutingInformationType request) {

		StringBuilder uri = new StringBuilder(idkEndpoint);
		uri.append("/ial/");
		uri.append(request.getCanonicalEvidenceTypeId());
		if (!ObjectUtils.isEmpty(request.getCountryCode())) {
			uri.append("/").append(request.getCountryCode());
		}
		String response = restTemplate.getForObject(uri.toString(), String.class);
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ResponseLookupRoutingInformationType responseLookup = new ResponseLookupRoutingInformationType();
		try {
			AvailableSourcesType availableSources = mapper.readValue(response, AvailableSourcesType.class);
			responseLookup.setAvailableSources(availableSources);
		} catch (JsonProcessingException e) {
			ErrorListType errorList = new ErrorListType();
			ErrorType error = new ErrorType();
			// TODO error handling
			error.setCode("501");
			error.setText("Error converting JSON to object");
			errorList.addError(error);
			responseLookup.setErrorList(errorList);
		}
		return responseLookup;
	}

	public ResponseLookupRoutingInformationType getProvisions(RequestLookupRoutingInformationType request) {

		StringBuilder uri = new StringBuilder(idkEndpoint);
		uri.append("/provision");
		uri.append("?").append("canonicalEvidenceTypeId");
		uri.append("=").append(request.getCanonicalEvidenceTypeId());
		uri.append("&").append("dataOwnerId");
		uri.append("=").append(request.getDataOwnerId());

		String response = restTemplate.getForObject(uri.toString(), String.class);
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ResponseLookupRoutingInformationType responseLookup = new ResponseLookupRoutingInformationType();
		try {
			AvailableSourcesType availableSources = mapper.readValue(response, AvailableSourcesType.class);
			responseLookup.setAvailableSources(availableSources);
		} catch (JsonProcessingException e) {
			ErrorListType errorList = new ErrorListType();
			ErrorType error = new ErrorType();
			// TODO error handling
			error.setCode("501");
			error.setText("Error converting JSON to object");
			errorList.addError(error);
			responseLookup.setErrorList(errorList);
		}
		return responseLookup;
	}

	public boolean pushEvidence(String endpoint, Document requestForwardDoc) {
		logger.debug("Sending RequestForwardEvidence to evaluator {}", endpoint);

		String urlRequest = endpoint + "/requestForwardEvidence";
		String request = DOMUtils.documentToString(requestForwardDoc);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		ResponseEntity<String> response = restTemplate.postForEntity(urlRequest, new HttpEntity<>(request, headers),
				String.class);

		ResponseErrorType responseObj = null;
		if (!ObjectUtils.isEmpty(response.getBody()) && HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
			responseObj = DE4AMarshaller.deUsiResponseMarshaller().read(String.valueOf(response.getBody()));
		} else {
			// TODO error handling
			return false;
		}
		return AckType.OK.equals(responseObj.getAck());
	}

	public Object sendEvidenceRequest(RequestTransferEvidenceUSIIMDRType evidenceRequest, String endpoint,
			boolean isUsi) throws MessageException {
		if (logger.isDebugEnabled()) {
			logger.debug("Request: {}", evidenceRequest.getRequestId());
		}
		String urlRequest = endpoint + (isUsi ? "USI" : "IM");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);

		try {
			if (!isUsi) {
				RequestExtractEvidenceIMType requestExtractEvidence = MessagesUtils
						.transformRequestToOwnerIM(evidenceRequest);
				String reqXML = DE4AMarshaller.doImRequestMarshaller().getAsString(requestExtractEvidence);

				ResponseEntity<String> response = restTemplate.postForEntity(urlRequest,
						new HttpEntity<>(reqXML, headers), String.class);

				ResponseExtractEvidenceType responseExtractEvidenceType = DE4AMarshaller
						.doImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
						.read(String.valueOf(response.getBody()));
				return MessagesUtils.transformResponseTransferEvidenceUSI(responseExtractEvidenceType, evidenceRequest);
			} else {
				RequestExtractEvidenceUSIType requestExtractEvidence = MessagesUtils
						.transformRequestToOwnerUSI(evidenceRequest);
				String reqXML = DE4AMarshaller.doUsiRequestMarshaller().getAsString(requestExtractEvidence);
				ResponseEntity<String> response = restTemplate.postForEntity(urlRequest,
						new HttpEntity<>(reqXML, headers), String.class);
				if (ObjectUtils.isEmpty(response.getBody())) {
					return DE4AResponseDocumentHelper.createResponseError(false);
				}
				return DE4AMarshaller.doUsiResponseMarshaller().read(String.valueOf(response.getBody()));
			}
		} catch (NullPointerException e) {
			throw new MessageException("Error processing response from Owner:" + e.getMessage());
		}
	}
}
