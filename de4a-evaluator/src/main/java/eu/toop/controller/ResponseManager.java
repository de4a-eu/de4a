package eu.toop.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.helger.commons.mime.CMimeType;

import eu.de4a.evaluator.model.EvaluatorRequest;
import eu.de4a.evaluator.model.EvaluatorRequestData;
import eu.de4a.evaluator.repository.EvaluatorRequestDataRepository;
import eu.de4a.evaluator.repository.EvaluatorRequestRepository;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
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
	
	public void manageResponse(ResponseTransferEvidenceType response) throws MessageException {
		EvaluatorRequestData datarequest = new EvaluatorRequestData();
		byte[] data = DE4AMarshaller
				.drImResponseMarshaller(EDE4ACanonicalEvidenceType.NONE).getAsBytes(response);
		datarequest.setData(data);
		LOGGER.info("--->" 
				+ new String(DE4AMarshaller.drImResponseMarshaller(EDE4ACanonicalEvidenceType.NONE)
						.formatted().getAsString(response)));
		datarequest.setMimetype(CMimeType.APPLICATION_XML.getAsString());
		datarequest.setIddata(DE4AConstants.TAG_EVIDENCE_RESPONSE);
		EvaluatorRequest request = evaluatorRequestRepository.findById(response.getRequestId()).orElse(null);
		datarequest.setRequest(request);
		evaluatorRequestDataRepository.save(datarequest);
	}
	
	public void manageResponse(RequestForwardEvidenceType response) throws MessageException {
		EvaluatorRequestData datarequest = new EvaluatorRequestData();
		DE4AMarshaller<RequestForwardEvidenceType> marshaller = DE4AMarshaller.deUsiRequestMarshaller(
				EDE4ACanonicalEvidenceType.NONE);
		byte[] data = marshaller.getAsBytes(response);
		datarequest.setData(data);
		LOGGER.info("--->" 
				+ new String(marshaller.formatted().getAsString(response)));
		datarequest.setMimetype(CMimeType.APPLICATION_XML.getAsString());
		datarequest.setIddata(DE4AConstants.TAG_FORWARD_EVIDENCE_REQUEST);
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
 
}
