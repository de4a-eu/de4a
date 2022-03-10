package eu.de4a.connector.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import eu.de4a.connector.api.service.DeliverService;
import eu.de4a.connector.error.exceptions.MessageException;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.utils.DOMUtils;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class TestConnectorServices {

    @Autowired
    private DeliverService deliverService;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ResourceLoader resourceLoader;

    private MockRestServiceServer mockServer;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testPushMessage() {
        try {
            // Mock the DE/DOs rest service response - modify it as you need
            mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://localhost:8080/dataOwner/in/usi/")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_XML)
                .body(""));
            
            // Using test request
            Resource rReq = this.resourceLoader.getResource("classpath:xml/request-usi.xml");
            Document dReq = DOMUtils.byteToDocument(rReq.getInputStream().readAllBytes());
            
            // Calling the tested method
            ResponseEntity<byte[]> response = this.deliverService.pushMessage(dReq, "iso6523-actorid-upis::9999:lu000000025", 
                    "iso6523-actorid-upis::9999:test-sgad", LogMessages.LOG_REQ_DO);
        
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
        } catch (URISyntaxException | MessageException | IOException e) {
            fail();
        }
    }
}
