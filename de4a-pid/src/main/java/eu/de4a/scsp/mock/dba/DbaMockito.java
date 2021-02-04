package eu.de4a.scsp.mock.dba; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.connector.api.rest.TCPayload;
@Component
public class DbaMockito implements OwnerGateway{  
	private static final Logger logger = LogManager.getLogger(DbaMockito.class);
	private static final String XPATH_LEGAL_ID="//*[local-name()='LegalEntityIdentifier']";
	@Value("${as4.pid.owner.endpoint}")
	private String endpoint; 
	@Autowired
	private DbaRepository dbaRepository;
	public List<TCPayload> sendEvidenceRequest(org.w3c.dom.Element evidenceRequest) throws MessageException{
		if(logger.isDebugEnabled()) { 
			logger.debug("Request to DBA Mockito: {}",DOMUtils.documentToString(evidenceRequest.getOwnerDocument()));
		}
		Node id=DOMUtils.getNodeFromXpath(XPATH_LEGAL_ID, evidenceRequest);
		Entity e=dbaRepository.selectEntity(id.getNodeValue());
		return null;
	} 
	 
	private  TCPayload  makePayload (String name,String mimetype,byte[]data){ 
			 
				TCPayload payload=new TCPayload();
				payload.setContentID(name);
				payload.setMimeType(mimetype); 
					payload.setValue(data); 
		return payload ;
	}
	 
	 
	
}
 