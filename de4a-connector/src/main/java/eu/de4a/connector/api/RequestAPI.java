package eu.de4a.connector.api;

import java.io.InputStream;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import eu.de4a.iem.core.jaxb.common.ResponseErrorType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Request service API interface including
 * Swagger OpenAPI definitions
 *
 */
public interface RequestAPI {
	
	@ApiOperation(value = "Receive request message for User Supported Intermediation pattern",
			consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "request", required = true,
	        dataType = "eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType",
	        paramType = "body")
	})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
			@ApiResponse(responseCode = "404", description = "Bad request - Review the input data",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
            @ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
	})
	public ResponseEntity<byte[]> requestEvidenceUSI(@Valid @ApiParam(hidden = true) InputStream request);


	@ApiOperation(value = "Receive request message for Intermediation pattern",
			consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "request", required = true,
	        dataType = "eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType",
	        paramType = "body")
	})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Message processed successfully",
					content = @Content(schema = @Schema(implementation = ResponseExtractMultiEvidenceType.class))),
			@ApiResponse(responseCode = "404", description = "Bad request - Review the input data",
			        content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
			@ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
	})
	public ResponseEntity<byte[]> requestEvidenceIM(@Valid @ApiParam(hidden = true) InputStream request);
	
	@ApiOperation(value = "Receive request message for Lookup pattern",
            consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "request", required = true,
            dataType = "eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceLUType",
            paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message processed successfully",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
            @ApiResponse(responseCode = "404", description = "Bad request - Review the input data",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
            @ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
    })
    public ResponseEntity<byte[]> requestEvidenceLU(@Valid @ApiParam(hidden = true) InputStream request);
    
    @ApiOperation(value = "Receive request message for Subscription & Notification pattern",
            consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "request", required = true,
            dataType = "eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType",
            paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message processed successfully",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
            @ApiResponse(responseCode = "404", description = "Bad request - Review the input data",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
            @ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
    })
    public ResponseEntity<byte[]> requestEventSubscription(@Valid @ApiParam(hidden = true) InputStream request);

}
