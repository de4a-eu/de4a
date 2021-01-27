package eu.de4a.scsp.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import com.helger.commons.mime.CMimeType;

import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.scsp.translate.EvidenceMapper;
import eu.de4a.scsp.translate.EvidenceTranslator;
import eu.de4a.scsp.ws.client.ClientePidWS;
import eu.de4a.util.DE4AConstants;
import eu.toop.connector.api.rest.TCPayload;
@Component
public class ScspGateway implements OwnerGateway{

	private static final Logger logger = LogManager.getLogger(ScspGateway.class);
	@Autowired
	private ClientePidWS clientePidWS;
	@Autowired
	private EvidenceMapper evidenceMapper;
	public List<TCPayload> sendEvidenceRequest(org.w3c.dom.Element evidenceRequest,String evidenceServiceUri) throws MessageException{
		if(logger.isDebugEnabled()) {
			logger.debug("Requesting service {}",evidenceServiceUri);
			logger.debug("Request: {}",DOMUtils.documentToString(evidenceRequest.getOwnerDocument()));
		}
		EvidenceTranslator translator=evidenceMapper.getTranslator(evidenceServiceUri);
		Element scspRequest=translator.translateEvidenceRequest(evidenceRequest);
		Element scspResponse=(Element) clientePidWS.sendRequest(scspRequest);
		String id=DOMUtils.getNodeFromXpath(String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_REQUEST), evidenceRequest).getNodeValue();
		Element evidenceResponse= translator.translateEvidenceResponse(scspResponse) ;
		DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(), String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_RESPONSE), id);
		List<TCPayload> payloads=new ArrayList<TCPayload>();
		
		TCPayload p=new TCPayload();
		 p.setContentID(DE4AConstants.TAG_EVIDENCE_RESPONSE);
		 p.setMimeType(CMimeType.APPLICATION_XML.getAsString ());
		 p.setValue(DOMUtils.documentToByte(evidenceResponse.getOwnerDocument()));
		 payloads.add(p);
		 
		 TCPayload scsp=new TCPayload();
		 scsp.setContentID(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE);
		 scsp.setMimeType(CMimeType.APPLICATION_XML.getAsString ());
		 scsp.setValue(DOMUtils.documentToByte(scspResponse.getOwnerDocument()));
		 payloads.add(scsp);
		return payloads;
		
		
		/* List<TCPayload> payloads=new ArrayList<TCPayload>();
			 TCPayload p=new TCPayload();
			 p.setContentID(DE4AConstants.TAG_EVIDENCE_RESPONSE);
			 p.setMimeType(CMimeType.APPLICATION_XML.getAsString ());
			 p.setValue(DOMUtils.documentToByte(canonical));*/
	}
}
