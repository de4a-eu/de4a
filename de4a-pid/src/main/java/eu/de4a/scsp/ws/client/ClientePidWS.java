package eu.de4a.scsp.ws.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.w3c.dom.Element;

import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
 

public class ClientePidWS extends org.springframework.ws.client.core.WebServiceTemplate{  
	
	private String endpointPid;
	private static final Log LOG = LogFactory.getLog(ClientePidWS.class);  
	public ClientePidWS(AxiomSoapMessageFactory messageFactory) {
		super(messageFactory); 
		setMessageFactory(messageFactory);
	}
 
	public Element sendRequest(Element request) throws MessageException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		ByteArrayInputStream tmp = new ByteArrayInputStream(DOMUtils.documentToByte(request.getOwnerDocument())); 
		try {
			if(LOG.isDebugEnabled()) {
				LOG.debug("-------------------------REQUEST--------------------------");
				LOG.debug(DOMUtils.documentToString(request.getOwnerDocument()));
				LOG.debug("------------------------/REQUEST--------------------------");
			} 
			StreamSource source = new StreamSource(tmp);
			StreamResult result = new StreamResult(out);
			this.setDefaultUri(endpointPid); 
			sendSourceAndReceiveToResult(source, result);
			LOG.debug("-------------------------RESPONSE--------------------------");
			LOG.debug(new String(out.toByteArray()));
			LOG.debug("------------------------/RESPONSE--------------------------");
			LOG.debug("Fin");  
			return DOMUtils.byteToDocument( out.toByteArray() ).getDocumentElement();
		} catch (Exception e) { 
			String err="Error:"+e.getMessage(); 
			LOG.error(err,e);
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
