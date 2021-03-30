package eu.de4a.scsp.translate.birth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.de4a.conn.api.canonical.BirthEvidence;
import eu.de4a.conn.api.canonical.ObjectFactory;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.CanonicalEvidenceType;
import eu.de4a.iem.jaxb.common.types.DomesticEvidenceType;
import eu.de4a.iem.jaxb.common.types.DomesticsEvidencesType;
import eu.de4a.iem.jaxb.common.types.IssuingTypeType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.scsp.owner.model.Municipio;
import eu.de4a.scsp.owner.repository.MunicipioRepository;
import eu.de4a.scsp.translate.EvidenceTranslator;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils; 


@Component
public class BirthEvidenceTranslator implements EvidenceTranslator{
	private static final Logger logger = LogManager.getLogger(BirthEvidenceTranslator.class); 
	private static final String	REQUEST_TEMPLATE="xsl/requestBirthDayCertificate.xsl";
	public static final String	RESPONSE_TEMPLATE="xsl/responseBirthDayCertificate.xsl";
	private static final String XPATH_MUNICIPIO= "//*[local-name()='PoblacionHechoRegistral']";
	private static final String XPATH_SEXO= "//*[local-name()='Sexo']";	
	public static final String XPATH_SCSP_DOC="//*[local-name()='Documentacion']/text()";  
	public static final String XPATH_SCSP_AP1="//*[local-name()='Apellido1']/text()";  
	public static final String XPATH_SCSP_NAME="//*[local-name()='Nombre']/text()";  
	public static final String XPATH_SCSP_BIRTHDATE="//*[local-name()='FechaHechoRegistral']/text()";  
	public static final String XPATH_SCSP_COUNTRY="//*[local-name()='PaisHechoRegistral']/text()";  
	public static final String XPATH_SCSP_VILLAGE="//*[local-name()='PoblacionHechoRegistral']/text()";  
	public static final String XPATH_SCSP_IDPETICION="//*[local-name()='IdPeticion']/text()";  
	public static final String VILLAGE_PARAM="lugarNacimiento";  
	public static final String COUNTRY_PARAM="paisNacimiento";  
	public static final String NAME_MUNICIPIO_PARAM="nameMunicipio";  
	public static final String SEXO_PARAM="sexo";  
	private static int INDEX_END_PROVINCIA=2;
	private static int LENGTH_MUNICIPIO=5;
	@Autowired
	Environment env;
	@Value("${scsp.seed.birthday.certificate}")
	private String seed;
	@Value("${scsp.cif.solicitante}")
	private String cifSolicitante;
	@Value("${scsp.procedimiento}")
	private String procedimiento; 
	@Autowired
	private MunicipioRepository municipioRepository;
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
	
