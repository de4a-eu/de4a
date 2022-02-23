package eu.de4a.connector.api.manager;

import java.util.Calendar;
import java.util.Locale;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import eu.de4a.connector.as4.client.regrep.RegRepTransformer;
import eu.de4a.connector.client.Client;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseLookupRoutingInformationException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceUSIException;
import eu.de4a.connector.error.handler.ResponseErrorExceptionHandler;
import eu.de4a.connector.error.handler.ResponseLookupRoutingInformationExceptionHandler;
import eu.de4a.connector.error.handler.ResponseTransferEvidenceExceptionHandler;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.error.model.LogMessages;
import eu.de4a.connector.error.utils.ErrorHandlerUtils;
import eu.de4a.connector.error.utils.KafkaClientWrapper;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.util.DE4AConstants;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;

@Component
public class EvidenceRequestorManager extends EvidenceManager {

	private static final Logger logger = LoggerFactory.getLogger(EvidenceRequestorManager.class);
	@Value("#{'${smp.endpoint.jvm:${smp.endpoint:}}'}")
	private String smpEndpoint;
	@Value("#{'${idk.endpoint.jvm:${idk.endpoint:}}'}")
	private String idkEndpoint;
	@Value("${as4.timeout.miliseconds:#{60000}}")
	private long timeout;

	@Autowired
	private Client client;
	@Autowired
	private ResponseManager responseManager;

	public ResponseLookupRoutingInformationType manageRequest(RequestLookupRoutingInformationType request) {
		final IDocumentTypeIdentifier aDTI = SimpleIdentifierFactory.INSTANCE
                .parseDocumentTypeIdentifier(request.getCanonicalEvidenceTypeId());
		if (aDTI != null && aDTI.hasScheme() && aDTI.hasValue()) {
		    request.setCanonicalEvidenceTypeId(aDTI.getValue());
			if (ObjectUtils.isEmpty(request.getDataOwnerId())) {
				return client.getSources(request);
			} else {
				return client.getProvisions(request);
			}
		} else {
		    return new ResponseLookupRoutingInformationExceptionHandler().buildResponse(
                    new ResponseLookupRoutingInformationException().withFamily(FamilyErrorType.MISSING_REQUIRED_ARGUMENTS)
                        .withLayer(LayerError.INTERNAL_FAILURE)
                        .withModule(ExternalModuleError.IDK)
                        .withMessageArg("CanonicalEvidenceTypeId is missing or has incorrect format"));
		}
	}

	public ResponseErrorType manageRequestUSI(RequestExtractEvidenceType request) {
	    Document doc = (Document) ErrorHandlerUtils.conversionDocWithCatching(DE4AMarshaller.drUsiRequestMarshaller(), 
	            request, true, true, new ResponseTransferEvidenceUSIException()
	                                        .withModule(ExternalModuleError.CONNECTOR_DR)
	                                        .withRequest(request));
		try {
            if(sendRequestMessage(request.getDataEvaluator().getAgentUrn(), request.getDataOwner().getAgentUrn(), doc.getDocumentElement(),
            		request.getCanonicalEvidenceTypeId())) {
                return DE4AResponseDocumentHelper.createResponseError(true);
            }            
        } catch (ConnectorException e) {
            return new ResponseErrorExceptionHandler().buildResponse(
                    new ResponseTransferEvidenceUSIException().withFamily(e.getFamily())
                        .withLayer(e.getLayer())
                        .withModule(e.getModule())
                        .withMessageArgs(e.getArgs()));
        }
		return DE4AResponseDocumentHelper.createResponseError(false);
	}

	public ResponseTransferEvidenceType manageRequestIM(RequestExtractEvidenceType request) {
		Document doc = DE4AMarshaller.drImRequestMarshaller().getAsDocument(request);
		try {
            return handleRequestTransferEvidence(request.getDataEvaluator().getAgentUrn(), request.getDataOwner().getAgentUrn(), 
                    doc.getDocumentElement(), request.getRequestId(), request.getCanonicalEvidenceTypeId());
        } catch (ConnectorException e) {
            return new ResponseTransferEvidenceExceptionHandler().buildResponse(
                    new ResponseTransferEvidenceException().withLayer(e.getLayer())
                        .withFamily(e.getFamily())
                        .withModule(e.getModule())
                        .withMessageArgs(e.getArgs())
                        .withRequest(request));
        }
	}

