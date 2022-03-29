package eu.toop.smp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.de4a.connector.client.Client;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.service.spring.Conf;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;

@SpringBootTest(classes={Conf.class}, properties = {"smp.endpoint=https://toop-smp.pprod.medius.si",
        "idk.endpoint=https://de4a.redsara.es/idk-mock/",
		"truststore.type=JKS",
		"truststore.path=truststore/de4a-truststore-smp-it2-pw-de4a.jks",
		"truststore.password=de4a"})
@RunWith(SpringRunner.class)
public class TestClient {
	@Autowired
	private Client client;
	
	@Test
	@Ignore("Not run on Jenkins")
	public void testSmpNode() {
		NodeInfo info=client.getNodeInfo("9991:at000000271","urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration", true, null);
		assertNotNull(info);
		assertEquals("https://de4a-dev-connector.egovlab.eu/phase4", info.getEndpointURI ());
	}

  @Test
  @Ignore("Not run on Jenkins")
  public void testRomania() {
    // Search request
    final NodeInfo info=client.getNodeInfo("9991:ro000000006","urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration", false, null);
    assertNotNull(info);
    assertEquals("https://de4a.onrc.ro/de4a-connector/requestTransferEvidenceIM", info.getEndpointURI ());
  }
	
	@Test
	@Ignore("Endpoint connection problems")
    public void testIdkSources() {
        RequestLookupRoutingInformationType request = new RequestLookupRoutingInformationType();
        request.setCanonicalEvidenceTypeId("CompanyRegistration");
        request.setCountryCode("AT");
        ResponseLookupRoutingInformationType response = client.getSources(request);
        assertNotNull(response);
        assertEquals("iso6523-actorid-upis::9999:AT000000271", response.getAvailableSources().getSourceAtIndex(0)
                .getProvisionItems().getProvisionItemAtIndex(0).getDataOwnerId());
    }
    
    @Test
    @Ignore("Endpoint connection problems")
    public void testIdkProvisions() {
        RequestLookupRoutingInformationType request = new RequestLookupRoutingInformationType();
        request.setCanonicalEvidenceTypeId("CompanyRegistration");
        request.setDataOwnerId("iso6523-actorid-upis::9999:AT000000271");
        ResponseLookupRoutingInformationType response = client.getProvisions(request);
        assertNotNull(response);
        assertEquals("BUNDESMINISTERIUM FUER DIGITALISIERUNG UND WIRTSCHAFTSSTANDORT (BMDW)", response.getAvailableSources().getSourceAtIndex(0)
                .getProvisionItems().getProvisionItemAtIndex(0).getDataOwnerPrefLabel());
    }
}