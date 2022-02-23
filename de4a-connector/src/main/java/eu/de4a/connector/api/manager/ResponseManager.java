package eu.de4a.connector.api.manager;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.connector.as4.client.ResponseWrapper;
import eu.de4a.connector.client.Client;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.error.utils.KafkaClientWrapper;
import eu.de4a.connector.model.EvaluatorRequest;
import eu.de4a.connector.model.EvaluatorRequestData;
import eu.de4a.connector.repository.EvaluatorRequestDataRepository;
import eu.de4a.connector.repository.EvaluatorRequestRepository;
import eu.de4a.connector.service.spring.AddressesProperties;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
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
	@Autowired
	private AddressesProperties addressesProperties;

	public void processAS4Response(ResponseWrapper response) {
		String id = response.getId();
		
		KafkaClientWrapper.sendInfo(LogMessages.LOG_AS4_RESP_RECEIPT, id);
		
		EvaluatorRequest evaluatorinfo = evaluatorRequestRepository.findById(id).orElse(null);
		if (evaluatorinfo == null) {
		    KafkaClientWrapper.sendError(LogMessages.LOG_ERROR_AS4_RESP_RECEIPT, id);
		} else {
			evaluatorinfo.setDone(true);
			evaluatorRequestRepository.save(evaluatorinfo);
			saveData(response, evaluatorinfo);
            if (evaluatorinfo.isUsi()) {
                String evaluatorAddress = this.addressesProperties.getDataEvaluators()
                        .get(evaluatorinfo.getIdevaluator());
                if (!ObjectUtils.isEmpty(evaluatorAddress)) {
                    // Send RequestForwardEvidence to evaluator - USI pattern
                    if(DE4AConstants.TAG_EVIDENCE_REQUEST.equals(response.getTagDataId())) {
                        client.pushEvidence(evaluatorAddress, response.getResponseDocument());
                    } else if(DE4AConstants.TAG_REDIRECT_USER.equals(response.getTagDataId())) {
                        client.pushRedirectUserMsg(evaluatorAddress, response.getResponseDocument());
                    }
                } else {
                    //TODO in this case, how DE or DO is advised of the situation?
                    KafkaClientWrapper.sendError(LogMessages.LOG_ERROR_UNKNOWN_DE, id, evaluatorinfo.getIdevaluator());
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
		    .withRequest(DE4AMarshaller.drImRequestMarshaller().read(request));
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
}
