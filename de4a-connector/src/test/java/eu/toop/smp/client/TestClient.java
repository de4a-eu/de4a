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

@SpringBootTest(classes={Conf.class}, properties = {"smp.endpoint=https://de4a-smp.egovlab.eu/",
        "idk.endpoint=https://de4a-dev-idk.egovlab.eu/",
		"truststore.type=JKS",
		"truststore.path=truststore/de4a-truststore-test-smp-pw-de4a.jks",
		"truststore.password=de4a"})
@RunWith(SpringRunner.class)
public class TestClient {
	@Autowired
	private Client client;
	
	@Test
	public void testSmpNode() {
		NodeInfo info=client.getNodeInfo("9999:egov","urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration", true, null);
		assertNotNull(info);
		assertEquals("https://de4a-dev-connector.egovlab.eu/phase4", info.getEndpointURI ());
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