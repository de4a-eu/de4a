package eu.de4a.connector.utils;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestConnectorUtils {
  @Test
  public void TestMessageSources() {
    final String message = MessageUtils.format("error.as4.communications", new Object[] {"SMP", "Data not found"});

    assertTrue(message.contains("Data not found"));
  }

}
