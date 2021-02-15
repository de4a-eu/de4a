package eu.de4a.scsp.ws.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
		//ByteArrayInputStream tmp = new ByteArrayInputStream(DOMUtils.documentToByte(request.getOwnerDocument()));
		ByteArrayInputStream tmp = new ByteArrayInputStream(doc2bytes(request ));
		try { 
			StreamSource source = new StreamSource(tmp);
			StreamResult result = new StreamResult(out);
			this.setDefaultUri(endpointPid); 
			sendSourceAndReceiveToResult(source, result); 
			return DOMUtils.byteToDocument( out.toByteArray() ).getDocumentElement();
		} catch (Exception e) { e.printStackTrace();
			String err="Error:"+e.getMessage();  
			throw new MessageException(err  );
		}
	}
	public static byte[] doc2bytes(Node node) {
        try {
            Source source = new DOMSource(node);
            ByteArrayOutputStream out = new ByteArrayOutputStream(); 
            Result result = new StreamResult(out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return out.toByteArray();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
	public String getEndpointPid() {
		return endpointPid;
	}

	public void setEndpointPid(String endpointPid) {
		this.endpointPid = endpointPid;
	} 
	
}
