package eu.de4a.connector.error.handler;

import org.w3c.dom.Element;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.SMPLookingMetadataInformationException;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.util.DOMUtils;

public class SMPLookingMetadataInformationExceptionHandler  extends ConnectorExceptionHandler{
    
	@Override
	public Object getResponseError(ConnectorException ex, boolean returnBytes) {
		ResponseTransferEvidenceType response = buildResponse(ex);
		if(returnBytes) {
		    return DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE)
		            .getAsBytes(response);
		}
		return response;
	}
	
	@Override
	public ResponseTransferEvidenceType buildResponse(ConnectorException ex) {
	    SMPLookingMetadataInformationException exsmp = (SMPLookingMetadataInformationException) ex;
        Element requestEl=exsmp.getUserMessage();
        RequestExtractEvidenceType request=DE4AMarshaller.drImRequestMarshaller().read(DOMUtils.documentToString(requestEl.getOwnerDocument()));
        ResponseTransferEvidenceType response =DE4AResponseDocumentHelper.createResponseTransferEvidence(request);
        ErrorListType errorList = new ErrorListType();  
        String msg=getMessage(ex ) ;
        errorList.addError( DE4AResponseDocumentHelper.createError( ex.buildCode(),msg));
        response.setErrorList(errorList);
        return response;
	}
 
}
