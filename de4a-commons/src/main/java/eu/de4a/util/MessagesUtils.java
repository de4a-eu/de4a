package eu.de4a.util;

import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;

public class MessagesUtils {

	private MessagesUtils() {
		//empty private constructor
	}

	public static RequestExtractEvidenceType transformRequestToOwnerIM(RequestExtractEvidenceType evidenceRequest) {
		return evidenceRequest.clone ();
	}

	public static RequestExtractEvidenceType transformRequestToOwnerUSI(
			RequestExtractEvidenceType evidenceRequest) {
		return evidenceRequest.clone ();
	}
	
	public static RequestTransferEvidenceUSIDTType getErrorRequestTransferEvidenceUSIDT(RequestExtractEvidenceType evidenceRequest,
	        ErrorListType errorList) {
        RequestTransferEvidenceUSIDTType requestTransferEvidenceUSIDT = new RequestTransferEvidenceUSIDTType();
        requestTransferEvidenceUSIDT.setDataEvaluator(evidenceRequest.getDataEvaluator());
        requestTransferEvidenceUSIDT.setDataOwner(evidenceRequest.getDataOwner());
        requestTransferEvidenceUSIDT.setDataRequestSubject(evidenceRequest.getDataRequestSubject());
        requestTransferEvidenceUSIDT.setProcedureId(evidenceRequest.getProcedureId());
        requestTransferEvidenceUSIDT.setRequestId(evidenceRequest.getRequestId());
        requestTransferEvidenceUSIDT.setSpecificationId(evidenceRequest.getSpecificationId());
        requestTransferEvidenceUSIDT.setTimeStamp(evidenceRequest.getTimeStamp());
        requestTransferEvidenceUSIDT.setErrorList(errorList);
        return requestTransferEvidenceUSIDT;
    }

	public static ResponseTransferEvidenceType transformResponseTransferEvidence(
			ResponseExtractEvidenceType responseExtractEvidenceType,
			RequestExtractEvidenceType evidenceRequest) {
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
	        RequestExtractEvidenceType evidenceRequest, ErrorListType errorList) {
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
