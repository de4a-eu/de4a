package eu.de4a.connector.api;

import java.io.InputStream;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;

/**
 * Request service API interface including
 * Swagger OpenAPI definitions
 *
 */
public interface RequestAPI {

//	@ApiOperation(value = "Receive request message for User Supported Intermediation pattern",
//			consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
//	@ApiImplicitParams({
//	    @ApiImplicitParam(name = "request", required = true,
//	        dataType = "eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType",
//	        paramType = "body")
//	})
//	@ApiResponses(value = {
//			@ApiResponse(responseCode = "200", description = "OK",
//					content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
//			@ApiResponse(responseCode = "404", description = "Bad request - Review the input data",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
//            @ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
//	})
	  ResponseEntity<byte[]> requestEvidenceUSI(@Valid /* @ApiParam(hidden = true) */ InputStream request);


//	@ApiOperation(value = "Receive request message for Intermediation pattern",
//			consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
//	@ApiImplicitParams({
//	    @ApiImplicitParam(name = "request", required = true,
//	        dataType = "eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType",
//	        paramType = "body")
//	})
//	@ApiResponses(value = {
//			@ApiResponse(responseCode = "200", description = "Message processed successfully",
//					content = @Content(schema = @Schema(implementation = ResponseExtractMultiEvidenceType.class))),
//			@ApiResponse(responseCode = "404", description = "Bad request - Review the input data",
//			        content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
//			@ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
//	})
	  ResponseEntity<byte[]> requestEvidenceIM(@Valid /* @ApiParam(hidden = true) */ InputStream request);

//	@ApiOperation(value = "Receive request message for Lookup pattern",
//            consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "request", required = true,
//            dataType = "eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceLUType",
//            paramType = "body")
//    })
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Message processed successfully",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
//            @ApiResponse(responseCode = "404", description = "Bad request - Review the input data",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
//            @ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
//    })
    ResponseEntity<byte[]> requestEvidenceLU(@Valid /* @ApiParam(hidden = true) */ InputStream request);

//    @ApiOperation(value = "Receive request message for Subscription & Notification pattern",
//            consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "request", required = true,
//            dataType = "eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType",
//            paramType = "body")
//    })
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Message processed successfully",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
//            @ApiResponse(responseCode = "404", description = "Bad request - Review the input data",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
//            @ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
//                    content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
//    })
    ResponseEntity<byte[]> requestEventSubscription(@Valid /* @ApiParam(hidden = true) */ InputStream request);

}
