package eu.toop.as4.client;

 

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.security.cert.CertificateException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.mime.MimeTypeParser;
import com.helger.commons.string.StringHelper;

import eu.de4a.conn.api.smp.NodeInfo;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.connector.api.TCIdentifierFactory;
import eu.toop.connector.api.me.EMEProtocol;
import eu.toop.connector.api.me.IMessageExchangeSPI;
import eu.toop.connector.api.me.MessageExchangeManager;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.model.MEMessage;
import eu.toop.connector.api.me.model.MEPayload;
import eu.toop.connector.api.me.outgoing.IMERoutingInformation;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.me.outgoing.MERoutingInformation;
import eu.toop.connector.api.rest.TCOutgoingMessage;
import eu.toop.connector.api.rest.TCOutgoingMetadata;
import eu.toop.connector.api.rest.TCPayload;
import eu.toop.connector.api.rest.TCRestJAXB; 
@Component
public class Phase4GatewayClient implements As4GatewayInterface{
  private static final Logger LOGGER = LoggerFactory.getLogger (Phase4GatewayClient.class);
  static{
	  org.apache.xml.security.Init.init();
  }
  public   void sendMessage(String sender,NodeInfo receiver, String evidenceServiceUri,Element requestUsuario,List<TCPayload>payloads,boolean request) throws MEOutgoingException {
    final TCOutgoingMessage aOM = new TCOutgoingMessage ();
    {
    	
    	/***"AS4 Submit message:
- partyIdAuthCredentials
- service
- action
- conversationId
- originalSender
- finalRecipient
- payload"
*/
      final TCOutgoingMetadata aMetadata = new TCOutgoingMetadata ();
      aMetadata.setSenderID (TCRestJAXB.createTCID (TCIdentifierFactory.PARTICIPANT_SCHEME, sender ));
      aMetadata.setReceiverID (TCRestJAXB.createTCID (TCIdentifierFactory.PARTICIPANT_SCHEME, receiver.getParticipantIdentifier()));
      aMetadata.setDocTypeID (TCRestJAXB.createTCID (TCIdentifierFactory.DOCTYPE_SCHEME,  evidenceServiceUri));
      aMetadata.setProcessID (TCRestJAXB.createTCID (TCIdentifierFactory.PROCESS_SCHEME, "bdx:noprocess"));
      aMetadata.setTransportProtocol (EMEProtocol.AS4.getTransportProfileID ()); 
      aMetadata.setEndpointURL (receiver.getEndpointURI()); 
      aMetadata.setReceiverCertificate (receiver.getCertificate());
      aOM.setMetadata (aMetadata);
    }
    {
      final TCPayload aPayload = new TCPayload (); 
      aPayload.setValue (documentToByte(requestUsuario.getOwnerDocument())); 
      aPayload.setMimeType (CMimeType.APPLICATION_XML.getAsString ());
      aPayload.setContentID ("message");
      aOM.addPayload (aPayload);
      if(payloads!=null) {
    	  payloads.forEach(p->{
    		  final TCPayload aPayloadTmp = new TCPayload (); 
    		  aPayloadTmp.setValue (p.getValue()); 
    		  aPayloadTmp.setMimeType (p.getMimeType());
    		  aPayloadTmp.setContentID (p.getContentID());
    	      aOM.addPayload (aPayloadTmp);
    	  });
      }
    }

    LOGGER.info (TCRestJAXB.outgoingMessage ().getAsString (aOM));
    send(aOM); 
  }
  private void send (TCOutgoingMessage aOutgoingMsg) throws  MEOutgoingException    {
	     // These fields are optional in the XSD but required here
	    if (StringHelper.hasNoText (aOutgoingMsg.getMetadata ().getEndpointURL ()))
	      throw new MEOutgoingException  ("The 'OutgoingMessage/Metadata/EndpointURL' element MUST be present and not empty");
	    if (ArrayHelper.isEmpty (aOutgoingMsg.getMetadata ().getReceiverCertificate ()))
	      throw new MEOutgoingException  ("The 'OutgoingMessage/Metadata/ReceiverCertificate' element MUST be present and not empty");

	    // Convert metadata
	    final IMERoutingInformation aRoutingInfo;
	    try
	    {
	      aRoutingInfo = MERoutingInformation.createFrom (aOutgoingMsg.getMetadata ());
	    }
	    catch (final CertificateException ex)
	    {
	      throw new MEOutgoingException  ("Invalid routing information provided: " + ex.getMessage ());
	    }

	    // Add payloads
	    final MEMessage.Builder aMessage = MEMessage.builder ();
	    for (final TCPayload aPayload : aOutgoingMsg.getPayload ())
	    {
	      aMessage.addPayload (MEPayload.builder ()
	                                    .mimeType (MimeTypeParser.parseMimeType (aPayload.getMimeType ()))
	                                    .contentID (StringHelper.getNotEmpty (aPayload.getContentID (),
	                                                                          MEPayload.createRandomContentID ()))
	                                    .data (aPayload.getValue ()));
	    } 
	    IMessageExchangeSPI aMEM = MessageExchangeManager.getConfiguredImplementation ();
	    aMEM.sendOutgoing (aRoutingInfo, aMessage.build()); 
  }  
	public ResponseWrapper processResponseAs4(IncomingEDMResponse responseas4) {
	  LOGGER.debug("Procesando respuesta AS4..."); 
	  ResponseWrapper responsewrapper=new ResponseWrapper();
	  var wrapper = new Object(){ String id=null; };
	  responseas4.getAllAttachments().forEachValue(a->{
			responsewrapper.addAttached(getMultipart(a.getContentID(), a.getMimeTypeString( ), a.getData().bytes()));
//	    	builder.addBinaryBody(a.getContentID(),a.getData().bytes(),ContentType.create(a.getMimeTypeString( ))  , a.getContentID()) ;
			if(a.getContentID().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)) {
	    		Document evidence=null;
				try {
					evidence = DOMUtils.byteToDocument(a.getData().bytes());
				} catch (MessageException e1) {
					LOGGER.error("Error managing evidence dom",e1);
				}
	    		try {
	    			 wrapper.id=DOMUtils.getValueFromXpath(String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_RESPONSE),evidence.getDocumentElement());
	    			 responsewrapper.setCanonicalEvidence(unmarshallMe( evidence));
//	    			 builder.addTextBody("id" ,wrapper.id);
	    		} catch (MessageException e) {
	    			LOGGER.error("Error handling request id",e);
	    		}
	    	}
	    });  
		  
		
	   // builder.addTextBody("id" ,"request00000001@de4a"); 
	  
	    responsewrapper.setId(wrapper.id );  
	    return responsewrapper;
	}
	private  eu.de4a.conn.api.requestor.ResponseTransferEvidence   unmarshallMe(Document evidence) {  
        try
        { 
            JAXBContext jaxbContext = JAXBContext.newInstance(eu.de4a.conn.api.requestor.ResponseTransferEvidence.class);
            Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller() ;   
            return (eu.de4a.conn.api.requestor.ResponseTransferEvidence) jaxbMarshaller.unmarshal(evidence); 
 
        } catch (JAXBException e) {
        	LOGGER.error("Error building request DOM",e);
           return null;
        }  
}
	private MultipartFile getMultipart(String label,String mimetype,byte[] data)   { 
		File tempFile = null; 
		try {
			tempFile = File.createTempFile("de4a-", null);
			 
			DiskFileItem item= new  org.apache.commons.fileupload.disk.DiskFileItem
					(  label, mimetype, false ,label,   1, tempFile.getParentFile());
			ByteArrayInputStream in=new ByteArrayInputStream(data);
			OutputStream out=item.getOutputStream();
			IOUtils.copy(in,  out);
			in.close();
			out.close();
			return new  CommonsMultipartFile(item);
		} catch (IOException e1) {
			LOGGER.error("Error attaching files",e1);
		} 
		return null ;
	
		
	}
  private byte[] documentToByte(Document document)
  { 
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      org.apache.xml.security.utils.XMLUtils.outputDOM(document, baos, true);
      return baos.toByteArray();
  }
	
}
