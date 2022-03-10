package eu.de4a.connector.api.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.de4a.connector.api.ServiceAPI;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.utils.APIRestUtils;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/service")
@Validated
@Log4j2
public class ServiceController implements ServiceAPI {
    
    @Value("${mor.file.endpoint}")
    private String morFileEndpoint;

    @PostMapping(value = "/ial/", produces = MediaType.APPLICATION_XML_VALUE, 
            consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> lookupRoutingInformation(InputStream request) {
        log.debug("Request to API /ial/ received");
        
        // NOT IMPLEMENTED - YET
        // TODO - has to be adapted to the new IAL implementation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    @GetMapping(value = "/mor/{lang}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getMorFile(@Valid @PathVariable @NotNull String lang) {
        //TODO - Potential changes around the file name pattern and implementation of this API
        log.debug("Request to API /mor/ received");
        try {
            String fileName = String.format("mor_%s.json", lang);
            URIBuilder uri = APIRestUtils.buildURI(this.morFileEndpoint, new String[]{fileName}, null, null);
            try(BufferedInputStream in = new BufferedInputStream(new URL(uri.toString()).openStream())) {
                return ResponseEntity.ok(in.readAllBytes());
            }
        } catch (NullPointerException | IOException e) {
            throw new ConnectorException().withFamily(FamilyErrorType.CONNECTION_ERROR)
                .withLayer(LayerError.INTERNAL_FAILURE)
                .withModule(ExternalModuleError.MOR)
                .withMessageArg("Error accessing/processing to remote MOR file from: " + this.morFileEndpoint)
                .withHttpStatus(HttpStatus.NOT_FOUND);
        }
    }
    
}
