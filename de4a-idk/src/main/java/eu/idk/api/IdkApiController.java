package eu.idk.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.idk.model.AtuNode;
import eu.idk.model.EvidenceService;
import eu.idk.model.EvidenceTypeIds;
import eu.idk.model.InlineResponse200;
import eu.idk.model.MorItem;
import eu.idk.presistence.EvidenceServiceRepository;
import eu.idk.presistence.IssuingAuthorityRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
public class IdkApiController implements IdkApi {

	private static final Logger log = LoggerFactory.getLogger(IdkApiController.class);

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	@Autowired
	private IssuingAuthorityRepository issuingAuthorityRepository;
	@Autowired
	private EvidenceServiceRepository evidenceServiceRepository;

	@Autowired
	public IdkApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	public ResponseEntity<EvidenceService> idkEslServiceIdGet(
			@Parameter(in = ParameterIn.PATH, description = "Evidence Service Identificator", required = true, schema = @Schema()) @PathVariable("serviceId") String serviceId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<EvidenceService>(objectMapper.readValue(
						"{\n  \"dataQuality\" : {\n    \"additionalInfo\" : \"additionalInfo\",\n    \"populationCoverage\" : 8\n  },\n  \"redirectURL\" : \"redirectURL\",\n  \"evidenceTypeId\" : \"HigherEdCertificate\",\n  \"serviceId\" : \"9920:ESS2833002E:BirthCertificate\",\n  \"inputParameterSets\" : {\n    \"recordMatchingAssurance\" : \"low\",\n    \"name\" : \"name\",\n    \"title\" : \"title\",\n    \"parameters\" : [ {\n      \"itemId\" : \"itemId\",\n      \"itemType\" : \"attribute\",\n      \"children\" : [ null, null ],\n      \"dataType\" : \"dataType\",\n      \"text\" : [ {\n        \"definition\" : \"definition\",\n        \"label\" : \"label\",\n        \"lang\" : \"lang\"\n      }, {\n        \"definition\" : \"definition\",\n        \"label\" : \"label\",\n        \"lang\" : \"lang\"\n      } ],\n      \"constraints\" : \"constraints\"\n    }, {\n      \"itemId\" : \"itemId\",\n      \"itemType\" : \"attribute\",\n      \"children\" : [ null, null ],\n      \"dataType\" : \"dataType\",\n      \"text\" : [ {\n        \"definition\" : \"definition\",\n        \"label\" : \"label\",\n        \"lang\" : \"lang\"\n      }, {\n        \"definition\" : \"definition\",\n        \"label\" : \"label\",\n        \"lang\" : \"lang\"\n      } ],\n      \"constraints\" : \"constraints\"\n    } ]\n  }\n}",
						EvidenceService.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<EvidenceService>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<EvidenceService>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<?> idkEvidenceTypeIdCountryCodeGet(
			@Parameter(in = ParameterIn.PATH, description = "evidence type with a canonical definition", required = true, schema = @Schema(allowableValues = {
					"HigherEdCertificate", "SecondaryEdCertificate", "ResidencyProof", "BirthCertificate",
					"MarriageCertificate", "DeathCertificate" })) @PathVariable("evidenceTypeId") String evidenceTypeId,
			@Parameter(in = ParameterIn.PATH, description = "ISO 3166-1 Alpha 2", required = true, schema = @Schema()) @PathVariable("countryCode") String countryCode,
			@RequestParam(value = "atuCode", required = false) String atuCode) {

		try {
			Object response = null;
			if(StringUtils.isEmpty(atuCode)) {
				response = issuingAuthorityRepository.findByEvidenceTypeAndCountryCode(evidenceTypeId, countryCode);							
			} else {
				response = evidenceServiceRepository.findByCanonicalEvidenceAndCountryCodeAndAtuCode(evidenceTypeId, countryCode, atuCode);
			}
			if(response == null) {
				return new ResponseEntity<InlineResponse200>(new InlineResponse200(), HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<String>(objectMapper.writeValueAsString(response), HttpStatus.ACCEPTED);
			}
		} catch (IOException e) {
			log.error("Couldn't serialize response for content type application/json", e);
			return new ResponseEntity<InlineResponse200>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<AtuNode> idkIalAtuCodeGet(
			@Parameter(in = ParameterIn.PATH, description = "code of the administrative territorial unit", required = true, schema = @Schema()) @PathVariable("atuCode") String atuCode) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<AtuNode>(objectMapper.readValue(
						"{\n  \"childen\" : [ null, null ],\n  \"element\" : {\n    \"atuCode\" : \"atuCode\",\n    \"atuPath\" : \"atuPath\",\n    \"atuLevel\" : \"nuts1\",\n    \"atuName\" : \"atuName\",\n    \"atuLatinName\" : \"atuLatinName\"\n  }\n}",
						AtuNode.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<AtuNode>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<AtuNode>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<MorItem> idkMorEvidenceTypeIdGet(
			@Parameter(in = ParameterIn.PATH, description = "Description of the canonical schema of the given evidence type", required = true, schema = @Schema(allowableValues = {
					"HigherEdCertificate", "SecondaryEdCertificate", "ResidencyProof", "BirthCertificate",
					"MarriageCertificate",
					"DeathCertificate" })) @PathVariable("evidenceTypeId") String evidenceTypeId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<MorItem>(objectMapper.readValue(
						"{\n  \"itemId\" : \"itemId\",\n  \"itemType\" : \"attribute\",\n  \"children\" : [ null, null ],\n  \"dataType\" : \"dataType\",\n  \"text\" : [ {\n    \"definition\" : \"definition\",\n    \"label\" : \"label\",\n    \"lang\" : \"lang\"\n  }, {\n    \"definition\" : \"definition\",\n    \"label\" : \"label\",\n    \"lang\" : \"lang\"\n  } ],\n  \"constraints\" : \"constraints\"\n}",
						MorItem.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<MorItem>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<MorItem>(HttpStatus.NOT_IMPLEMENTED);
	}

	public ResponseEntity<MorItem> idkMorTermIdGet(
			@Parameter(in = ParameterIn.PATH, description = "Semantic description of the given term", required = true, schema = @Schema()) @PathVariable("termId") EvidenceTypeIds termId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<MorItem>(objectMapper.readValue(
						"{\n  \"itemId\" : \"itemId\",\n  \"itemType\" : \"attribute\",\n  \"children\" : [ null, null ],\n  \"dataType\" : \"dataType\",\n  \"text\" : [ {\n    \"definition\" : \"definition\",\n    \"label\" : \"label\",\n    \"lang\" : \"lang\"\n  }, {\n    \"definition\" : \"definition\",\n    \"label\" : \"label\",\n    \"lang\" : \"lang\"\n  } ],\n  \"constraints\" : \"constraints\"\n}",
						MorItem.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<MorItem>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<MorItem>(HttpStatus.NOT_IMPLEMENTED);
	}

}
