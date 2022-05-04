package eu.de4a.connector.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.helger.dcng.webapi.as4.ApiPostLookendAndSend;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import eu.de4a.connector.StaticContextAccessor;
import eu.de4a.connector.api.controller.EventController;
import eu.de4a.connector.api.controller.RequestController;
import eu.de4a.connector.api.controller.ResponseController;
import eu.de4a.connector.api.controller.ServiceController;
import eu.de4a.connector.config.AddressesProperties;
import eu.de4a.connector.config.TestConf;
import eu.de4a.connector.xml.MessagesHelper;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = {TestConf.class, AddressesProperties.class, StaticContextAccessor.class})
public class TestConnectorAPIs {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConnectorAPIs.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResponseController responseController;
    @Autowired
    private RequestController requestController;
    @Autowired
    private EventController eventController;
    @Autowired
    private ServiceController serviceController;
        

    @Test
    public void testRequestAPI() {
        
        // USI Request - modify it as needed
        RequestExtractMultiEvidenceUSIType req = MessagesHelper.createRequestExtractMultiEvidenceUSI(2);
        var marshaller = DE4ACoreMarshaller.drRequestTransferEvidenceUSIMarshaller();
        
        LOGGER.debug(marshaller.formatted().getAsString(req));
        byte[] bReq = marshaller.getAsBytes(req);
        
        int status = postCallMockedAS4API(bReq, "/request/usi/");
        
        assertEquals(HttpStatus.OK.value(), status);
    }
    
    @Test
    public void testResponseAPI() {
        
        // USI Redirect User message - modify it as needed
        RedirectUserType req = MessagesHelper.createRedirectUser();
        var marshaller = DE4ACoreMarshaller.dtUSIRedirectUserMarshaller();
        
        LOGGER.debug(marshaller.formatted().getAsString(req));
        byte[] bReq = marshaller.getAsBytes(req);
        
        int status = postCallMockedAS4API(bReq, "/response/usi/redirectUser/");
        
        assertEquals(HttpStatus.OK.value(), status);
    }
    
    @Test
    public void testNotificationAPI() {
        
        EventNotificationType req = MessagesHelper.createRequestEventNotification(2);
        var marshaller = DE4ACoreMarshaller.dtEventNotificationMarshaller();
        
        LOGGER.debug(marshaller.formatted().getAsString(req));
        byte[] bReq = marshaller.getAsBytes(req);
        
        int status = postCallMockedAS4API(bReq, "/event/notification/");
        
        assertEquals(HttpStatus.OK.value(), status);
    }
    
    @Ignore("Not neccessary for integration")
    @Test
    public void testMorAPI() {
        
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/service/mor/{lang}", "en");
        try {
            MvcResult result = createCall(httpReq, status().isOk());    
            assertNotNull(result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            fail();
        }
        
    }

    
    private MvcResult createCall(MockHttpServletRequestBuilder httpReq, ResultMatcher matcher) 
            throws Exception {       
        return this.mockMvc.perform(httpReq)
            .andDo(print()).andExpect(matcher)
            .andReturn();
    }
    
    private int postCallMockedAS4API(final byte[] bReq, final String path) {
        
        final IJsonObject aJson = new JsonObject();
        aJson.add ("success", true);
        
        // To skip the AS4 exchange since there is no multiple Connector instances running on tests
        try (MockedStatic<ApiPostLookendAndSend> apiMock = Mockito.mockStatic(ApiPostLookendAndSend.class)) {
            apiMock.when(() -> ApiPostLookendAndSend.perform(any(), any(), any(), any(), any(), any()))
                    .thenReturn(aJson);
            
            MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post(path);
            httpReq.contentType(MediaType.APPLICATION_XML_VALUE).content(bReq);
            
            MvcResult result = createCall(httpReq, status().isOk());
            LOGGER.info(result.getResponse().getContentAsString());
            return result.getResponse().getStatus();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            fail();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
