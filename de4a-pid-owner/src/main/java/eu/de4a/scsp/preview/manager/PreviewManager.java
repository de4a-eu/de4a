package eu.de4a.scsp.preview.manager;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.exception.MessageException;
import eu.de4a.model.RequestorRequest;
import eu.de4a.model.RequestorRequestData;
import eu.de4a.repository.RequestorRequestDataRepository;
import eu.de4a.repository.RequestorRequestRepository;
import eu.de4a.scsp.translate.EvidenceMapper;
import eu.de4a.scsp.translate.EvidenceTranslator;
import eu.de4a.scsp.ws.client.ClientePidWS;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.FileUtils;

@Component
public class PreviewManager {
	
	private static final Logger logger =  LoggerFactory.getLogger (PreviewManager.class); 

	@Autowired
	private RequestorRequestRepository requestorRequestRepository;
	@Autowired
	private RequestorRequestDataRepository requestorRequestDataRepository;
	@Autowired
	private ClientePidWS clientePidWS;
	@Autowired
	private EvidenceMapper evidenceMapper;

	public Document gimmePreview(String id) {
		RequestorRequest requestorRequest = getRequestorRequest(id);		
		
		if (requestorRequest != null) {
//			RequestorRequestData requestorRequestData = new RequestorRequestData();
//			requestorRequestData.setRequest(requestorRequest);
//			
//			Example<RequestorRequestData> exampleReqData = Example.of(requestorRequestData);
//			List<RequestorRequestData>filesAttached=requestorRequestDataRepository.findAll(exampleReqData);
//			return filesAttached.stream().filter(
//					p->p.getIddata().equals(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE)).findFirst().orElse(null);
			try {
				return getDomesticEvidenceFromRequest(requestorRequest);
			} catch (MessageException e) {
				logger.error("There was an unexpected error processing domestic evidence", e);
			}
		}
		return null;
	}
	
	public RequestorRequest getRequestorRequest(String id) {
		RequestorRequest request = new RequestorRequest();
		request.setSenderId(id);
		return requestorRequestRepository.findById(id).orElse(null);
	}
	
