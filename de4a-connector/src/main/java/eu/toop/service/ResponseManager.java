package eu.toop.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import eu.de4a.conn.api.requestor.ErrorType;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.exception.MessageException;
import eu.de4a.model.EvaluatorRequest;
import eu.de4a.model.EvaluatorRequestData;
import eu.de4a.repository.EvaluatorRequestDataRepository;
import eu.de4a.repository.EvaluatorRequestRepository;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.toop.as4.client.ResponseWrapper;
import eu.toop.rest.Client;

@Component
@Aspect
@Transactional
public class ResponseManager {
	private static final Logger logger = LoggerFactory.getLogger(ResponseManager.class);
	@Autowired
	private Client client;
	@Autowired
	private EvaluatorRequestRepository evaluatorRequestRepository;
	@Autowired
	private EvaluatorRequestDataRepository evaluatorRequestDataRepository;
	@PersistenceContext
    private EntityManager entityManager;
	@Autowired
	private JpaTransactionManager transactionManager;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	@AfterReturning(pointcut = "execution(* *.processResponseAs4(..))", returning = "retVal")
	public void cathResponseFromMultipleAs4(Object retVal) {
		ResponseWrapper response = (ResponseWrapper) retVal;
		String id = response.getId();
		EvaluatorRequest evaluatorinfo = evaluatorRequestRepository.findById(id).orElse(null);
		if (evaluatorinfo == null) {
			logger.error("what are u  container me?id=" + id);
		} else {
			evaluatorinfo.setDone(true);
			evaluatorRequestRepository.save(evaluatorinfo);
			List<EvaluatorRequestData> responseData = saveData(response, evaluatorinfo);			
			if (evaluatorinfo.isUsi()) { // Mandar objeto de respuesta al evaluator
				// Caso USI
				logger.debug("Pushing data to {}", evaluatorinfo.getUrlreturn());
				ResponseTransferEvidence responseTransferEvidence = getElementFromAttached(responseData);
				client.pushEvidence(evaluatorinfo.getUrlreturn(), responseTransferEvidence);
			}
		}
	}

	private List<EvaluatorRequestData> saveData(ResponseWrapper response, EvaluatorRequest evaluator) {
		try {
			logger.debug("Saving data for response with id " + response.getId());
			List<EvaluatorRequestData> datas = new ArrayList<EvaluatorRequestData>();
			for (MultipartFile part : response.getAttacheds()) {
				byte[] data = part.getBytes();
				EvaluatorRequestData dataresponse = new EvaluatorRequestData();
//		        	try {
//						ResponseTransferEvidence r=(ResponseTransferEvidence) DOMUtils.unmarshall(ResponseTransferEvidence.class, DOMUtils.byteToDocument(data));
//						Document d= DOMUtils.decodeCompressed(r.getDomesticEvidenceList().getDomesticEvidence().get(0).getEvidenceData())  ;
//						d.getFirstChild();
//					  } catch (MessageException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				dataresponse.setData(data);
				dataresponse.setMimetype(part.getContentType());
				dataresponse.setIddata(part.getOriginalFilename());
				datas.add(dataresponse);
			}
			for (EvaluatorRequestData d : datas) {
				d.setRequest(evaluator);
				evaluatorRequestDataRepository.save(d);
			}
			return datas;
		} catch (IOException io) {
			logger.error("Error saving evidence data", io);
			return new ArrayList<>();
		}
	}

//	el response manager debe enviar al evaluator lo que se mandaba en manageREsponse.
//	Despues se ha de hacer lo del plugin ws de domibus de checkear peticion pendiente y mandar respuesta.
//	despues probar a que el transferor tira con un domibus en un tomcat fuera del eclipse
//	
//	
	/*
	 * public void manageResponse(IncomingDe4aResponse response) { String
	 * id=response.getTopLevelContentID(); EvaluatorRequest
	 * evaluatorinfo=evaluatorRequestRepository.findById(id).orElse(null);
	 * if(evaluatorinfo==null) { logger.error("what are u  container me?"); }else {
	 * logger.debug("pushing data 2 ",evaluatorinfo.getUrlreturn());
	 * client.pushEvidence2(evaluatorinfo.getUrlreturn(), response ,id );
	 * evaluatorinfo.setDone(true); evaluatorRequestRepository.save(evaluatorinfo);
	 * } }
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean isDone(String id) {
		entityManager.clear();
		EvaluatorRequest evaluator = evaluatorRequestRepository.findById(id).orElse(null);		
		return evaluator == null ? false : evaluator.isDone();
	}

	public ResponseTransferEvidence getResponse(String id) throws MessageException {
		logger.debug("Processing ResponseTransferEvidence with id ", id);
		
		EvaluatorRequest evaluator = evaluatorRequestRepository.findById(id).orElse(null);
		EvaluatorRequestData data = new EvaluatorRequestData();
		data.setRequest(evaluator);
		Example<EvaluatorRequestData> example = Example.of(data);
		List<EvaluatorRequestData> filesAttached = evaluatorRequestDataRepository.findAll(example);
		if(!CollectionUtils.isEmpty(filesAttached)) {
			return getElementFromAttached(filesAttached);
			// JAXBContext jaxbContext;
			// try {
			// jaxbContext =
			// JAXBContext.newInstance(eu.de4a.conn.api.requestor.ResponseTransferEvidence.class);
			// javax.xml.bind.Unmarshaller jaxbMarshaller = (Unmarshaller)
			// jaxbContext.createUnmarshaller() ;
			// logger.info(new String(data.getData()));
			// Document doc=DOMUtils.byteToDocument(data.getData());
			// ResponseTransferEvidence response= (ResponseTransferEvidence)
			// jaxbMarshaller.unmarshal(doc);
			// logger.info(DOMUtils.jaxbObjectToXML(response,
			// ResponseTransferEvidence.class));
			// return response;
			// } catch (JAXBException e) {
			// logger.error("Error unmarshalling ResponseTransferEvidence",e);
			// throw new MessageException(String.format("Error unmarshalling
			// ResponseTransferEvidence en request %s. %s",id,e.getMessage()));
			// }
		}
		throw new MessageException("Not exists a ResponseTransferEvidence for ID:" + id);
	}
	
	private ResponseTransferEvidence getElementFromAttached(List<EvaluatorRequestData> filesAttached) {
		EvaluatorRequestData data = filesAttached.stream().filter(p -> p.getIddata()
				.equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)).findFirst()
				.orElse(null);
		if (data != null) {
			Document doc = null;
			try {
				doc = DOMUtils.byteToDocument(data.getData());
			} catch (MessageException e) {
				logger.error("Error transforming document");
			}
			return (ResponseTransferEvidence) DOMUtils.unmarshall(ResponseTransferEvidence.class,
					doc);
		}
		return null;
	}

	private String jaxbObjectToXML(ResponseTransferEvidence request) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ResponseTransferEvidence.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(request, sw);
			return sw.toString();

		} catch (JAXBException e) {
			logger.error("Error marshalling object", e);
			return "";
		}
	}

	public ResponseTransferEvidence getErrorResponse(MessageException ex) {
		ResponseTransferEvidence error = new ResponseTransferEvidence();
		ErrorType errortype = new ErrorType();
		errortype.setCode(ex.getCode());
		errortype.setText(ex.getMessage());
		error.setError(errortype);
		return error;
	}

}
