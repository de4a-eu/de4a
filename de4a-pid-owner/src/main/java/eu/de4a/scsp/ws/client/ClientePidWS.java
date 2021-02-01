package eu.de4a.scsp.ws.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.w3c.dom.Element;

import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
 

public class ClientePidWS extends org.springframework.ws.client.core.WebServiceTemplate{  
	
	private String endpointPid; 
	public ClientePidWS(AxiomSoapMessageFactory messageFactory) {
		super(messageFactory); 
		setMessageFactory(messageFactory);
	}
 
	public Element sendRequest(Element request) throws MessageException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		ByteArrayInputStream tmp = new ByteArrayInputStream(DOMUtils.documentToByte(request.getOwnerDocument())); 
		try { 
			StreamSource source = new StreamSource(tmp);
			StreamResult result = new StreamResult(out);
			this.setDefaultUri(endpointPid); 
			sendSourceAndReceiveToResult(source, result); 
			return DOMUtils.byteToDocument( out.toByteArray() ).getDocumentElement();
		} catch (Exception e) { 
			String err="Error:"+e.getMessage();  
			throw new MessageException(err  );
		}
	}

	public String getEndpointPid() {
		return endpointPid;
	}

	public void setEndpointPid(String endpointPid) {
		this.endpointPid = endpointPid;
	} 
	
}
