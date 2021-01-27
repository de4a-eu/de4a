package eu.toop.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.helger.commons.mime.CMimeType;

import eu.de4a.conn.api.requestor.DomesticEvidenceType;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.req.model.EvaluatorRequest;
import eu.toop.req.model.EvaluatorRequestData;
import eu.toop.req.repository.EvaluatorRequestDataRepository;
import eu.toop.req.repository.EvaluatorRequestRepository; 
@Component 
public class ResponseManager {
	private static final Logger LOGGER = LoggerFactory.getLogger (ResponseManager.class);
	@Autowired
	private EvaluatorRequestRepository evaluatorRequestRepository;
	@Autowired
	private EvaluatorRequestDataRepository evaluatorRequestDataRepository;
//	public void manageResponse(ResponseTransferEvidence responseEvidence,MultipartFile [] files) {
//		String id=responseEvidence.getRequestId();
//		EvaluatorRequest request=evaluatorRequestRepository.findById(id).orElse(null);
//    	if(request==null) {
//    		LOGGER.error("Request cannot be recoveried ....what kind of id has been set? id:{}",id);
//    	}else {
//    		EvaluatorRequestData datarequest=new  EvaluatorRequestData();
//    		Document response= marshallMe(responseEvidence);
//    		datarequest.setData(DOMUtils.documentToByte(response));
//    		datarequest.setMimetype(CMimeType.APPLICATION_XML.toString());
//    		datarequest.setIddata(ResponseTransferEvidence.class.getName());   
//    		List<DomesticEvidenceType> domesticos=(List<DomesticEvidenceType>) responseEvidence.getDomesticEvidenceList().getDomesticEvidence();
//    		for(DomesticEvidenceType dom:domesticos) {
//    			try {
//					filling(request, dom,files);
//				} catch (IOException e) {
//					LOGGER.error("Error processing attached file",e);
//				}
//    		}
//    		
//    	}
//    	
//	}
	private void filling(  EvaluatorRequest request,List<EvaluatorRequestData> datas) throws IOException {
		if(datas==null ||datas.size()==0) {
			LOGGER.error("Not received attacheds");
		}else {
			for(EvaluatorRequestData d:datas) { 
					d.setRequest(request);
					evaluatorRequestDataRepository.save(d); 
			}
			
		} 
		
	}
	private Document marshallMe(ResponseTransferEvidence evidence) {  
	        try
	        {
	            JAXBContext jaxbContext = JAXBContext.newInstance(ResponseTransferEvidence.class);
	            Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
	            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	            StringWriter sw = new StringWriter(); 
	            jaxbMarshaller.marshal(evidence, sw); 
	            return DOMUtils.stringToDocument(sw.toString()); 
	 
	        } catch (JAXBException e) {
	        	LOGGER.error("Error building request DOM",e);
	           return null;
	        }  
	}
	public void manageResponse(MultipartFile[] files) throws IOException, MessageException {
		LOGGER.debug("Response received"); 
		String  id=null; 
		List<EvaluatorRequestData>datas=new ArrayList<EvaluatorRequestData>(); 
	        for (MultipartFile part : files) {
	        	byte[] data =  part.getBytes()  ;
				 
	        	EvaluatorRequestData datarequest=new  EvaluatorRequestData();
        		datarequest.setData(data);
        		datarequest.setMimetype(part.getContentType());
        		datarequest.setIddata( part.getOriginalFilename());  
	            if ( part.getOriginalFilename() .equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)) {
	            	 Document doc=DOMUtils.byteToDocument(data);
	            	 id=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, doc.getDocumentElement());
	            }
	            datas.add(datarequest);
	        }
	        
	     //  id="request00000001@de4a";
	        if(id==null) {
	        	LOGGER.error("No Id, No evidence response to the citizen...ko!");
	        }else {
	        	EvaluatorRequest request=evaluatorRequestRepository.findById(id).orElse(null);
	        	if(request==null) {
	        		LOGGER.error("Request cannot be recoveried ....what kind of id has been set? id:{}",id);
	        	}else {
	        		filling(request, datas);
	        	}
	        }
	        
	}
//	private void filling(  EvaluatorRequest request,List<EvaluatorRequestData>datas) {
//		datas.forEach(d->{ 
//			 d.setRequest(request);
//			 x
//		 });
//	}
	private byte[]  getBytes(Part part) {
		try  {
            return IOUtils.toByteArray(part.getInputStream());
        } catch (IOException e) {
			LOGGER.error("Error getting inputstream from multipart response",e);
			return null;
		}	
	}
	 
 
}
