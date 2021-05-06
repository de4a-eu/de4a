package eu.de4a.connector.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface ResponseApi {
	
	@ApiOperation(value = "Receive RequestTransferEvidenceUSIDT message through USI pattern",
	consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "request", required = true,
		    dataType = "eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType",
		    paramType = "body")
	})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
	})
	public @ResponseBody String requestTransferEvidenceUSIDT(@RequestBody @ApiParam(hidden = true) String request);

}
