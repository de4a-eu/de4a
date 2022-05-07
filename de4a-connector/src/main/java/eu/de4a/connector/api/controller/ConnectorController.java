package eu.de4a.connector.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConnectorController {
    @GetMapping(value = "/")
    // @ApiOperation(httpMethod = "GET", value = "Connector Index Page")
    public String root() {
        // Refers to WEB-INF/view/index.jsp
        return "index";
    }

}