	private ResponseTransferEvidenceType handleRequestTransferEvidence(String from, String dataOwnerId,
			Element documentElement, String requestId, String canonicalEvidenceTypeId) {
		boolean ok = false;
		sendRequestMessage(from, dataOwnerId, documentElement, canonicalEvidenceTypeId);
		try {
			ok = waitResponse(requestId);
		} catch (InterruptedException e) {
		    String errorMsg = "Error waiting for response";
			logger.error(errorMsg, e);
			Thread.currentThread().interrupt();
			throw new ConnectorException().withLayer(LayerError.INTERNAL_FAILURE)
			    .withFamily(FamilyErrorType.ERROR_RESPONSE)
			    .withModule(ExternalModuleError.CONNECTOR_DR)
			    .withMessageArg(errorMsg);
		}
		if (!ok) {
			String errorMsg = "No response before timeout";
            logger.error(errorMsg);
            throw new ConnectorException().withLayer(LayerError.INTERNAL_FAILURE)
                .withFamily(FamilyErrorType.ERROR_RESPONSE)
                .withModule(ExternalModuleError.CONNECTOR_DR)
                .withMessageArg(errorMsg);
		}
		return responseManager.getResponse(requestId, documentElement);		
	}

	private boolean waitResponse(String id) throws InterruptedException {
		long init = Calendar.getInstance().getTimeInMillis();
		boolean wait = !responseManager.isDone(id);
		boolean ok = !wait;
		while (wait) {
			logger.debug("Waiting for response to complete...");
			Thread.sleep(700);
			ok = responseManager.isDone(id);
			wait = !ok && Calendar.getInstance().getTimeInMillis() - init < timeout;
		}
		return ok;
	}

