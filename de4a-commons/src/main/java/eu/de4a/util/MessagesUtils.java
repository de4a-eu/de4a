package eu.de4a.util;

import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceUSIType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;

public class MessagesUtils {

	private MessagesUtils() {
		//empty private constructor
	}

	public static RequestExtractEvidenceIMType transformRequestToOwnerIM(RequestTransferEvidenceUSIIMDRType evidenceRequest) {
		RequestExtractEvidenceIMType requestExtractEvidence = new RequestExtractEvidenceIMType();
		requestExtractEvidence.setCanonicalEvidenceTypeId(evidenceRequest.getCanonicalEvidenceTypeId());
		requestExtractEvidence.setDataEvaluator(evidenceRequest.getDataEvaluator());
		requestExtractEvidence.setDataOwner(evidenceRequest.getDataOwner());
		requestExtractEvidence.setDataRequestSubject(evidenceRequest.getDataRequestSubject());
		requestExtractEvidence.setProcedureId(evidenceRequest.getProcedureId());
		requestExtractEvidence.setRequestGrounds(evidenceRequest.getRequestGrounds());
		requestExtractEvidence.setRequestId(evidenceRequest.getRequestId());
		requestExtractEvidence.setSpecificationId(evidenceRequest.getSpecificationId());
		requestExtractEvidence.setTimeStamp(evidenceRequest.getTimeStamp());
		return requestExtractEvidence;
	}

	public static RequestExtractEvidenceUSIType transformRequestToOwnerUSI(
			RequestTransferEvidenceUSIIMDRType evidenceRequest) {
		RequestExtractEvidenceUSIType requestExtractEvidence = new RequestExtractEvidenceUSIType();
		requestExtractEvidence.setCanonicalEvidenceTypeId(evidenceRequest.getCanonicalEvidenceTypeId());
		requestExtractEvidence.setDataEvaluator(evidenceRequest.getDataEvaluator());
		requestExtractEvidence.setDataOwner(evidenceRequest.getDataOwner());
		requestExtractEvidence.setDataRequestSubject(evidenceRequest.getDataRequestSubject());
		requestExtractEvidence.setProcedureId(evidenceRequest.getProcedureId());
		requestExtractEvidence.setRequestGrounds(evidenceRequest.getRequestGrounds());
		requestExtractEvidence.setRequestId(evidenceRequest.getRequestId());
		requestExtractEvidence.setSpecificationId(evidenceRequest.getSpecificationId());
		requestExtractEvidence.setTimeStamp(evidenceRequest.getTimeStamp());
		return requestExtractEvidence;
	}

	public static ResponseTransferEvidenceType transformResponseTransferEvidence(
			ResponseExtractEvidenceType responseExtractEvidenceType,
			RequestTransferEvidenceUSIIMDRType evidenceRequest) {
		ResponseTransferEvidenceType responseTransferEvidenceType = new ResponseTransferEvidenceType();
		responseTransferEvidenceType.setDataEvaluator(evidenceRequest.getDataEvaluator());
		responseTransferEvidenceType.setDataOwner(evidenceRequest.getDataOwner());
		responseTransferEvidenceType.setCanonicalEvidenceTypeId(evidenceRequest.getCanonicalEvidenceTypeId());
		responseTransferEvidenceType.setCanonicalEvidence(responseExtractEvidenceType.getCanonicalEvidence());
		responseTransferEvidenceType.setRequestId(evidenceRequest.getRequestId());
		responseTransferEvidenceType.setTimeStamp(evidenceRequest.getTimeStamp());
		responseTransferEvidenceType.setProcedureId(evidenceRequest.getProcedureId());
		responseTransferEvidenceType.setSpecificationId(evidenceRequest.getSpecificationId());
		responseTransferEvidenceType.setErrorList(responseExtractEvidenceType.getErrorList());
		responseTransferEvidenceType.setDataRequestSubject(evidenceRequest.getDataRequestSubject());
		responseTransferEvidenceType.setDomesticEvidenceList(responseExtractEvidenceType.getDomesticEvidenceList());

		return responseTransferEvidenceType;
	}
	
	public static ResponseTransferEvidenceType getErrorResponseFromRequest(
	        RequestTransferEvidenceUSIIMDRType evidenceRequest, ErrorListType errorList) {
	    ResponseTransferEvidenceType responseTransferEvidenceType = new ResponseTransferEvidenceType();
        responseTransferEvidenceType.setDataEvaluator(evidenceRequest.getDataEvaluator());
        responseTransferEvidenceType.setDataOwner(evidenceRequest.getDataOwner());
        responseTransferEvidenceType.setCanonicalEvidenceTypeId(evidenceRequest.getCanonicalEvidenceTypeId());
        responseTransferEvidenceType.setRequestId(evidenceRequest.getRequestId());
        responseTransferEvidenceType.setTimeStamp(evidenceRequest.getTimeStamp());
        responseTransferEvidenceType.setProcedureId(evidenceRequest.getProcedureId());
        responseTransferEvidenceType.setSpecificationId(evidenceRequest.getSpecificationId());
        responseTransferEvidenceType.setErrorList(errorList);
        responseTransferEvidenceType.setDataRequestSubject(evidenceRequest.getDataRequestSubject());

        return responseTransferEvidenceType;
	}
	
	public static RequestForwardEvidenceType transformRequestTransferUSIDT(RequestTransferEvidenceUSIDTType request) {
	    RequestForwardEvidenceType requestForward = new RequestForwardEvidenceType();
	    requestForward.setRequestId(request.getRequestId());
	    requestForward.setTimeStamp(request.getTimeStamp());
	    requestForward.setCanonicalEvidence(request.getCanonicalEvidence());
	    requestForward.setDomesticEvidenceList(request.getDomesticEvidenceList());
	    requestForward.setErrorList(request.getErrorList());
	    return requestForward;
	}

}
