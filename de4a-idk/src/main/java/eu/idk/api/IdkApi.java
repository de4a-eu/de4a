package eu.idk.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface IdkApi {
	
	@Operation(summary = "", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class))),

			@ApiResponse(responseCode = "400", description = "Bad request. Evidence Type ID must be - HigherEdCertificate - SecondaryEdCertificate - ResidencyProof - BirthCertificate - MarriageCertificate - CompanyRegistration"),

			@ApiResponse(responseCode = "404", description = "Not found country code"),

			@ApiResponse(responseCode = "5XX", description = "Unexpected error") })
	@GetMapping(value = "/ial/{canonicalEvidenceTypeId}/{countryCode}")
	ResponseEntity<String> ialCanonicalEvidenceTypeIdCountryCodeGet(
			@Parameter(in = ParameterIn.PATH, description = "evidence type with a canonical definition", required = true, schema = @Schema()) @PathVariable("canonicalEvidenceTypeId") String canonicalEvidenceTypeId,
			@Pattern(regexp = "[A-Z][A-Z]") @Parameter(in = ParameterIn.PATH, description = "country of the available sources", required = true, schema = @Schema()) @PathVariable("countryCode") String countryCode);

	@Operation(summary = "", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class))),

			@ApiResponse(responseCode = "400", description = "Bad request. Evidence Type ID must be - HigherEdCertificate - SecondaryEdCertificate - ResidencyProof - BirthCertificate - MarriageCertificate - CompanyRegistration"),

			@ApiResponse(responseCode = "5XX", description = "Unexpected error") })
	@GetMapping(value = "/ial/{canonicalEvidenceTypeId}")
	ResponseEntity<String> ialCanonicalEvidenceTypeIdGet(
			@Parameter(in = ParameterIn.PATH, description = "evidence type with a canonical definition", required = true, schema = @Schema()) @PathVariable("canonicalEvidenceTypeId") String canonicalEvidenceTypeId);

	@Operation(summary = "", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class))),

			@ApiResponse(responseCode = "400", description = "Bad request. Evidence Type ID must be - HigherEdCertificate - SecondaryEdCertificate - ResidencyProof - BirthCertificate - MarriageCertificate - CompanyRegistration"),

			@ApiResponse(responseCode = "404", description = "Not found country code"),

			@ApiResponse(responseCode = "5XX", description = "Unexpected error") })
	@GetMapping(value = "/provision")
	ResponseEntity<String> provisionGet(
			@NotNull @Parameter(in = ParameterIn.QUERY, description = "evidence type with a canonical definition", required = true, schema = @Schema()) @Valid @RequestParam(value = "canonicalEvidenceTypeId", required = true) String canonicalEvidenceTypeId,
			@NotNull @Pattern(regexp = "iso6523-actorid-upis::[0-9][0-9][0-9][0-9]:.+") @Parameter(in = ParameterIn.QUERY, description = "country of the available sources", required = true, schema = @Schema()) @Valid @RequestParam(value = "dataOwnerId", required = true) String dataOwnerId);

}
