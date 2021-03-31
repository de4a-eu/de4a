package eu.de4a.connector.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.smpclient.bdxr1.marshal.BDXR1MarshallerSignedServiceMetadataType;
import com.helger.xsds.bdxr.smp1.ProcessType;
import com.helger.xsds.bdxr.smp1.ServiceMetadataType;
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
import eu.de4a.util.DOMUtils;
import eu.de4a.util.MessagesUtils;
import eu.de4a.util.XDE4ACanonicalEvidenceType;
import eu.de4a.util.XDE4AMarshaller;

@Component
public class Client {
	@Autowired
	private RestTemplate restTemplate;
	@Value("#{'${idk.endpoint.jvm:${idk.endpoint:}}'}")
	private String idkEndpoint;
	private static final Logger logger = LoggerFactory.getLogger(Client.class);

	/**
	 * Obtain service metadata info from SMP by participantId and envidenceTypeId
	 * <p>
	 * scheme : service :: participantId : EvicendeTypeId</br>
	 * eg.: iso6523-actorid-upis:service::9921:ESS2833002E:BirthCertificate
	 * </p>
	 * 
	 * @param uri             Url to retrieve service metadata from SMP
	 * @param isReturnService Determine if the process looked for it is a
	 *                        response or request
	 * @return NodeInfo Service metadata
	 */
	public NodeInfo getNodeInfo(String uri, boolean isReturnService) {
		logger.debug("Request SMP {}", uri);
		final String serviceProc = "request";
		final String returnServiceProc = "response";

		NodeInfo nodeInfo = new NodeInfo();
		try {
			String signedServiceMetadataXML = restTemplate.getForObject(uri, String.class);
			BDXR1MarshallerSignedServiceMetadataType smpMarshaller = new BDXR1MarshallerSignedServiceMetadataType(true);
			SignedServiceMetadataType signedServiceMetadata = smpMarshaller.read(signedServiceMetadataXML);
			if (signedServiceMetadata == null) {
				return nodeInfo;
			}
			ServiceMetadataType serviceMetadata = signedServiceMetadata.getServiceMetadata();

			nodeInfo.setParticipantIdentifier(
					serviceMetadata.getServiceInformation().getParticipantIdentifier().getValue());
			nodeInfo.setDocumentIdentifier(serviceMetadata.getServiceInformation().getDocumentIdentifier().getValue());

			if (!CollectionUtils.isEmpty(serviceMetadata.getServiceInformation().getProcessList().getProcess())) {
				List<ProcessType> processes = serviceMetadata.getServiceInformation().getProcessList().getProcess();
				processes.stream().forEach(elem -> {
					String processId = elem.getProcessIdentifier().getValue();
					if (returnServiceProc.equals(processId) && isReturnService
							|| serviceProc.equals(processId) && !isReturnService) {
						nodeInfo.setEndpointURI(elem.getServiceEndpointList().getEndpointAtIndex(0).getEndpointURI());
						nodeInfo.setCertificate(elem.getServiceEndpointList().getEndpointAtIndex(0).getCertificate());
						nodeInfo.setProcessIdentifier(processId);
					}
				});

			}
			// TODO error handling
		} catch (NullPointerException nPe) {
			logger.error("Error parsing response from SMP", nPe);
			return new NodeInfo();
		} catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
			logger.error("Service metadata not found on SMP");
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

		ResponseEntity<String> response = restTemplate.postForEntity(urlRequest, request, String.class);
		ResponseErrorType responseObj = null;
		if (!ObjectUtils.isEmpty(response.getBody()) && HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
			responseObj = DE4AMarshaller.deUsiResponseMarshaller().read(response.getBody());
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

		try {
			if (!isUsi) {
				RequestExtractEvidenceIMType requestExtractEvidence = MessagesUtils
						.transformRequestToOwnerIM(evidenceRequest);
				String reqXML = DE4AMarshaller.doImRequestMarshaller().getAsString(requestExtractEvidence);
				ResponseEntity<String> response = restTemplate.postForEntity(urlRequest, reqXML, String.class);

				// Transform ResponseExtractEvidence into ResponseTransferEvidence
				// TODO move to utils class
				ResponseExtractEvidenceType responseExtractEvidenceType = XDE4AMarshaller
						.doImResponseMarshaller(XDE4ACanonicalEvidenceType
								.getXDE4CanonicalEvidenceType(evidenceRequest.getCanonicalEvidenceTypeId()))
						.read(response.getBody());
				return MessagesUtils.transformResponseTransferEvidenceUSI(responseExtractEvidenceType, evidenceRequest);
			} else {
				RequestExtractEvidenceUSIType requestExtractEvidence = MessagesUtils
						.transformRequestToOwnerUSI(evidenceRequest);
				String request = DE4AMarshaller.doUsiRequestMarshaller().getAsString(requestExtractEvidence);
				ResponseEntity<String> response = restTemplate.postForEntity(urlRequest, request, String.class);
				if (ObjectUtils.isEmpty(response.getBody())) {
					return DE4AResponseDocumentHelper.createResponseError(false);
				}
				return DE4AMarshaller.doUsiResponseMarshaller().read(response.getBody());
			}
		} catch (NullPointerException e) {
			throw new MessageException("Error processing response from Owner:" + e.getMessage());
		}
	}
}
