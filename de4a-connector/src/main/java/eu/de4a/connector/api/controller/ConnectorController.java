package eu.de4a.connector.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConnectorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorController.class);

    @GetMapping(value = "/")
    // @ApiOperation(httpMethod = "GET", value = "Connector Index Page")
    public String root() {
        LOGGER.debug("Request to API / received");

        // Refers to WEB-INF/view/index.jsp
        return "index";
    }

}
