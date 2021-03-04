package eu.toop.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.helger.commons.mime.CMimeType;

import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.exception.MessageException;
import eu.de4a.model.EvaluatorRequest;
import eu.de4a.model.EvaluatorRequestData;
import eu.de4a.repository.EvaluatorRequestDataRepository;
import eu.de4a.repository.EvaluatorRequestRepository;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;

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
//	            JAXBContext jaxbContext = JAXBContext.newInstance(ResponseTransferEvidence.class);
//	            Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
//	            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//	            StringWriter sw = new StringWriter(); 
//	            jaxbMarshaller.marshal(evidence, sw); 
//	            LOGGER.error(sw.toString());
//	            return DOMUtils.stringToDocument(sw.toString()); 
	        	JAXBContext jaxbContext = JAXBContext.newInstance(ResponseTransferEvidence.class);
 		        Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
	        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        	dbf.setNamespaceAware(true);
	        	DocumentBuilder db = dbf.newDocumentBuilder();
	        	Document doc = db.newDocument(); 
	        	jaxbMarshaller.marshal( evidence, doc );
	        	return doc;
	        } catch (JAXBException | ParserConfigurationException e) {
	        	LOGGER.error("Error building request DOM",e);
	           return null;
	        }  
	}
	
	public void manageResponse(ResponseTransferEvidence response) throws MessageException {
		EvaluatorRequestData datarequest = new EvaluatorRequestData();
		// DOMUtils.decodeCompressed(response.getDomesticEvidenceList().getDomesticEvidence().get(0).getEvidenceData());
		byte[] data = DOMUtils.serializeJaxbObject(ResponseTransferEvidence.class, response);
		datarequest.setData(data);
		ResponseTransferEvidence r = (ResponseTransferEvidence) DOMUtils.unmarshall(ResponseTransferEvidence.class,
				DOMUtils.byteToDocument(data));
		// DOMUtils.decodeCompressed(r.getDomesticEvidenceList().getDomesticEvidence().get(0).getEvidenceData());

		// DOMUtils.decodeCompressed(response.getDomesticEvidenceList().getDomesticEvidence().get(0).getEvidenceData());
		LOGGER.info("--->" + new String(datarequest.getData()));
		datarequest.setMimetype(CMimeType.APPLICATION_XML.getAsString());
		datarequest.setIddata(DE4AConstants.TAG_EVIDENCE_RESPONSE);
		EvaluatorRequest request = evaluatorRequestRepository.findById(response.getRequestId()).orElse(null);
		datarequest.setRequest(request);
		evaluatorRequestDataRepository.save(datarequest);
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
