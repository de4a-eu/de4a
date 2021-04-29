package eu.de4a.connector.api.controller.error;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.de4a.iem.jaxb.common.types.AtuLevelType;
import eu.de4a.iem.jaxb.common.types.AvailableSourcesType;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.ErrorType;
import eu.de4a.iem.jaxb.common.types.ProvisionItemType;
import eu.de4a.iem.jaxb.common.types.ProvisionItemsType;
import eu.de4a.iem.jaxb.common.types.ProvisionType;
import eu.de4a.iem.jaxb.common.types.ProvisionTypeType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.SourceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;

public class ResponseLookupRoutingInformationExceptionHandler  extends ConnectorExceptionHandler{
	@Override
	public String getResponseError(ConnectorException ex) {
//		ResponseLookupRoutingInformationType response=new ResponseLookupRoutingInformationType();
//		AvailableSourcesType sources=new AvailableSourcesType();
//		SourceType  source=new SourceType();
////		source.setAtuLevel(AtuLevelType.NUTS_0);
////		source.setCountryCode("00");
////		source.setNumProvisions(1);
////		ProvisionItemsType ptype=new ProvisionItemsType(); 
////		source.setOrganisation(" ");
////		
////		ProvisionItemType item=new ProvisionItemType();
////		item.setAtuCode(" ");
////		item.setAtuLatinName(" ");
////		item.setDataOwnerId(" "); 
////		ProvisionType provision=new ProvisionType();
////		provision.setProvisionType( ProvisionTypeType.IP);
////		item.setProvision(provision);
////		ptype.getProvisionItem().add(item);
//		sources.getSource().add(source);
//		response.setAvailableSources(sources);
//		//source.setProvisionItems(ptype);
//		ErrorListType errorListType = new ErrorListType();
//		String msg=getMessage(ex.getMessage(), ex.getArgs()) ;
//        errorListType.addError(
//                DE4AResponseDocumentHelper.createError( ex.buildCode(),msg)
//        );
//        response.setErrorList(errorListType);
//        return DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(response);
        
        ResponseLookupRoutingInformationType responseLookup = new ResponseLookupRoutingInformationType();  
			ErrorListType errorList = new ErrorListType();  
			String msg=getMessage(ex ) ;
			errorList.addError( DE4AResponseDocumentHelper.createError( ex.buildCode(),msg));
			responseLookup.setErrorList(errorList);
			return DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(responseLookup);
	}
 
}
