package eu.de4a.connector.as4.client.regrep;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import eu.de4a.exception.MessageException;
import eu.de4a.util.DOMUtils;

public class RegRepTransformer {
	private static final Logger log = LoggerFactory.getLogger(RegRepTransformer.class);
	private static final String REQUEST_TEMPLATE = "templates/regrep_request.xsl";
	private static final String RESPONSE_TEMPLATE = "templates/regrep_response.xsl";
	private static final String CONSUMER_ID = "ConsumerId";
	private static final String CONSUMER_NAME = "ConsumerName";
	private static final String PROVIDER_ID = "ProviderId";
	private static final String PROVIDER_NAME = "ProviderName";
	private static final String PERSONAL_ID = "PersonalId";
	private static final String CURRENT_TIME = "CurrentTime";
	private static final String XPATH_CONSUMER_ID = "//*[local-name()='DataConsumer']/*[local-name()='Agent']/*[local-name()='id']/text() ";
	private static final String XPATH_CONSUMER_NAME = "//*[local-name()='DataConsumer']/*[local-name()='Agent']/*[local-name()='name']/text() ";
	private static final String XPATH_PROVIDER_ID = "//*[local-name()='DataProvider']/*[local-name()='Agent']/*[local-name()='id']/text() ";
	private static final String XPATH_PROVIDER_NAME = "//*[local-name()='DataProvider']/*[local-name()='Agent']/*[local-name()='name']/text() ";
	private static final String XPATH_PERSONAL_ID = "//*[local-name()='CorePerson']/*[local-name()='PersonID']/text() ";

	private static final String DEFAULT_ENCODING = "UTF-8";

	public Element wrapMessage(Element canonical, boolean request) throws MessageException {

		try {
			String template = request ? REQUEST_TEMPLATE : RESPONSE_TEMPLATE;
			log.debug("wrapping canonical request");
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			InputStream inputStreamPlantilla = this.getClass().getClassLoader().getResourceAsStream(template);
			Source xslDoc = new StreamSource(inputStreamPlantilla);
			Source src = new DOMSource();
			OutputStream xmlFile = new ByteArrayOutputStream();
			Transformer transformerxsl = factory.newTransformer(xslDoc);
			fillParameter(transformerxsl, CONSUMER_ID, DOMUtils.getValueFromXpath(XPATH_CONSUMER_ID, canonical));
			fillParameter(transformerxsl, CONSUMER_NAME, DOMUtils.getValueFromXpath(XPATH_CONSUMER_NAME, canonical));
			fillParameter(transformerxsl, PERSONAL_ID, DOMUtils.getValueFromXpath(XPATH_PERSONAL_ID, canonical));
			fillParameter(transformerxsl, CURRENT_TIME, getCurrentTime());
			fillParameter(transformerxsl, PROVIDER_ID, DOMUtils.getValueFromXpath(XPATH_PROVIDER_ID, canonical));
			fillParameter(transformerxsl, PROVIDER_NAME, DOMUtils.getValueFromXpath(XPATH_PROVIDER_NAME, canonical));
			transformerxsl.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
			transformerxsl.transform(src, new StreamResult(xmlFile));
			xmlFile.close();
			String xmlespecificos = ((ByteArrayOutputStream) xmlFile).toString();
			DocumentBuilderFactory factoryDom = DocumentBuilderFactory.newInstance();
			factoryDom.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factoryDom.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factoryDom.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			factoryDom.setNamespaceAware(true);
			DocumentBuilder builder = factoryDom.newDocumentBuilder();
			Document docFinal = builder.parse(new InputSource(new StringReader(xmlespecificos)));
			
			return docFinal.getDocumentElement();
		} catch (Exception e) {
			String error = "Error building wrapping message";
			log.error(error, e);
			throw new MessageException(error);
		}

	}

	private void fillParameter(Transformer transformerxsl, String label, String value) {
		if (value != null && !value.isBlank()) {
			transformerxsl.setParameter(label, value);
		}
	}

	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(new Date());
	}

}
