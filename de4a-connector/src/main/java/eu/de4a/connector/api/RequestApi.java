package eu.de4a.connector.api;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface RequestApi {

	@PostMapping(value = "/lookupRoutingInformation")
	@ApiOperation(value = "Lookup Routing Information",
		consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiImplicitParams({
	@ApiImplicitParam(name = "request", required = true,
	    dataType = "eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType",
	    paramType = "body")
	})
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(schema = @Schema(implementation = ResponseLookupRoutingInformationType.class)))
	})
	public @ResponseBody String lookupRoutingInformation(
			@Valid @RequestBody @ApiParam(hidden = true) RequestLookupRoutingInformationType request);


	@PostMapping(value = "/requestTransferEvidenceUSI")
	@ApiOperation(value = "Receive RequestTransferEvidence message through USI pattern",
			consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "request", required = true,
	        dataType = "eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType",
	        paramType = "body")
	})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
	})
	public @ResponseBody String sendRequestUSI(@Valid @RequestBody @ApiParam(hidden = true) String request);


	@PostMapping(value = "/requestTransferEvidenceIM")
	@ApiOperation(value = "Receive RequestTransferEvidence message through IM pattern",
			consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "request", required = true,
	        dataType = "eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType",
	        paramType = "body")
	})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = ResponseTransferEvidenceType.class)))
	})
	public @ResponseBody String sendRequestIM(@Valid @RequestBody @ApiParam(hidden = true) String request);

}
