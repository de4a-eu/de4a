package eu.de4a.connector.api;

import java.io.InputStream;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
    
    @ApiOperation(httpMethod = "GET", value = "Connector Index Page")
    public String rootPath();
	
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
	public ResponseEntity<String> lookupRoutingInformation(@Valid @ApiParam(hidden = true) InputStream request);


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
	public ResponseEntity<String> requestTransferEvidenceUSI(@Valid @ApiParam(hidden = true) InputStream request);


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
	public ResponseEntity<String> requestTransferEvidenceIM(@Valid @ApiParam(hidden = true) InputStream request);

}
