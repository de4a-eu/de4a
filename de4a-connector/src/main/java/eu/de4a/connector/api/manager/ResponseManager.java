package eu.de4a.connector.api.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import eu.de4a.connector.as4.client.ResponseWrapper;
import eu.de4a.connector.client.Client;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.connector.model.EvaluatorRequest;
import eu.de4a.connector.model.EvaluatorRequestData;
import eu.de4a.connector.repository.EvaluatorRequestDataRepository;
import eu.de4a.connector.repository.EvaluatorRequestRepository;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;

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

	@Transactional(isolation = Isolation.SERIALIZABLE)
	@AfterReturning(pointcut = "execution(* *.processResponseAs4(..))", returning = "retVal")
	public void cathResponseFromMultipleAs4(Object retVal) {
		ResponseWrapper response = (ResponseWrapper) retVal;
		String id = response.getId();
		EvaluatorRequest evaluatorinfo = evaluatorRequestRepository.findById(id).orElse(null);
		if (evaluatorinfo == null) {
			logger.error("Request does not exists ID: {}", id);
		} else {
			evaluatorinfo.setDone(true);
			evaluatorRequestRepository.save(evaluatorinfo);
			List<EvaluatorRequestData> responseData = saveData(response, evaluatorinfo);
			if (evaluatorinfo.isUsi()) {
				// Send RequestForwardEvidence to evaluator - USI pattern
				logger.debug("Pushing data to {}", evaluatorinfo.getUrlreturn());
				Document doc = getDocumentFromAttached(responseData, DE4AConstants.TAG_FORWARD_EVIDENCE_REQUEST);
				String endpointDE = evaluatorinfo.getUrlreturn().substring(0, evaluatorinfo.getUrlreturn().lastIndexOf("/"));
				client.pushEvidence(endpointDE, doc);
			}
		}
	}

	private List<EvaluatorRequestData> saveData(ResponseWrapper response, EvaluatorRequest evaluatorRequest) {
		try {
			logger.debug("Saving data for response with id {}", response.getId());
			List<EvaluatorRequestData> datas = new ArrayList<>();
			for (MultipartFile part : response.getAttacheds()) {
				byte[] data = part.getBytes();
				EvaluatorRequestData dataresponse = new EvaluatorRequestData();
				dataresponse.setData(data);
				dataresponse.setMimetype(part.getContentType());
				dataresponse.setIddata(part.getOriginalFilename());
				datas.add(dataresponse);

				dataresponse.setRequest(evaluatorRequest);
				evaluatorRequestDataRepository.save(dataresponse);
			}
			return datas;
		} catch (IOException io) {
			logger.error("Error saving evidence data", io);
			return new ArrayList<>();
		}
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean isDone(String id) {
		entityManager.clear();
		EvaluatorRequest evaluator = evaluatorRequestRepository.findById(id).orElse(null);
		return evaluator != null && evaluator.isDone();
	}

	public ResponseTransferEvidenceType getResponse(String id) throws MessageException {
		logger.debug("Processing ResponseTransferEvidence with id {}", id);

		EvaluatorRequest evaluator = evaluatorRequestRepository.findById(id).orElse(null);
		EvaluatorRequestData data = new EvaluatorRequestData();
		data.setRequest(evaluator);
		Example<EvaluatorRequestData> example = Example.of(data);
		List<EvaluatorRequestData> filesAttached = evaluatorRequestDataRepository.findAll(example);
		if(!CollectionUtils.isEmpty(filesAttached)) {
			Document doc = getDocumentFromAttached(filesAttached, DE4AConstants.TAG_EVIDENCE_RESPONSE);
			if(doc != null) {
				return DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
						.read(doc);
			}
		}
		throw new MessageException("Not exists a ResponseTransferEvidence for ID:" + id);
	}

	private Document getDocumentFromAttached(List<EvaluatorRequestData> filesAttached,
			String tagIdData) {
		EvaluatorRequestData data = filesAttached.stream().filter(p -> p.getIddata()
				.equals(tagIdData)).findFirst()
				.orElse(null);
		if (data != null) {
			Document doc = null;
			try {
				doc = DOMUtils.byteToDocument(data.getData());
			} catch (MessageException e) {
				logger.error("Error transforming document");
			}
			return doc;
		}
		return null;
	}

	public ResponseTransferEvidenceType getErrorResponse(MessageException ex) {
		ResponseTransferEvidenceType error = new ResponseTransferEvidenceType();
		ErrorListType errorList = new ErrorListType();
		ErrorType errortype = new ErrorType();
		errortype.setCode(ex.getCode());
		errortype.setText(ex.getMessage());
		errorList.addError(errortype);
		error.setErrorList(errorList);
		return error;
	}

}
