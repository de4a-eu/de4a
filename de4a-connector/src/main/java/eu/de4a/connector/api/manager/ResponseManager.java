package eu.de4a.connector.api.manager;

import java.io.IOException;
import java.text.MessageFormat;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.error.level.EErrorLevel;

import eu.de4a.connector.as4.client.ResponseWrapper;
import eu.de4a.connector.client.Client;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
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
		
		String logMsg = MessageFormat.format("Processing response received from AS4 gateway - RequestId: {0}", id);
        DE4AKafkaClient.send(EErrorLevel.INFO, logMsg);
		
		EvaluatorRequest evaluatorinfo = evaluatorRequestRepository.findById(id).orElse(null);
		if (evaluatorinfo == null) {
		    logMsg = MessageFormat.format("Request not found on registries with the ID received - RequestId: {0}", id);
			DE4AKafkaClient.send(EErrorLevel.ERROR, logMsg);
		} else {
			evaluatorinfo.setDone(true);
			evaluatorRequestRepository.save(evaluatorinfo);
			List<EvaluatorRequestData> responseData = saveData(response, evaluatorinfo);
            if (evaluatorinfo.isUsi()) {

                // TODO USI pattern depends on redirectURL of DataEvaluator setted on the request
                // to perform the way back once the response is received by Connector
                // temporary solution until the final solution will be defined
                if (ObjectUtils.isEmpty(evaluatorinfo.getUrlreturn())) {
                    // Send RequestForwardEvidence to evaluator - USI pattern
                    String msg = MessageFormat.format("Sending RequestForwardEvidence to DataEvaluator - RequestId: {0}, "
                            + "DataEvaluatorId: {1}, Endpoint: {2}", id, evaluatorinfo.getIdevaluator(), evaluatorinfo.getUrlreturn());
                    DE4AKafkaClient.send(EErrorLevel.INFO, msg);
                    
                    Document doc = getDocumentFromAttached(responseData, DE4AConstants.TAG_EVIDENCE_REQUEST_DT);
                    client.pushEvidence(evaluatorinfo.getUrlreturn(), doc);
                } else {
                    //TODO in this case, how DE or DO is advised of the situation?
                    // turn redirectURL into a mandatory field?
                    DE4AKafkaClient.send(EErrorLevel.ERROR, MessageFormat.format("RequestForwardEvidence could not been sended, "
                            + "unkown DataEvaluator endpoint - RequestId: {0}, DataEvaluatorId: {1}, Endpoint: {2}", id, 
                            evaluatorinfo.getIdevaluator(), evaluatorinfo.getUrlreturn()));
                }
            }
		}
	}

	private List<EvaluatorRequestData> saveData(ResponseWrapper response, EvaluatorRequest evaluatorRequest) {
		try {
			logger.info("Saving data for response with id {}", response.getId());
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

	public ResponseTransferEvidenceType getResponse(String id, Element request) {
		logger.info("Processing ResponseTransferEvidence with id {}", id);

		EvaluatorRequest evaluator = evaluatorRequestRepository.findById(id).orElse(null);
		if(evaluator != null) {
			EvaluatorRequestData data = new EvaluatorRequestData();
			data.setRequest(evaluator);
			Example<EvaluatorRequestData> example = Example.of(data);
			List<EvaluatorRequestData> filesAttached = evaluatorRequestDataRepository.findAll(example);
			if(!CollectionUtils.isEmpty(filesAttached)) {
				Document doc = getDocumentFromAttached(filesAttached, DE4AConstants.TAG_EVIDENCE_RESPONSE);
				if(doc != null) {
					return (ResponseTransferEvidenceType) ErrorHandlerUtils.conversionDocWithCatching(
							DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE), doc, false, false, 
							new ResponseTransferEvidenceException().withModule(ExternalModuleError.CONNECTOR_DR)
								.withRequest(DE4AMarshaller.drImRequestMarshaller().read(request)));
				}
			}
		}
		throw new ResponseTransferEvidenceException().withLayer(LayerError.INTERNAL_FAILURE)
		    .withFamily(FamilyErrorType.SAVING_DATA_ERROR)
		    .withModule(ExternalModuleError.NONE)
		    .withMessageArg(MessageFormat.format("Response {0} not found on database", id))
		    .withRequest(DE4AMarshaller.drImRequestMarshaller().read(request))
		    .withHttpStatus(HttpStatus.OK);
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
