package eu.toop.smp.client;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.de4a.connector.api.manager.EvidenceRequestorManager;
import eu.de4a.connector.service.spring.Conf;

@SpringBootTest(classes={Conf.class})
  
@RunWith(SpringRunner.class) 
public class TestRequestor { 
	@Autowired
	private EvidenceRequestorManager evidenceRequestorManager;
	 
	@Test
	public void sendTestEvidenceRequest() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException { 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(this.getClass().getClassLoader()  .getResourceAsStream( "edm/request.xml"));


		
		evidenceRequestorManager.sendRequestMessage("9914:tc-ng-test-sender", "urn:eu:toop:ns:dataexchange-1p40::Response##urn:eu.toop.response.registeredorganization::1.40",
				 doc.getDocumentElement(), null);
	}
}
