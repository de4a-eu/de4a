package eu.de4a.connector.as4.client.regrep;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.XMLConstants;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;

public class RegRepTransformer {
	private static final Logger log = LoggerFactory.getLogger(RegRepTransformer.class);
	private static final String REQUEST_TEMPLATE = "templates/regrep_req.xsl";
	private static final String RESPONSE_TEMPLATE = "templates/regrep_resp.xsl";
	private static final String REQUEST_ID = "requestId";
	private static final String CONSUMER_ID = "ConsumerId";
	private static final String CONSUMER_NAME = "ConsumerName";
	private static final String PROVIDER_ID = "ProviderId";
	private static final String PROVIDER_NAME = "ProviderName";
	private static final String PERSONAL_ID = "PersonalId";
	private static final String FAMILY_NAME = "familyName";
	private static final String GIVEN_NAME = "givenName";
	private static final String BIRTH_DATE = "birthDate";
	private static final String CURRENT_TIME = "CurrentTime";
	private static final String EVIDENCE_TYPE_ID = "evidenceTypeId";
	private static final String MESSAGE_TAG = "messageType";
	private static final String MESSAGE_CONTENT_TAG = "MessageContent";

	private static final String DEFAULT_ENCODING = "UTF-8";

	public Element wrapMessage(Element canonical, String messageTag, boolean isRequest) throws MessageException {

		try (OutputStream xmlFile = new ByteArrayOutputStream()) {
			String template = (isRequest ? REQUEST_TEMPLATE : RESPONSE_TEMPLATE);
			log.debug("Wrapping canonical request as RegRep message");
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			InputStream inputStreamPlantilla = this.getClass().getClassLoader().getResourceAsStream(template);
			Source xslDoc = new StreamSource(inputStreamPlantilla);
			Source src = new DOMSource();			
			Transformer transformerxsl = factory.newTransformer(xslDoc);

			fillParameter(transformerxsl, REQUEST_ID, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, canonical));
			if(isRequest) {
			    fillParameter(transformerxsl, CONSUMER_ID, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVALUATOR_ID, canonical));
			    fillParameter(transformerxsl, CONSUMER_NAME, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EVALUATOR_NAME, canonical));
			} else {
			    fillParameter(transformerxsl, PROVIDER_ID, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_OWNER_ID, canonical));
                fillParameter(transformerxsl, PROVIDER_NAME, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_OWNER_NAME, canonical));
			}

			fillParameter(transformerxsl, PERSONAL_ID, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_DOC, canonical));
			fillParameter(transformerxsl, FAMILY_NAME, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_SURNAME, canonical));
			fillParameter(transformerxsl, GIVEN_NAME, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_NAME, canonical));
			fillParameter(transformerxsl, BIRTH_DATE, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_EIDAS_BIRTHDATE, canonical));
            fillParameter(transformerxsl, CURRENT_TIME, getCurrentTime());
            fillParameter(transformerxsl, EVIDENCE_TYPE_ID, DOMUtils.getValueFromXpath(DE4AConstants.XPATH_CANONICAL_EVICENCE_ID, canonical));
            fillParameter(transformerxsl, MESSAGE_TAG, messageTag);
			transformerxsl.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
			transformerxsl.transform(src, new StreamResult(xmlFile));

			String xmlespecificos = ((ByteArrayOutputStream) xmlFile).toString();			
			Document docFinal = DOMUtils.stringToDocument(xmlespecificos);

			copyNode(docFinal, MESSAGE_CONTENT_TAG, canonical);
			
			return docFinal.getDocumentElement();
		} catch (Exception e) {
			String error = "Error building RegRep wrapped message";
			log.error(error, e);
			throw new ConnectorException()
			    .withFamily(FamilyErrorType.CONVERSION_ERROR)
			    .withLayer(LayerError.INTERNAL_FAILURE)
			    .withMessageArg(error);
		}

	}

	private void fillParameter(Transformer transformerxsl, String label, Object value) {
		if (value != null) {
			transformerxsl.setParameter(label, value);
		}
	}

	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(new Date());
	}
	
	private void copyNode(Document doc, String nodeName, Element newNode) 
	        throws NullPointerException {
	    Node copyNode = doc.importNode(newNode, true);
	    NodeList nodeList = doc.getElementsByTagName(nodeName);
	    Node oldNode = nodeList.item(0);
	    if(oldNode == null) {
	        throw new NullPointerException("Node: " + nodeName + " - does not exists at the XML Document");
	    }
	    oldNode.getParentNode().replaceChild(copyNode, oldNode);
	}

}
