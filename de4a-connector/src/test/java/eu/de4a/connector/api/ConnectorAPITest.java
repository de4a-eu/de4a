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

import com.helger.dcng.api.DcngConfig;
import com.helger.dcng.webapi.as4.ApiPostLookupAndSendIt2;
import com.helger.dcng.webapi.as4.LookupAndSendingResult;
import com.helger.peppolid.factory.IIdentifierFactory;

import eu.de4a.connector.StaticContextAccessor;
import eu.de4a.connector.api.controller.EventController;
import eu.de4a.connector.api.controller.RequestController;
import eu.de4a.connector.api.controller.ResponseController;
import eu.de4a.connector.api.controller.ServiceController;
import eu.de4a.connector.config.AddressesProperties;
import eu.de4a.connector.config.MockConf;
import eu.de4a.connector.xml.MockMessagesHelper;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;

@RunWith (SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles ("test")
@SpringBootTest (classes = { MockConf.class, AddressesProperties.class, StaticContextAccessor.class })
public class ConnectorAPITest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConnectorAPITest.class);

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
  public void testRequestAPI ()
  {

    // USI Request - modify it as needed
    final RequestExtractMultiEvidenceUSIType req = MockMessagesHelper.createRequestExtractMultiEvidenceUSI (2);
    final var marshaller = DE4ACoreMarshaller.drRequestExtractMultiEvidenceUSIMarshaller ();

    LOGGER.debug (marshaller.formatted ().getAsString (req));
    final byte [] bReq = marshaller.getAsBytes (req);

    final int status = _postCallMockedAS4API (bReq, "/request/usi/");
    assertEquals (HttpStatus.OK.value (), status);
  }

  @Test
  @Ignore
  public void testResponseAPI ()
  {

    // USI Redirect User message - modify it as needed
    final RedirectUserType req = MockMessagesHelper.createRedirectUser ();
    final var marshaller = DE4ACoreMarshaller.dtUSIRedirectUserMarshaller ();

    LOGGER.debug (marshaller.formatted ().getAsString (req));
    final byte [] bReq = marshaller.getAsBytes (req);

    final int status = _postCallMockedAS4API (bReq, "/response/usi/redirectUser/");
    assertEquals (HttpStatus.OK.value (), status);
  }

  @Test
  public void testNotificationAPI ()
  {

    final EventNotificationType req = MockMessagesHelper.createRequestEventNotification (2);
    final var marshaller = DE4ACoreMarshaller.dtEventNotificationMarshaller ();

    LOGGER.debug (marshaller.formatted ().getAsString (req));
    final byte [] bReq = marshaller.getAsBytes (req);

    final int status = _postCallMockedAS4API (bReq, "/event/notification/");

    assertEquals (HttpStatus.OK.value (), status);
  }

  @Ignore ("Not neccessary for integration")
  @Test
  public void testMorAPI ()
  {

    final MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get ("/service/mor/{lang}", "en");
    try
    {
      final MvcResult result = _createCall (httpReq, status ().isOk ());
      assertNotNull (result);
    }
    catch (final Exception e)
    {
      LOGGER.error (e.getMessage ());
      fail ();
    }

  }

  private MvcResult _createCall (final MockHttpServletRequestBuilder httpReq, final ResultMatcher matcher) throws Exception
  {
    return this.mockMvc.perform (httpReq).andDo (print ()).andExpect (matcher).andReturn ();
  }

  private int _postCallMockedAS4API (final byte [] bReq, final String path)
  {
    final IIdentifierFactory aIF = DcngConfig.getIdentifierFactory ();
    final LookupAndSendingResult aResult = new LookupAndSendingResult (aIF.createParticipantIdentifierWithDefaultScheme ("9999:test-sender"),
                                                                       aIF.createParticipantIdentifierWithDefaultScheme ("9999:test-receiver"),
                                                                       aIF.createDocumentTypeIdentifierWithDefaultScheme ("bla"),
                                                                       aIF.createProcessIdentifierWithDefaultScheme ("foo"),
                                                                       "fasel");
    aResult.setOverallSuccess (true);

    // To skip the AS4 exchange since there is no multiple Connector instances
    // running on tests
    try (MockedStatic <ApiPostLookupAndSendIt2> apiMock = Mockito.mockStatic (ApiPostLookupAndSendIt2.class))
    {
      apiMock.when ( () -> ApiPostLookupAndSendIt2.perform (any (), any (), any (), any (), any (), any ())).thenReturn (aResult);

      final MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post (path);
      httpReq.contentType (MediaType.APPLICATION_XML_VALUE).content (bReq);

      final MvcResult result = _createCall (httpReq, status ().isOk ());
      LOGGER.info (result.getResponse ().getContentAsString ());

      return result.getResponse ().getStatus ();
    }
    catch (final Exception e)
    {
      LOGGER.error (e.getMessage ());
      fail ();
    }
    return HttpStatus.INTERNAL_SERVER_ERROR.value ();
  }
}
