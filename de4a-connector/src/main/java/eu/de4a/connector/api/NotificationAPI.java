package eu.de4a.connector.api;

import java.io.InputStream;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import eu.de4a.iem.core.jaxb.common.ResponseErrorType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Notification service API interface including
 * Swagger OpenAPI definitions
 *
 */
public interface NotificationAPI {

    @ApiOperation(value = "Receive evidence response message",
            consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "request", required = true,
            dataType = "eu.de4a.iem.core.jaxb.common.EventNotificationType",
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
    public ResponseEntity<byte[]> eventNotification(@Valid @ApiParam(hidden = true) InputStream request);
    
}
