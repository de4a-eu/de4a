package eu.de4a.connector.api;

import java.io.InputStream;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;

/**
 * Service API interface including
 * Swagger OpenAPI definitions
 * Meant to provide IAL and MOR data to the consumers
 *
 */
public interface ServiceAPI {

//    @ApiOperation(value = "Lookup Routing Information",
//            consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
//    @ApiImplicitParams({
//    @ApiImplicitParam(name = "request", required = true,
//        dataType = "eu.de4a.ial.api.jaxb.RequestLookupRoutingInformationType",
//        paramType = "body")
//    })
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "OK",
//                content = @Content(schema = @Schema(implementation = ResponseLookupRoutingInformationType.class)))
//    })
    ResponseEntity<byte[]> lookupRoutingInformation(@Valid /* @ApiParam(hidden = true) */ InputStream request);

//    @ApiOperation(value = "Request to the Multilingual Ontology Repository - Returns a JSON file",
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "OK",
//                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
//        @ApiResponse(responseCode = "404", description = "No data found for the input parameters",
//            content = @Content(schema = @Schema(implementation = ResponseErrorType.class))),
//        @ApiResponse(responseCode = "500", description = "Something went wrong - Check the response content",
//                content = @Content(schema = @Schema(implementation = ResponseErrorType.class)))
//    })
    ResponseEntity<byte[]> getMorFile(@Valid /* @ApiParam */ String language);
}
