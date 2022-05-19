package eu.de4a.connector.utils;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import eu.de4a.connector.StaticContextAccessor;
import eu.de4a.connector.config.AddressesProperties;
import eu.de4a.connector.config.MockConf;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {MockConf.class, AddressesProperties.class, StaticContextAccessor.class})
public class ConnectorUtilsTest {
  @Test
  public void TestMessageSources() {
    final String message = MessageUtils.format("error.as4.communications", new Object[] {"SMP", "Data not found"});

    assertTrue(message.contains("Data not found"));
  }
}