	public ResponseExtractEvidenceType translateExtractEvidenceResponse(Element response)
			throws MessageException {
		
		ResponseExtractEvidenceType responseExtractEvidence = new ResponseExtractEvidenceType();
		DomesticsEvidencesType domesticEvidences = initDomesticBirthDate(response.getOwnerDocument());
		responseExtractEvidence.setDomesticEvidenceList(domesticEvidences);
		
		Element processedResponse = translateEvidenceResponse(response);
		Node canonicalEvidence = DOMUtils.getNodeFromXpath("//*[local-name()='BirthEvidence']", 
				processedResponse);
		CanonicalEvidenceType canonicalEvidenceType = new CanonicalEvidenceType();
		ObjectFactory factory = new ObjectFactory();
		BirthEvidence canonicalEvidenceObj = factory.createBirthEvidence();
		Unmarshaller jaxbUnmarshaller;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(BirthEvidence.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			canonicalEvidenceObj = (BirthEvidence) jaxbUnmarshaller.unmarshal(canonicalEvidence);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		canonicalEvidenceType.setAny(canonicalEvidenceObj);
		
		responseExtractEvidence.setCanonicalEvidence(canonicalEvidenceType);
		return responseExtractEvidence;
	}
	
	public RequestForwardEvidenceType translateRequestForwardEvidence(Element response) throws MessageException {
		RequestForwardEvidenceType requestForwardEvidence = new RequestForwardEvidenceType();
		DomesticsEvidencesType domesticEvidences = initDomesticBirthDate(response.getOwnerDocument());
		requestForwardEvidence.setDomesticEvidenceList(domesticEvidences);
		
		Element processedResponse = translateEvidenceResponse(response);
		Node canonicalEvidence = DOMUtils.getNodeFromXpath("//*[local-name()='BirthEvidence']", 
				processedResponse);
		CanonicalEvidenceType canonicalEvidenceType = new CanonicalEvidenceType();
		ObjectFactory factory = new ObjectFactory();
		BirthEvidence canonicalEvidenceObj = factory.createBirthEvidence();
		Unmarshaller jaxbUnmarshaller;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(BirthEvidence.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			canonicalEvidenceObj = (BirthEvidence) jaxbUnmarshaller.unmarshal(canonicalEvidence);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		canonicalEvidenceType.setAny(canonicalEvidenceObj);
		
		requestForwardEvidence.setCanonicalEvidence(canonicalEvidenceType);
		
		return requestForwardEvidence;		
	}


	public Element translateEvidenceResponse(Element response) throws MessageException {
		if(logger.isDebugEnabled()) {
			logger.debug("SCSP Response:",  DOMUtils.documentToString(response.getOwnerDocument()));
		}
		String doc=DOMUtils.getValueFromXpath( XPATH_SCSP_DOC, response);
		String ap1=DOMUtils.getValueFromXpath(XPATH_SCSP_AP1, response);
		String name=DOMUtils.getValueFromXpath(XPATH_SCSP_NAME, response);  
		String birthDate=DOMUtils.getValueFromXpath(XPATH_SCSP_BIRTHDATE, response);
		String pais=DOMUtils.getValueFromXpath(XPATH_SCSP_COUNTRY, response);
		String poblacion=DOMUtils.getValueFromXpath(XPATH_SCSP_VILLAGE, response); 
		String idpeticion=DOMUtils.getValueFromXpath(XPATH_SCSP_IDPETICION, response);
		String municipio=DOMUtils.getValueFromXpath(XPATH_MUNICIPIO, response);
		String sexo=DOMUtils.getValueFromXpath(XPATH_SEXO, response);
		Municipio data=new Municipio();
		String codprovincia=municipio.substring(0,INDEX_END_PROVINCIA);
		String codmunicipio=municipio.substring(INDEX_END_PROVINCIA,LENGTH_MUNICIPIO);
		data.setProvincia(codprovincia);
		data.setMunicipio(codmunicipio);
		Example<Municipio> example = Example.of(data);
		List<Municipio>registros=municipioRepository.findAll(example); 
		String nameMunicipio=registros.get(0).getNombre();
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
		        fillParameter(transformerxsl,NAME_MUNICIPIO_PARAM,nameMunicipio );
		        fillParameter(transformerxsl,SEXO_PARAM,sexo );
		        transformerxsl.setParameter( DOMESTIC_EVIDENCES_PARAM,initDomesticBirthDateList( 
		        		response.getOwnerDocument()));
		        transformerxsl.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
		        transformerxsl.transform(src, new StreamResult(xmlFile));
		        xmlFile.close();
		        String xmlespecificos= ((ByteArrayOutputStream)xmlFile).toString(); 
		        DocumentBuilderFactory factoryDom = DocumentBuilderFactory.newInstance();
				factoryDom.setNamespaceAware(true);
			    DocumentBuilder builder = factoryDom.newDocumentBuilder();
			    Document docFinal =builder.parse(new InputSource(new StringReader(xmlespecificos))); 
			    //DOMUtils.changeNodo(docFinal,DE4AConstants.XPATH_EVIDENCE_DATA,new String(DOMUtils.encodeCompressed(response.getOwnerDocument())));
			    return docFinal.getDocumentElement();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			String err="Error building SCSP request:"+e.getMessage();
			logger.error(err,e);
			throw new MessageException(err);
		} 
	} 
	public static DomesticsEvidencesType initDomesticBirthDate(Document data) {
		DomesticsEvidencesType domesticEvidences = new DomesticsEvidencesType();
		List<DomesticEvidenceType> evidences = initDomesticBirthDateList(data);
		domesticEvidences.setDomesticEvidence(evidences);		
		domesticEvidences.setLang(EvidenceTranslator.LANGUAGE_DEFAULT);
		return domesticEvidences;
	}
	
	public static List<DomesticEvidenceType> initDomesticBirthDateList(Document data) {
		List<DomesticEvidenceType> evidences=new ArrayList<>(); 
		DomesticEvidenceType dom= EvidenceTranslator.buildDomesticEvidence(IssuingTypeType.ORIGINAL_ISSUING,
				MediaType.APPLICATION_XML.toString(), 
				EvidenceTranslator.LANGUAGE_DEFAULT, data);
		evidences.add(dom);
		return evidences;
	}
	
}
