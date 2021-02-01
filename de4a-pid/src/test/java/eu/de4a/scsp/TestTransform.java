package eu.de4a.scsp;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.scsp.translate.EvidenceTranslator;
import eu.de4a.scsp.translate.birth.BirthEvidenceTranslator;
import eu.toop.scsp.spring.ConfPid; 
@SpringBootTest(classes={ConfPid.class}) 
@RunWith(SpringRunner.class) 
public class TestTransform {

	private static final Logger logger = LoggerFactory.getLogger (TestTransform.class); 
	@Test
	public void transform() {
		try {
			Element response=null;  
 			DocumentBuilderFactory factorydom = DocumentBuilderFactory.newInstance();
 	        DocumentBuilder dBuilder = factorydom.newDocumentBuilder();
 	        Document docd = dBuilder.parse(this.getClass().getClassLoader()  .getResourceAsStream( "edm/responseBirthCertificate.xml")); 
 	        response=docd.getDocumentElement();  
 			String doc=DOMUtils.getValueFromXpath( BirthEvidenceTranslator.XPATH_SCSP_DOC, response);
			String ap1=DOMUtils.getValueFromXpath(BirthEvidenceTranslator.XPATH_SCSP_AP1, response);
			String name=DOMUtils.getValueFromXpath(BirthEvidenceTranslator.XPATH_SCSP_NAME, response);  
			String birthDate=DOMUtils.getValueFromXpath(BirthEvidenceTranslator.XPATH_SCSP_BIRTHDATE, response);
			String pais=DOMUtils.getValueFromXpath(BirthEvidenceTranslator.XPATH_SCSP_COUNTRY, response);
			String poblacion=DOMUtils.getValueFromXpath(BirthEvidenceTranslator.XPATH_SCSP_VILLAGE, response); 
			String idpeticion=DOMUtils.getValueFromXpath(BirthEvidenceTranslator.XPATH_SCSP_IDPETICION, response);
			TransformerFactory factory = TransformerFactory.newInstance( ); 
		   	InputStream inputStreamPlantilla = this.getClass().getClassLoader()  .getResourceAsStream(BirthEvidenceTranslator. RESPONSE_TEMPLATE); 
			Source xslDoc = new StreamSource(inputStreamPlantilla);
			Source src = new DOMSource () ; 
	        OutputStream xmlFile = new ByteArrayOutputStream( );
	        Transformer transformerxsl;
			
					transformerxsl = factory.newTransformer(xslDoc);
					fillParameter(transformerxsl,BirthEvidenceTranslator.ID_PETICION_PARAM,idpeticion); 
			        fillParameter(transformerxsl,BirthEvidenceTranslator.TIMESTAMP_PARAM,EvidenceTranslator.getCurrentTime()); 
			        fillParameter(transformerxsl,BirthEvidenceTranslator.NIFSOLICITANTE_PARAM,"S833002E");  
			        fillParameter(transformerxsl,BirthEvidenceTranslator.DOCUMENTACION_PARAM,doc );  
			        fillParameter(transformerxsl,BirthEvidenceTranslator.NOMBRE_PARAM,name );  
			        fillParameter(transformerxsl,BirthEvidenceTranslator.AP1_PARAM,ap1 ); 
			        fillParameter(transformerxsl,BirthEvidenceTranslator.FECHA_NACIMIENTO_PARAM,birthDate );
			        fillParameter(transformerxsl,BirthEvidenceTranslator.VILLAGE_PARAM,poblacion );
			        fillParameter(transformerxsl,BirthEvidenceTranslator.COUNTRY_PARAM,pais );
			        transformerxsl.setParameter(BirthEvidenceTranslator. DOMESTIC_EVIDENCES_PARAM,BirthEvidenceTranslator.initDomesticBirthDate(idpeticion) );
			        transformerxsl.setOutputProperty(OutputKeys.ENCODING,BirthEvidenceTranslator. DEFAULT_ENCODING);
			        transformerxsl.transform(src, new StreamResult(xmlFile));
			        xmlFile.close();
			        String xmlespecificos= ((ByteArrayOutputStream)xmlFile).toString(); 
			        DocumentBuilderFactory factoryDom = DocumentBuilderFactory.newInstance();
					factoryDom.setNamespaceAware(true);
				    DocumentBuilder builder = factoryDom.newDocumentBuilder();
				    Document docFinal =builder.parse(new InputSource(new StringReader(xmlespecificos))); 
				    assertNotNull(docFinal.getDocumentElement());
			} catch ( Throwable e) {
				String err="Error building SCSP request:"+e.getMessage();
				logger.error(err,e); 
			} 
		} 
	private void fillParameter(Transformer transformerxsl,String label, String value) {
		if(value!=null && !value.isBlank()) {
			 transformerxsl.setParameter(label,value); 
		}
	}


}
