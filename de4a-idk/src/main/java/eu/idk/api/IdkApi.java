package eu.idk.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import eu.idk.model.AtuNode;
import eu.idk.model.EvidenceService;
import eu.idk.model.EvidenceTypeIds;
import eu.idk.model.InlineResponse200;
import eu.idk.model.MorItem;

public interface IdkApi {

	@Operation(summary = "", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EvidenceService.class))),

			@ApiResponse(responseCode = "400", description = "Bad request."),

			@ApiResponse(responseCode = "404", description = "Evidence Service ID not found"),

			@ApiResponse(responseCode = "5XX", description = "Unexpected error") })
	@GetMapping(value = "/esl/{serviceId}", produces = { "application/json" })
	ResponseEntity<EvidenceService> idkEslServiceIdGet(
			@Parameter(in = ParameterIn.PATH, description = "Evidence Service Identificator", required = true, schema = @Schema()) @PathVariable("serviceId") String serviceId);

	@Operation(summary = "", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = InlineResponse200.class))),

			@ApiResponse(responseCode = "400", description = "Bad request. Evidence Type ID must be - HigherEdCertificate - SecondaryEdCertificate - ResidencyProof - BirthCertificate - MarriageCertificate - DeathCertificate"),

			@ApiResponse(responseCode = "404", description = "Country Code not found"),

			@ApiResponse(responseCode = "5XX", description = "Unexpected error") })
	@GetMapping(value = "/{evidenceTypeId}/{countryCode}", produces = { "application/json" })
	ResponseEntity<?> idkEvidenceTypeIdCountryCodeGet(
			@Parameter(in = ParameterIn.PATH, description = "evidence type with a canonical definition", required = true, schema = @Schema(allowableValues = {
					"HigherEdCertificate", "SecondaryEdCertificate", "ResidencyProof", "BirthCertificate",
					"MarriageCertificate", "DeathCertificate" })) @PathVariable("evidenceTypeId") String evidenceTypeId,
			@Parameter(in = ParameterIn.PATH, description = "ISO 3166-1 Alpha 2", required = true, schema = @Schema()) @PathVariable("countryCode") String countryCode,
			@RequestParam("atuCode") String atuCode);

	@Operation(summary = "", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AtuNode.class))),

			@ApiResponse(responseCode = "400", description = "Bad request."),

			@ApiResponse(responseCode = "404", description = "ATU Code not found"),

			@ApiResponse(responseCode = "5XX", description = "Unexpected error") })
	@GetMapping(value = "/ial/{atuCode}", produces = { "application/json" })
	ResponseEntity<AtuNode> idkIalAtuCodeGet(
			@Parameter(in = ParameterIn.PATH, description = "code of the administrative territorial unit", required = true, schema = @Schema()) @PathVariable("atuCode") String atuCode);

	@Operation(summary = "", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MorItem.class))),

			@ApiResponse(responseCode = "400", description = "Bad request."),

			@ApiResponse(responseCode = "404", description = "Evidence Type ID not found"),

			@ApiResponse(responseCode = "5XX", description = "Unexpected error") })
	@GetMapping(value = "/mor/{evidenceTypeId}", produces = { "application/json" })
	ResponseEntity<MorItem> idkMorEvidenceTypeIdGet(
			@Parameter(in = ParameterIn.PATH, description = "Description of the canonical schema of the given evidence type", required = true, schema = @Schema(allowableValues = {
					"HigherEdCertificate", "SecondaryEdCertificate", "ResidencyProof", "BirthCertificate",
					"MarriageCertificate",
					"DeathCertificate" })) @PathVariable("evidenceTypeId") String evidenceTypeId);

	@Operation(summary = "", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MorItem.class))),

			@ApiResponse(responseCode = "400", description = "Bad request."),

			@ApiResponse(responseCode = "404", description = "Term ID not found"),

			@ApiResponse(responseCode = "5XX", description = "Unexpected error") })
	@GetMapping(value = "/mor/{termId}", produces = { "application/json" })
	ResponseEntity<MorItem> idkMorTermIdGet(
			@Parameter(in = ParameterIn.PATH, description = "Semantic description of the given term", required = true, schema = @Schema()) @PathVariable("termId") EvidenceTypeIds termId);

}
