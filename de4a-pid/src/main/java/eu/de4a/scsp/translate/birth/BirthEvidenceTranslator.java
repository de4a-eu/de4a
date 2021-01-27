package eu.de4a.scsp.translate.birth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.helger.commons.mime.CMimeType;

import eu.de4a.conn.api.requestor.DomesticEvidenceType;
import eu.de4a.conn.api.requestor.IssuingTypeType;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.scsp.translate.EvidenceTranslator;
import eu.de4a.util.DE4AConstants; 
@Component
public class BirthEvidenceTranslator implements EvidenceTranslator{
	private static final Logger logger = LogManager.getLogger(BirthEvidenceTranslator.class); 
	private static final String	REQUEST_TEMPLATE="xsl/requestBirthDayCertificate.xsl";
	public static final String	RESPONSE_TEMPLATE="xsl/responseBirthDayCertificate.xsl";
	@Autowired
	Environment env;
	@Value("${scsp.seed.birthday.certificate}")
	private String seed;
	@Value("${scsp.cif.solicitante}")
	private String cifSolicitante;
	@Value("${scsp.procedimiento}")
	private String procedimiento; 
	public Element translateEvidenceRequest(Element request) throws MessageException {
		String doc=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_DOC, request);
		String ap1=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_SURNAME, request);
		String name=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_NAME, request);
		String fullname=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_FULLNAME, request);
		String ap2=fullname.replace(ap1, "").replace(name, "").trim();
		String birthDate=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_BIRTHDATE, request);
		
		String evaluatorId=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVALUATOR_ID, request);
		String evaluatorName=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVALUATOR_NAME, request);
		String ownerId=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_OWNER_ID, request);
		String ownerName=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_OWNER_NAME, request);
		String canonicalEvidenceId=DOMUtils.getValueFromXpath(DE4AConstants.XPATH_CANONICAL_EVICENCE_ID, request);
		TransformerFactory factory = TransformerFactory.newInstance(); 
	   	InputStream inputStreamPlantilla = this.getClass().getClassLoader()  .getResourceAsStream( REQUEST_TEMPLATE); 
		Source xslDoc = new StreamSource(inputStreamPlantilla);
		Source src = new DOMSource () ; 
        OutputStream xmlFile = new ByteArrayOutputStream( );
        Transformer transformerxsl;
		try {
				transformerxsl = factory.newTransformer(xslDoc);
				fillParameter(transformerxsl,ID_PETICION_PARAM,EvidenceTranslator.getIdPeticion(seed)); 
		        fillParameter(transformerxsl,TIMESTAMP_PARAM,EvidenceTranslator.getCurrentTime()); 
		        fillParameter(transformerxsl,NIFSOLICITANTE_PARAM,cifSolicitante); 
		        fillParameter(transformerxsl,PROCEDIMIENTO_PARAM,procedimiento );  
		        fillParameter(transformerxsl,TIPO_DOCUMENTACION_PARAM,"NIF" );  
		        fillParameter(transformerxsl,NOMBRE_PARAM,name );
		        fillParameter(transformerxsl,DOCUMENTACION_PARAM,getDoc(doc)  );
		        fillParameter(transformerxsl,AP1_PARAM,ap1 );
		        if(!ap2.isEmpty()) {
		        	fillParameter(transformerxsl,AP2_PARAM,ap2 );
		        } 
		        fillParameter(transformerxsl,FECHA_NACIMIENTO_PARAM,birthDate );
		        

		        fillParameter(transformerxsl,EVALUATOR_ID_PARAM,evaluatorId );
		        fillParameter(transformerxsl,EVALUATOR_NAME_PARAM,evaluatorName );
		        fillParameter(transformerxsl,OWNER_ID_PARAM,ownerId );
		        fillParameter(transformerxsl,OWNER_NAME_PARAM,ownerName );
		        fillParameter(transformerxsl,CANONICAL_EVIDENCE_PARAM,canonicalEvidenceId );
		        
		        transformerxsl.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
		        transformerxsl.transform(src, new StreamResult(xmlFile));
		        xmlFile.close();
		        String xmlespecificos= ((ByteArrayOutputStream)xmlFile).toString(); 
		        DocumentBuilderFactory factoryDom = DocumentBuilderFactory.newInstance();
				factoryDom.setNamespaceAware(true);
			    DocumentBuilder builder = factoryDom.newDocumentBuilder();
			    Document docFinal =builder.parse(new InputSource(new StringReader(xmlespecificos))); 
			    return docFinal.getDocumentElement();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			String err="Error building SCSP request:"+e.getMessage();
			logger.error(err,e);
			throw new MessageException(err);
		} 
       
	}
	private String getDoc(String doc) {
		if(doc.lastIndexOf("/")>0)return doc.substring(doc.lastIndexOf("/")+1);
		return doc;
	}
	private void fillParameter(Transformer transformerxsl,String label, String value) {
		if(value!=null && !value.isBlank()) {
			 transformerxsl.setParameter(label,value); 
		}
	}

	public static final String XPATH_SCSP_DOC="//*[local-name()='Documentacion']/text()";  
	public static final String XPATH_SCSP_AP1="//*[local-name()='Apellido1']/text()";  
	public static final String XPATH_SCSP_NAME="//*[local-name()='Nombre']/text()";  
	public static final String XPATH_SCSP_BIRTHDATE="//*[local-name()='FechaHechoRegistral']/text()";  
	public static final String XPATH_SCSP_COUNTRY="//*[local-name()='PaisHechoRegistral']/text()";  
	public static final String XPATH_SCSP_VILLAGE="//*[local-name()='PoblacionHechoRegistral']/text()";  
	public static final String XPATH_SCSP_IDPETICION="//*[local-name()='IdPeticion']/text()";  
	public static final String VILLAGE_PARAM="lugarNacimiento";  
	public static final String COUNTRY_PARAM="paisNacimiento";  

	
	public Element translateEvidenceResponse(Element response) throws MessageException {
		String doc=DOMUtils.getValueFromXpath( XPATH_SCSP_DOC, response);
		String ap1=DOMUtils.getValueFromXpath(XPATH_SCSP_AP1, response);
		String name=DOMUtils.getValueFromXpath(XPATH_SCSP_NAME, response);  
		String birthDate=DOMUtils.getValueFromXpath(XPATH_SCSP_BIRTHDATE, response);
		String pais=DOMUtils.getValueFromXpath(XPATH_SCSP_COUNTRY, response);
		String poblacion=DOMUtils.getValueFromXpath(XPATH_SCSP_VILLAGE, response); 
		String idpeticion=DOMUtils.getValueFromXpath(XPATH_SCSP_IDPETICION, response);
		TransformerFactory factory = TransformerFactory.newInstance( ); 
	   	InputStream inputStreamPlantilla = this.getClass().getClassLoader()  .getResourceAsStream( RESPONSE_TEMPLATE); 
		Source xslDoc = new StreamSource(inputStreamPlantilla);
		Source src = new DOMSource () ; 
        OutputStream xmlFile = new ByteArrayOutputStream( );
        Transformer transformerxsl;
		try {
				transformerxsl = factory.newTransformer(xslDoc);
				fillParameter(transformerxsl,ID_PETICION_PARAM,idpeticion); 
		        fillParameter(transformerxsl,TIMESTAMP_PARAM,EvidenceTranslator.getCurrentTime()); 
		        fillParameter(transformerxsl,NIFSOLICITANTE_PARAM,cifSolicitante);  
		        fillParameter(transformerxsl,DOCUMENTACION_PARAM,doc );  
		        fillParameter(transformerxsl,NOMBRE_PARAM,name );  
		        fillParameter(transformerxsl,AP1_PARAM,ap1 ); 
		        fillParameter(transformerxsl,FECHA_NACIMIENTO_PARAM,birthDate );
		        fillParameter(transformerxsl,VILLAGE_PARAM,poblacion );
		        fillParameter(transformerxsl,COUNTRY_PARAM,pais );
		        transformerxsl.setParameter( DOMESTIC_EVIDENCES_PARAM,initDomesticBirthDate(idpeticion) );
		        transformerxsl.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
		        transformerxsl.transform(src, new StreamResult(xmlFile));
		        xmlFile.close();
		        String xmlespecificos= ((ByteArrayOutputStream)xmlFile).toString(); 
		        DocumentBuilderFactory factoryDom = DocumentBuilderFactory.newInstance();
				factoryDom.setNamespaceAware(true);
			    DocumentBuilder builder = factoryDom.newDocumentBuilder();
			    Document docFinal =builder.parse(new InputSource(new StringReader(xmlespecificos))); 
			    return docFinal.getDocumentElement();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			String err="Error building SCSP request:"+e.getMessage();
			logger.error(err,e);
			throw new MessageException(err);
		} 
	} 
	public static List<DomesticEvidenceType> initDomesticBirthDate(String id) {
		List<DomesticEvidenceType> evidences=new ArrayList<DomesticEvidenceType>(); 
		DomesticEvidenceType dom= EvidenceTranslator.buildDomesticEvidence(id, IssuingTypeType.ORIGINAL_ISSUING, CMimeType.APPLICATION_XML.getAsString (), EvidenceTranslator.LANGUAGE_DEFAULT);
		evidences.add(dom);
		return evidences;
	}
}