	public Document getDomesticEvidenceFromRequest(RequestorRequest request) throws MessageException {
		RequestorRequestData reqData = getPendingRequest(request.getIdrequest(), DE4AConstants.TAG_EVIDENCE_REQUEST);
		Document docReq = DOMUtils.byteToDocument(reqData.getData());
		String evidenceServiceUri = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_SERVICE_URI, docReq.getDocumentElement());
		EvidenceTranslator translator = evidenceMapper.getTranslator(evidenceServiceUri);
		Element scspRequest = translator.translateEvidenceRequest(docReq.getDocumentElement());
		Element scspResponse = clientePidWS.sendRequest(scspRequest);
		if(scspResponse != null) {
			return scspResponse.getOwnerDocument();
		}
		return null;
	}
	
	public RequestorRequest registerRequestorRequest(Element request, boolean done) {
		RequestTransferEvidence req = (RequestTransferEvidence) DOMUtils.unmarshall(
				RequestTransferEvidence.class, request.getOwnerDocument());
		return registerRequestorRequest(req, done);
	}
	
	public RequestorRequest registerRequestorRequest(RequestTransferEvidence request, boolean done) {		
		RequestorRequest requestorReq = requestorRequestRepository.findById(request.getRequestId())
				.orElse(new RequestorRequest());
		
		if(StringUtils.isEmpty(requestorReq.getIdrequest())) {
			requestorReq.setIdrequest(request.getRequestId());
			requestorReq.setEvidenceServiceUri(request.getEvidenceServiceData().getEvidenceServiceURI());
			requestorReq.setReturnServiceUri(request.getReturnServiceId());
			requestorReq.setDone(done);			
		} else {
			requestorReq.setDone(done);
		}
		return requestorRequestRepository.save(requestorReq);
	}
	
	public void registerRequestorRequestData(RequestTransferEvidence request, Element doc, boolean done, String tagData) 
			throws MessageException {		
		RequestorRequest requestorReq = registerRequestorRequest(request, done);
		RequestorRequestData requestorRequestData = new RequestorRequestData();
		requestorRequestData.setMimetype("xml");
		requestorRequestData.setIddata(tagData);
		requestorRequestData.setData(DOMUtils.documentToByte(doc.getOwnerDocument()));
		requestorRequestData.setRequest(requestorReq);
		requestorRequestDataRepository.save(requestorRequestData);
	}
	
	public RequestorRequestData getPendingRequest(String idRequest, String tagData) {
		RequestorRequest request = requestorRequestRepository.findById(idRequest).orElse(null);
		RequestorRequestData requestorRequestData = new RequestorRequestData();
		requestorRequestData.setRequest(request);

		Example<RequestorRequestData> exampleReqData = Example.of(requestorRequestData);
		List<RequestorRequestData> filesAttached = requestorRequestDataRepository.findAll(exampleReqData);
		return filesAttached.stream().filter(p -> p.getIddata().equals(tagData))
				.findFirst().orElse(null);
	}
	
	public ResponseEntity<Resource> getPIDResponse(Element request) throws MessageException {
		String evidenceServiceUri = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_SERVICE_URI, request);
		EvidenceTranslator translator = evidenceMapper.getTranslator(evidenceServiceUri);
		Element scspRequest = translator.translateEvidenceRequest(request);
		Element scspResponse = clientePidWS.sendRequest(scspRequest);
		String idRequest = DOMUtils.getValueFromXpath(String.format(DE4AConstants.XPATH_REQUEST_ID, 
				DE4AConstants.TAG_EVIDENCE_REQUEST), request);
		String idevaluator = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVALUATOR_ID, request);
		String nameevaluator = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVALUATOR_NAME, request);
		String idowner = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_OWNER_ID, request);
		String nameownerr = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_OWNER_NAME, request);

		// Register request
		registerRequestorRequest(request, true);

		Element evidenceResponse = translator.translateEvidenceResponse(scspResponse);
		DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),
				String.format(DE4AConstants.XPATH_REQUEST_ID, DE4AConstants.TAG_EVIDENCE_RESPONSE), idRequest);
		DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(), DE4AConstants.XPATH_EVALUATOR_ID_NODE, idevaluator);
		DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(), DE4AConstants.XPATH_EVALUATOR_NAME_NODE,
				nameevaluator);
		DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(), DE4AConstants.XPATH_OWNER_NAME_NODE, nameownerr);
		DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(), DE4AConstants.XPATH_OWNER_ID_NODE, idowner);

		MultipartFile multipartCanonical;
		MultipartFile multipartScsp;
		try {
			multipartCanonical = FileUtils.getMultipart(DE4AConstants.TAG_EVIDENCE_RESPONSE,
					MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(evidenceResponse.getOwnerDocument()));
			multipartScsp = FileUtils.getMultipart(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE,
					MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(scspResponse.getOwnerDocument()));
		} catch (IOException e2) {
			logger.error("Error", e2);
			return returnErrorResource();
		}
		
		List<File> files = new ArrayList<>(2);
		File tempdir = null;
		try {
			tempdir = Files.createTempDirectory("de4a-temp").toFile();
			files.add(FileUtils.convert(multipartCanonical, tempdir));
			files.add(FileUtils.convert(multipartScsp, tempdir));
		} catch (IOException e1) {
			logger.error("Error", e1);
			return returnErrorResource();
		}
		
		byte[] data;
		try {
			data = FileUtils.empaquetarZip(tempdir);
		} catch (IOException e) {
			logger.error("Error", e);
			return returnErrorResource();
		}
		ByteArrayResource resource = new ByteArrayResource(data);
		
		return ResponseEntity.ok().contentLength(data.length)
				.contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
				.body(resource);
	}
	
	public ResponseEntity<Resource> returnErrorResource() {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ByteArrayResource(null));
	}
}
