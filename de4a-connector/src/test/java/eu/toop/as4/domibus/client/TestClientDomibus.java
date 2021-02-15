package eu.toop.as4.domibus.client; 

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.toop.as4.domibus.soap.ClienteWS;
import eu.toop.as4.domibus.soap.DomibusException;
import eu.toop.as4.domibus.soap.MessageFactory;
import eu.toop.as4.domibus.soap.ResponseAndHeader;
import eu.toop.as4.domibus.soap.auto.LargePayloadType;
import eu.toop.as4.domibus.soap.auto.Messaging;
import eu.toop.as4.domibus.soap.auto.PartInfo;
import eu.toop.as4.domibus.soap.auto.PartProperties;
import eu.toop.as4.domibus.soap.auto.Property;
import eu.toop.as4.domibus.soap.auto.RetrieveMessageResponse;
import eu.toop.service.spring.Conf;

@SpringBootTest(classes={Conf.class})
  
@RunWith(SpringRunner.class) 
public class TestClientDomibus { 
	@Autowired
	private ClienteWS clienteWS;
	//@Test
	public void getMessageID() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException,DomibusException { 
		String id="d6cddccf-288f-44b9-839c-f3e1e5630770@domibus.eu";
		ResponseAndHeader  message=clienteWS.getMessageWithHeader(id);
		
		byte[] targetArray = IOUtils.toByteArray(message.getMessage().getPayload().get(0).getValue().getDataSource().getInputStream());
		if( Base64.isBase64(targetArray)) {
			targetArray= Base64.decodeBase64(targetArray);
		} 
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setNamespaceAware(true);
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc= builder.parse(new ByteArrayInputStream(targetArray)); 
	}
	@Test
	public void sendTest() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException,DomibusException { 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(this.getClass().getClassLoader()  .getResourceAsStream( "edm/request.xml")); 
//		evidenceRequestorManager.sendRequestMessage("9914:tc-ng-test-sender", "9915:tooptest",  "urn:eu:toop:ns:dataexchange-1p40::Response##urn:eu.toop.response.registeredorganization::1.40","777"
//				, doc.getDocumentElement());
        String messageId="777";
        List<PartInfo> attacheds = new ArrayList<PartInfo>();
        PartInfo partInfo=new PartInfo();
        String idmessageattached="cid:message";
        partInfo.setHref(idmessageattached);
        Property prop=new Property();
        prop.setName("MimeType");
        prop.setValue("application/xml");
        PartProperties props=new PartProperties();
        props.getProperty().add(prop);
        partInfo.setPartProperties(props);   
        attacheds.add(partInfo);
        Messaging messageHeader= MessageFactory.makeMessage("domibus-blue", "red_gw",messageId, "TC1Leg1", attacheds);
        List<LargePayloadType> bodies =new ArrayList<LargePayloadType>();
        LargePayloadType payload=new LargePayloadType();
        payload.setContentType("application/xml");
        payload.setPayloadId(idmessageattached);
        DataSource source = new ByteArrayDataSource(documentToByte(doc),"text/xml");

        payload.setValue(new DataHandler(source));
        bodies.add(payload);
        
        LargePayloadType payload2=new LargePayloadType();
        payload2.setContentType("application/xml");
        payload2.setPayloadId("cid:EvidenceResponse");
        DataSource source2 = new ByteArrayDataSource(documentToByte(doc),"text/xml");

        payload2.setValue(new DataHandler(source2));
        bodies.add(payload2);
        
        
        clienteWS.submitMessage( messageHeader, bodies);
	}
	 private byte[] documentToByte(Document document)
	  { 
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      org.apache.xml.security.utils.XMLUtils.outputDOM(document, baos, true);
	      return baos.toByteArray();
	  }
}