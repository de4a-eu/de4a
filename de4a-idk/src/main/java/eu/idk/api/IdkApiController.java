package eu.idk.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.de4a.iem.jaxb.common.types.AvailableSourcesType;
import eu.idk.model.Source;
import eu.idk.presistence.SourceRepository;
import eu.idk.service.RequestManager;

@RestController
public class IdkApiController implements IdkApi {

	private static final Logger log = LoggerFactory.getLogger(IdkApiController.class);
	private static final String REQUEST_LOG_PARAM = "Processing request with: {}";
	private static final String REQUEST_LOG_PARAMS = "Processing request with: {}, {}";
	private static final String JSON_ERROR = "There was an error on JSON conversion";

	@Autowired
	private SourceRepository sourceRepository;
	@Autowired
	private RequestManager manager;

	@Autowired
	public IdkApiController(ObjectMapper objectMapper, HttpServletRequest request) {
	}

	@Override
	public ResponseEntity<byte[]> ialCanonicalEvidenceTypeIdCountryCodeGet(String canonicalEvidenceTypeId,
			String countryCode) {
		log.debug(REQUEST_LOG_PARAMS, canonicalEvidenceTypeId, countryCode);
		AvailableSourcesType availableSources = new AvailableSourcesType();
		byte[] jsonResponse = null;
		List<Source> sources = sourceRepository.findByCanonicalEvidenceTypeIdAndCountryCode(canonicalEvidenceTypeId, countryCode);
		if (sources != null) {
			manager.extractSourceInfo(sources, availableSources, null);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonResponse = mapper.writeValueAsBytes(availableSources);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(JSON_ERROR.getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonResponse, HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<byte[]> ialCanonicalEvidenceTypeIdGet(String canonicalEvidenceTypeId) {
		log.debug(REQUEST_LOG_PARAM, canonicalEvidenceTypeId);
		AvailableSourcesType availableSources = new AvailableSourcesType();
		byte[] jsonResponse = null;
		List<Source> sources = sourceRepository.findByCanonicalEvidenceTypeId(canonicalEvidenceTypeId);
		if (sources != null) {
			manager.extractSourceInfo(sources, availableSources, null);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonResponse = mapper.writeValueAsBytes(availableSources);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(JSON_ERROR.getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonResponse, HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<byte[]> provisionGet(String canonicalEvidenceTypeId, String dataOwnerId) {
		log.debug(REQUEST_LOG_PARAMS, canonicalEvidenceTypeId, dataOwnerId);
		AvailableSourcesType availableSources = new AvailableSourcesType();
		byte[] jsonResponse = null;
		List<Source> sources = sourceRepository.findByCanonicalEvidenceTypeId(canonicalEvidenceTypeId);
		if (sources != null) {
			manager.extractSourceInfo(sources, availableSources, dataOwnerId);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonResponse = mapper.writeValueAsBytes(availableSources);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(JSON_ERROR.getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonResponse, HttpStatus.ACCEPTED);
	}

}
