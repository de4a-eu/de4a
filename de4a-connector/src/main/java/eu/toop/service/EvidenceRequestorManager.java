package eu.toop.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.mime.CMimeType;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.smp.NodeInfo;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.as4.client.regrep.CletusLevelTransformer;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;
import eu.toop.rest.Client;

@Component 
public class EvidenceRequestorManager extends EvidenceManager{

	private static final Logger logger = LoggerFactory.getLogger (EvidenceRequestorManager.class);
	@Value("${as4.me.id}")
	private String meId;
	@Value("${as4.me.id.jvm:#{null}}")
	private String meIdjvm; 
	@Value("${as4.another.id}")
	private String anotherId;
	@Value("${as4.another.id.jvm:#{null}}") 
	private String anotherIdjvm;
	@Value("${as4.evidence.service}")
	private String evidenceServiceUri;
	@Autowired
	private Client clientSmp; 
	 
	  public boolean manageRequest(RequestTransferEvidence request) {
		String from=meId.isEmpty ()?meIdjvm:meId;
		String to=anotherId.isEmpty()?anotherIdjvm:anotherId;
		request.getDataOwner().setId(to);
		request.getDataOwner().setName("Name of "+to);
		Document doc=marshall(request);  
//		meId="9914:tc-ng-test-sender";
//		anotherId= "9915:tooptest";
		
		return sendRequestMessage(from, to, evidenceServiceUri, doc.getDocumentElement());
	  }
	private Document marshall(RequestTransferEvidence request ) {   
		        try
		        {
//		            JAXBContext jaxbContext = JAXBContext.newInstance(RequestTransferEvidence.class);
//		            Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
//		            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//		            StringWriter sw = new StringWriter(); 
//		            jaxbMarshaller.marshal(request, sw); 
//		            return DOMUtils.stringToDocument(sw.toString()); 
		        	
		        	JAXBContext jc = JAXBContext.newInstance(RequestTransferEvidence.class);
 

		            // Create the Document
		            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		            dbf.setNamespaceAware(true);
		            DocumentBuilder db = dbf.newDocumentBuilder();
		            Document document = db.newDocument();

		            // Marshal Object to the Document
		            Marshaller marshaller = jc.createMarshaller();
		            marshaller.marshal(request, document); 
		            return document;
		        } catch (JAXBException | ParserConfigurationException e) {
		           logger.error("Error building request DOM",e);
		           return null;
		        } 
	}
	public boolean sendRequestMessage(String sender,String dataOwnerId,String evidenceServiceUri, Element userMessage) {
		NodeInfo nodeInfo=clientSmp.getNodeInfo(dataOwnerId,evidenceServiceUri);
		try {//nodeInfo.setEndpoint("https://eu-domibus-server.redsara.es/domibus/services/msh");sender="domibus-blue" urn:oasis:names:tc:ebcore:partyid-type:unregistered
			logger.debug("Sending  message to as4 gateway ..."); 
			Element requestSillyWrapper=new CletusLevelTransformer().wrapMessage(userMessage, true);
			List<TCPayload> payloads=new ArrayList<TCPayload>();
			 TCPayload p=new TCPayload();
			 p.setContentID(DE4AConstants.TAG_EVIDENCE_REQUEST);
			 p.setMimeType(CMimeType.APPLICATION_XML.getAsString ());
			 p.setValue(DOMUtils.documentToByte(userMessage.getOwnerDocument()));
			 payloads.add(p);
			as4Client.sendMessage(sender,nodeInfo,evidenceServiceUri,requestSillyWrapper,payloads,true);
			return true;
		}  catch (MEOutgoingException e) {
			logger.error("Error with as4 gateway comunications",e);
		} catch (MessageException e) {
			logger.error("Error building regrep message",e);
		}
		return false;
	}
	

}