	public boolean sendRequestMessage(String sender, String dataOwnerId, Element userMessage,
			String canonicalEvidenceTypeId) {
	    String errorMsg;
		try {
		    IParticipantIdentifier doPI = SimpleIdentifierFactory.INSTANCE
		            .parseParticipantIdentifier(dataOwnerId.toLowerCase(Locale.ROOT));
		    IParticipantIdentifier sPI = SimpleIdentifierFactory
		            .INSTANCE.parseParticipantIdentifier(sender.toLowerCase(Locale.ROOT));
		    if(doPI != null) {
		        dataOwnerId = doPI.getValue();
		    }
		    if(sPI != null) {
		        sender = sPI.getValue();
		    }
		    
		    NodeInfo nodeInfo = client.getNodeInfo(dataOwnerId, canonicalEvidenceTypeId, false, userMessage);/*NodeInfo nodeInfo = new NodeInfo();/            
            nodeInfo.setParticipantIdentifier("9921:ess2833002e");
            nodeInfo.setDocumentIdentifier("BirthCertificate");
            nodeInfo.setProcessIdentifier("request");
            nodeInfo.setEndpointURI("https://de4a-dev-connector2.egovlab.eu/phase4");
            String certificate = "MIIFcjCCA1qgAwIBAgICEAMwDQYJKoZIhvcNAQELBQAwajELMAkGA1UEBhMCRVUxDzANBgNVBAgMBkV1cm9wZTENMAsGA1UECgwEREU0QTElMCMGA1UECwwcREU0QSBXUDUgRGV2IEludGVybWVkaWF0ZSBDQTEUMBIGA1UEAwwLREU0YSBXUDUgSU0wHhcNMjEwMjExMTIzNzMwWhcNMjYwODA0MTIzNzMwWjBRMQswCQYDVQQGEwJFVTELMAkGA1UECAwCU0UxDTALBgNVBAoMBERFNEExJjAkBgNVBAMMHWRlNGEtZGV2LWNvbm5lY3Rvci5lZ292bGFiLmV1MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs9kZpBGbmWGZDdbkOZ13eFoEJfbx3fprkD4sbPqKebcS7FwoyXaxi463Z0G9arj76fhVDQBmCY5zHDKsC9Ym2PB2U69VGrsAxAs7RJmMu2f8sH6UiHwL27aW8CYR/7QagbGDGFK2OimB4MlKgIVmyYnqfeDDiaZilvek41D00u2JyLHqVpqfv6fB1f3/8INeivix26/ny8TibzXgruSdIw0FuBZzZ2Ba1t+bQJbXrlMdywULFp0yV3ZT5ngouAduKwe/0GLYDc9QtRHcCO/ifc1MqVyEze62/K5R+ha1UWEVra1rVd5DDgFfLCVIZWEeeutsQstvaksvSrhwdIjJ+QIDAQABo4IBOTCCATUwCQYDVR0TBAIwADARBglghkgBhvhCAQEEBAMCBkAwMwYJYIZIAYb4QgENBCYWJE9wZW5TU0wgR2VuZXJhdGVkIFNlcnZlciBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUAvQbSDEd4VkMRy9Lzv0Lwkud60EwgZsGA1UdIwSBkzCBkIAUgXChA2vQzse7LPWu/DJLXI2o9sOhdKRyMHAxCzAJBgNVBAYTAkVVMQ8wDQYDVQQIDAZFdXJvcGUxDTALBgNVBAoMBERFNEExKzApBgNVBAsMIkRFNEEgV1A1IERldiBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkxFDASBgNVBAMMC0RFNEEgV1A1IENBggIQADAOBgNVHQ8BAf8EBAMCBaAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwDQYJKoZIhvcNAQELBQADggIBAKFI6VJoXr8/o7Ms7/aQottFmVO7+gU82z7mnTGxAhQXfy8c5Tk83gDtIj7uaVfLIfZUlHNbWoLyrX8JC9bK0Ta3oNnwhHdoqum4CE3aGYuKxxi/dCUpz3XVJfv6eHwPmwBl6PzeCl93RlGCa+uF51oWsWQHEuNZkeac7wXFG9dTQS7XXHpcUSfEhOzjuSWgU3nxx4TrZ0oukkAq6E+8SL0ls0NjBt2X2bxeqWj2detKHhc29KqEmaKCfH8UbjgUZEuQuDpXcxIc/z2sxYXPxo9/yLdU3j2bziJC3l//Kf1bb8WKy3VPH6hpAAscIGCYDppgoDcUtCuY89BvK/2wbONyw2PMn5g+rmNB0BSn1nfal0E3GqoQdiOFjiJ8AkjZGceyoRJvs5nJUXetx3rdRCuLTRWzmTVh4wjj9TTxMHECqxuyimcwRP++dBFDqH5FuEbozlthzzBygAHmtGDTrJtr9tSLB2N9psvjOTjwrH+Tbq1wd3jYwhNNPqO1girW2WLfhTQhPTimbaBTYDvTHNVT2gi9JlH+np7onmm0+lMehrHRBQKKsshoZ2RPrSTnt2cxhvPrOmHgjkLJ6m7CVmWn7wbmzp0GVO3pR2Rp0Mi6nb8aityuk2m0QzotNQkdhzj+tj0iNSbhATfYaU5SsFLgSx3SUR/flUkxwN6y+0cK"; 
                    //"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";
            nodeInfo.setCertificate(Base64.decode(certificate));*/
	        
	        KafkaClientWrapper.sendInfo(LogMessages.LOG_AS4_REQ_SENT, sPI.getURIEncoded(), doPI.getURIEncoded(), canonicalEvidenceTypeId);
			
			Element requestWrapper = new RegRepTransformer().wrapMessage(userMessage, DE4AConstants.TAG_EVIDENCE_REQUEST, true);			
			as4Client.sendMessage(sender, nodeInfo, requestWrapper, null, DE4AConstants.TAG_EVIDENCE_REQUEST);
			
			return true;
		} catch (MEOutgoingException e) {
		    errorMsg = "Error with as4 gateway communications";
			logger.error(errorMsg, e);
			throw new ConnectorException()
                .withLayer(LayerError.COMMUNICATIONS)
                .withFamily(FamilyErrorType.AS4_ERROR_COMMUNICATION)
                .withModule(ExternalModuleError.CONNECTOR_DT)
                .withMessageArg(errorMsg);
		} catch (ConnectorException cE) {
		    throw cE.withModule(ExternalModuleError.CONNECTOR_DT);
		} catch (NullPointerException | MessageException msgE) {
		    throw new ConnectorException().withLayer(LayerError.INTERNAL_FAILURE)
		        .withFamily(FamilyErrorType.CONVERSION_ERROR)
		        .withModule(ExternalModuleError.CONNECTOR_DR)
		        .withMessageArg(msgE.getMessage());
		}
	}

}
