package eu.toop.smp.client;



import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import eu.de4a.connector.client.Client;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.service.spring.Conf;

@SpringBootTest(classes={Conf.class})
  
@RunWith(SpringRunner.class) 
public class TestSMPClient {	
	@Autowired
	private ConfigurableEnvironment env; 
	@Autowired
	private Client client;
	@Test
	@Ignore("until test are defined and compilant")
	public void testNodeMine() {
		NodeInfo info=client.getNodeInfo("9915:tooptest",false);
		Assert.assertNotNull(info);
	}
}
