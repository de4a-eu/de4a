package eu.toop.smp.client;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.de4a.connector.client.Client;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.service.spring.Conf;

@SpringBootTest(classes={Conf.class})

@RunWith(SpringRunner.class)
public class TestSMPClient {
	@Autowired
	private Client client;
	@Test
	// @Ignore("until test are defined and compilant")
	public void testNodeMine() {
		NodeInfo info=client.getNodeInfo("iso6523-actorid-upis::9999:egov","urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration", true);
    assertNotNull(info);
    assertEquals("https://de4a-dev-connector.egovlab.eu/phase4", info.getEndpointURI ());
	}
}
