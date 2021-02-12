package eu.de4a.scsp.mock.dba; 

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import eu.de4a.conn.api.requestor.DomesticEvidenceType;
import eu.de4a.conn.api.requestor.IssuingTypeType;
import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.connector.api.rest.TCPayload;
@Component
public class DbaMockitoGateway implements OwnerGateway{  
	private static final Logger logger = LogManager.getLogger(DbaMockitoGateway.class);
	private static final String XPATH_LEGAL_ID="//*[local-name()='LegalEntityIdentifier']";
	private static final String XSL_NATIONAL="xsl/responseNationalDba.xsl";
	private static final String LANGUAGE_DEFAULT="es";
	private static final String XSL_CANONICAL="xsl/responseDba.xsl"; 
	private static final String DEFAULT_ENCODING = "UTF-8";  
	private static final String DOMESTIC_EVIDENCES_PARAM="domesticEvidences"; 
	private static final String ENTITY_PARAM="entity"; 
	private static final String IDPETICION_PARAM="idPeticion"; 
	private static final String ID_EVALUATOR_PARAM="evaluatorId"; 
	private static final String NAME_EVALUATOR_PARAM="evaluatorName"; 
	private static final String ID_OWNER_PARAM="ownerId"; 
	private static final String NAME_OWNER_PARAM="ownerName"; 
	private static final String TIMESTAMP_PARAM="timeStamp"; 
	private DbaRepository dbaRepository=new DbaRepository();
	public List<TCPayload> sendEvidenceRequest(org.w3c.dom.Element evidenceRequest) throws MessageException{
		if(logger.isDebugEnabled()) { 
			logger.debug("Request to DBA Mockito: {}",DOMUtils.documentToString(evidenceRequest.getOwnerDocument()));
		}
		List<TCPayload> payloads=new ArrayList<TCPayload>();
		Node id=DOMUtils.getNodeFromXpath(XPATH_LEGAL_ID, evidenceRequest);
		String requestId=DOMUtils.getValueFromXpath(String.format(DE4AConstants.XPATH_REQUEST_ID, DE4AConstants.TAG_EVIDENCE_REQUEST),evidenceRequest);
		Entity e=dbaRepository.selectEntity(id.getTextContent()  ); 
		String idevaluator=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_EVALUATOR_ID, evidenceRequest);
		String nameevaluator=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_EVALUATOR_NAME, evidenceRequest);
		String idowner=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_OWNER_ID, evidenceRequest);
		String nameownerr=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_OWNER_NAME, evidenceRequest);
		
		Document canonical=getCanonicalResponse(e,requestId,idevaluator,nameevaluator,idowner,nameownerr);
		Document national=getNationalResponse(e);
		payloads.add(makePayload(DE4AConstants.TAG_EVIDENCE_RESPONSE,MediaType.APPLICATION_XML.toString(),canonical));
		payloads.add(makePayload(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE,MediaType.APPLICATION_XML.toString(),national));
		return payloads;
	} 
	 
	private  TCPayload  makePayload (String name,String mimetype,Document document){  
				TCPayload payload=new TCPayload();
				payload.setContentID(name);
				payload.setMimeType(mimetype); 
				byte []data=DOMUtils.documentToByte(document);
				payload.setValue(data); 
		return payload ;
	}

	private Document translateEntity(Entity e, String xsl,String id,String idevaluator,String nameevaluator,String idowner,String nameownerr) {
		TransformerFactory factory = TransformerFactory.newInstance( ); 
	   	InputStream inputStreamPlantilla = this.getClass().getClassLoader()  .getResourceAsStream( xsl); 
		Source xslDoc = new StreamSource(inputStreamPlantilla);
		Source src = new DOMSource () ; 
        OutputStream xmlFile = new ByteArrayOutputStream( );
        Transformer transformerxsl;
		try {
				
				transformerxsl = factory.newTransformer(xslDoc);
				transformerxsl.setParameter(ENTITY_PARAM,e);  	
				transformerxsl.setParameter(TIMESTAMP_PARAM,getCurrentTime()); 
				if(id!=null)
					transformerxsl.setParameter(IDPETICION_PARAM,id); 
				if(idevaluator!=null)
					transformerxsl.setParameter(ID_EVALUATOR_PARAM,idevaluator); 
				if(nameevaluator!=null)
					transformerxsl.setParameter(NAME_EVALUATOR_PARAM,nameevaluator); 
				if(idowner!=null)
					transformerxsl.setParameter(ID_OWNER_PARAM,idowner);
				if(nameownerr!=null)
					transformerxsl.setParameter( NAME_OWNER_PARAM,nameownerr); 
		        
		        if(xsl.equals(XSL_CANONICAL))transformerxsl.setParameter( DOMESTIC_EVIDENCES_PARAM,initDomestic(e.getId()) );
		        transformerxsl.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
		        transformerxsl.transform(src, new StreamResult(xmlFile));
		        xmlFile.close();
		        String xmlespecificos= ((ByteArrayOutputStream)xmlFile).toString(); 
		        DocumentBuilderFactory factoryDom = DocumentBuilderFactory.newInstance();
				factoryDom.setNamespaceAware(true);
			    DocumentBuilder builder = factoryDom.newDocumentBuilder();
			    Document docFinal =builder.parse(new InputSource(new StringReader(xmlespecificos))); 
			    return docFinal  ;
		}catch(Exception ex) {
			logger.error("Qué contrariedad", e);
			return null;
		}
	}
	private Document getCanonicalResponse(Entity e,String id,String idevaluator,String nameevaluator,String idowner,String nameownerr) {
		return translateEntity(e, XSL_CANONICAL,id,idevaluator,nameevaluator,idowner,nameownerr);
	
	}
	private Document getNationalResponse(Entity e ) {
		return translateEntity(e, XSL_NATIONAL,null,null,null,null,null);
	
	}
	private List<DomesticEvidenceType> initDomestic(String id) {
			List<DomesticEvidenceType> evidences=new ArrayList<DomesticEvidenceType>(); 
			DomesticEvidenceType  domestic = new DomesticEvidenceType();
			domestic.setDataLanguage(LANGUAGE_DEFAULT);
			domestic.setDomesticEvidenceIdRef(id);
			domestic.setIssuingType(IssuingTypeType.ORIGINAL_ISSUING);
			domestic.setMimeType(MediaType.APPLICATION_XML.toString());
			evidences.add(domestic);
			return evidences;
	}
	private static  String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss" ); 
		return sdf.format(new Date()) ;
	}
}
 