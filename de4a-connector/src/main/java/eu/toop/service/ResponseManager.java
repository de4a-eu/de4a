package eu.toop.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import eu.de4a.conn.api.requestor.DomesticEvidenceType;
import eu.de4a.conn.api.requestor.DomesticsEvidencesType;
import eu.de4a.conn.api.requestor.ErrorType;
import eu.de4a.conn.api.requestor.IssuingTypeType;
import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.as4.client.ResponseWrapper;
import eu.toop.req.model.EvaluatorRequest;
import eu.toop.req.model.EvaluatorRequestData;
import eu.toop.req.repository.EvaluatorRequestDataRepository;
import eu.toop.req.repository.EvaluatorRequestRepository;
import eu.toop.rest.Client;
import eu.toop.service.spring.Conf;

@Component
@Aspect
public class ResponseManager {
	private static final Logger logger =  LoggerFactory.getLogger (Conf.class);
	@Autowired
	private Client client;
	@Autowired
	private EvaluatorRequestRepository evaluatorRequestRepository; 
	@Autowired
	private EvaluatorRequestDataRepository evaluatorRequestDataRepository;
	@AfterReturning(pointcut="execution(* *.processResponseAs4(..))", returning="retVal") 
	public void cathResponseFromMultipleAs4(Object retVal) {
		ResponseWrapper response=(ResponseWrapper)retVal; 
		String id=response.getId();//"request00000001@de4a";
		EvaluatorRequest evaluatorinfo=evaluatorRequestRepository.findById(id).orElse(null);
		if(evaluatorinfo==null) {
			logger.error("what are u  container me?id="+id);
		}else {
			evaluatorinfo.setDone(true);
			evaluatorRequestRepository.save(evaluatorinfo);
			saveData(response, evaluatorinfo);
			if(evaluatorinfo.isUsi()) {
				//Caso USI
				logger.debug("pushing data 2 {}",evaluatorinfo.getUrlreturn()); 
				client.pushEvidence(evaluatorinfo.getUrlreturn(), response);
			}    
		}
	}  
	private void saveData(ResponseWrapper response, EvaluatorRequest evaluator) {
		try {
			logger.debug("Saving data for response with id "+response.getId());
			List<EvaluatorRequestData>datas=new ArrayList<EvaluatorRequestData>(); 
			 for (MultipartFile part : response.getAttacheds()) {
		        	byte[] data =  part.getBytes(); 
		        	EvaluatorRequestData datarequest=new  EvaluatorRequestData();
	        		datarequest.setData(data);
	        		datarequest.setMimetype(part.getContentType());
	        		datarequest.setIddata( part.getOriginalFilename());   
		            datas.add(datarequest);
		        } 
			for(EvaluatorRequestData d:datas) { 
					d.setRequest(evaluator);
					evaluatorRequestDataRepository.save(d); 
			} 
		}catch(IOException io) {
			logger.error("Error saving evidence data",io);
		}
	}
//	el response manager debe enviar al evaluator lo que se mandaba en manageREsponse.
//	Despues se ha de hacer lo del plugin ws de domibus de checkear peticion pendiente y mandar respuesta.
//	despues probar a que el transferor tira con un domibus en un tomcat fuera del eclipse
//	
//	
	/*public void manageResponse(IncomingDe4aResponse response) {
		String id=response.getTopLevelContentID();
		EvaluatorRequest evaluatorinfo=evaluatorRequestRepository.findById(id).orElse(null);
		if(evaluatorinfo==null) {
			logger.error("what are u  container me?");
		}else {
			logger.debug("pushing data 2 ",evaluatorinfo.getUrlreturn()); 
			client.pushEvidence2(evaluatorinfo.getUrlreturn(),  response  ,id );
			evaluatorinfo.setDone(true);
			evaluatorRequestRepository.save(evaluatorinfo);
		}
	}*/
	 public boolean isDone(String id) {
		 EvaluatorRequest evaluator=evaluatorRequestRepository.findById(id).orElse(null);
		 return evaluator==null?false:evaluator.isDone();
	 }
	 public ResponseTransferEvidence getResponse(String id) throws MessageException {
		 logger.debug("Makins ResponseTransferEvidence with id ",id);
		 EvaluatorRequest evaluator=evaluatorRequestRepository.findById(id).orElse(null);
		 EvaluatorRequestData data=new EvaluatorRequestData();
		 data.setRequest(evaluator);
		 Example<EvaluatorRequestData> example = Example.of(data);
		 List<EvaluatorRequestData>filesAttached=evaluatorRequestDataRepository.findAll(example); 
		 data=filesAttached.stream().filter(p->p.getIddata().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)).findFirst().orElse(null);
		 if(data!=null) {
			 JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(eu.de4a.conn.api.requestor.ResponseTransferEvidence.class);
				javax.xml.bind.Unmarshaller jaxbMarshaller = (Unmarshaller) jaxbContext.createUnmarshaller() ;  
				ResponseTransferEvidence response= (ResponseTransferEvidence) jaxbMarshaller.unmarshal( new ByteArrayInputStream(data.getData()) );
				filesAttached.forEach(a->{
					if(!a.getIddata().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)) {
						DomesticEvidenceType domestic= response.getDomesticEvidenceList().getDomesticEvidence().stream().filter(p->p.getDomesticEvidenceIdRef().equals(id)).findFirst().orElse(null);
						if(domestic==null) {
							 logger.error("It hasn´t been located a domestic evidence with id "+id);
						}else {
							 domestic.setEvidenceData(a.getData());
						}
					} 
				}); 
				DomesticEvidenceType dom=new DomesticEvidenceType();
				dom.setAddtionalInfo("add");
				dom.setDataLanguage("es");
				dom.setDomesticEvidenceIdRef("id");
				dom.setEvidenceData("tu padr".getBytes());
				dom.setIssuingType(IssuingTypeType.ORIGINAL_ISSUING);
				dom.setMimeType("app");
				DomesticsEvidencesType  list=new   DomesticsEvidencesType();
				list.getDomesticEvidence().add(dom);
				response.setDomesticEvidenceList(list);;
				logger.error(jaxbObjectToXML(response));
				return response;
			} catch (JAXBException e) {
				logger.error("Error unmarshalling ResponseTransferEvidence",e);
				throw new MessageException(String.format("Error unmarshalling ResponseTransferEvidence en request %s. %s",id,e.getMessage()));
			} 
		 }
		 throw new MessageException("It´s not exists a ResponseTransferEvidence for ID:"+id);
	 } 
	 
	 private String jaxbObjectToXML(ResponseTransferEvidence request) 
	    {
	        try
	        { 
	            JAXBContext jaxbContext = JAXBContext.newInstance(ResponseTransferEvidence.class); 
	            Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
	            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); 
	            StringWriter sw = new StringWriter(); 
	            jaxbMarshaller.marshal(request, sw); 
	            return sw.toString(); 
	 
	        } catch (JAXBException e) {
	            logger.error("Error marshalling object",e);
	            return "";
	        }
	    }
	 public ResponseTransferEvidence getErrorResponse(MessageException ex)  {
		 ResponseTransferEvidence error=new ResponseTransferEvidence();
		 ErrorType errortype =new ErrorType();
		 errortype.setCode(ex.getCode());
		 errortype.setText(ex.getMessage());
		 error.setError(errortype);
		 return error;
	 }
	
}
