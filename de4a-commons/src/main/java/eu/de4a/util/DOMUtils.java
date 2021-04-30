package eu.de4a.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.de4a.exception.MessageException;

public class DOMUtils {
	private static final Logger logger = LogManager.getLogger(DOMUtils.class);

	private static final String XML_TRANSFORMER_IMPL = "net.sf.saxon.TransformerFactoryImpl";

	private DOMUtils() {
		//empty private constructor
	}

	public static Node changeNodo(Document request, String expression, String value) {
		Node node = null;
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			node = (Node) xpath.evaluate(expression, request, XPathConstants.NODE);
			node.setTextContent(value);
		} catch (XPathExpressionException e) {
			logger.error(String.format("Error accessing indicated element '%s'", expression), e);
		}
		return node;
	}

	public static String getValueFromXpath(String xpath, Element message) throws MessageException {
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xPath.evaluate(xpath, message, XPathConstants.NODESET);
			if (nodes != null && nodes.getLength() > 0) {
				return nodes.item(0).getTextContent();
			}
			return null;
		} catch (XPathExpressionException e) {
			String err = "Error getting value from path: " + xpath;
			logger.error(err, e);
			throw new MessageException(err);
		}
	}

	public static Node getNodeFromXpath(String xpath, Element message) throws MessageException {
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xPath.evaluate(xpath, message, XPathConstants.NODESET);
			if (nodes != null && nodes.getLength() > 0) {
				return nodes.item(0);
			}
			return null;
		} catch (XPathExpressionException e) {
			String err = "Error getting node from path: " + xpath;
			logger.error(err, e);
			throw new MessageException(err);
		}
	}

	public static byte[] documentToByte(Document document) throws MessageException {
		Transformer transformer;
		try {
			transformer = getXXESecureTransformer(XML_TRANSFORMER_IMPL);
			if(transformer != null) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

				StringWriter writer = new StringWriter();
				Result result = new StreamResult(writer);
				DOMSource source = new DOMSource(document);
				transformer.transform(source, result);
				return writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
			}
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			String err = "Error convert bytes from DOM";
			logger.error(err, e);
			throw new MessageException(err + e.getMessage());
		}
		return new byte[0];
	}

	public static Document byteToDocument(byte[] documentoXml) throws MessageException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setNamespaceAware(true);
			DocumentBuilder builder;

			builder = factory.newDocumentBuilder();
			return builder.parse(new ByteArrayInputStream(documentoXml));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			String err = "Xpath error in building DOM from bytes";
			logger.error(err, e);
			throw new MessageException(err);
		}
	}

	public static Document newDocumentFromInputStream(InputStream in) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document ret = null;
		String err = "Error parsing DOM.";

		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error(err, e);
			return null;
		}

		try {
			ret = builder.parse(new InputSource(in));
		} catch (SAXException | IOException e) {
			logger.error(err, e);
		}
		return ret;
	}

	public static String documentToString(Document doc) {

		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			Transformer transformer = DOMUtils.getXXESecureTransformer(XML_TRANSFORMER_IMPL);
			if(transformer != null) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(domSource, result);
				return writer.toString();
			}
		} catch (Exception e) {
			logger.error("Error doc->string", e);
		}
		return null;
	}

	public static String loadString(Document doc) {
		try {
			Source xmlInput = new StreamSource(new StringReader(documentToString(doc)));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			Transformer transformer = getXXESecureTransformer(XML_TRANSFORMER_IMPL);
			if(transformer != null) {
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(xmlInput, xmlOutput);
				return xmlOutput.getWriter().toString();
			}
		} catch (Exception e) {
			logger.error("Error doc -> Formatted string", e);
		}
		return null;
	}

	public static String nodeToString(final Node node, final boolean omitXMLDeclaration) {
		final StringWriter writer = new StringWriter();
		try {
			final Transformer t = getXXESecureTransformer(XML_TRANSFORMER_IMPL);
			if(t != null) {
				t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXMLDeclaration ? "yes" : "no");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.transform(new DOMSource(node), new StreamResult(writer));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	public static Document stringToDocument(String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			DocumentBuilder builder;

			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			logger.error("Error string -> doc", e);
		}

		return null;
	}

	public static byte[] encodeCompressed(Document doc) {
		try {
			String docStr = DOMUtils.documentToString(doc);
			byte[] input = new byte[0];
			if(!ObjectUtils.isEmpty(docStr)) {
				input = docStr.getBytes();
			}
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			try (GZIPOutputStream outputStream = new GZIPOutputStream(arrayOutputStream)) {
				outputStream.write(input, 0, input.length);
			}
			return Base64.encodeBase64(arrayOutputStream.toByteArray());
		} catch (Exception e) {
			logger.error("Error encoding compressed", e);
			return new byte[0];
		}
	}

	public static Document decodeCompressed(byte[] data) {
		byte[] decoded = Base64.decodeBase64(data);
		try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(decoded))) {
			byte[] targetArray = IOUtils.toByteArray(gis);
			return DOMUtils.byteToDocument(targetArray);
		} catch (Exception | MessageException e) {
			logger.error("Error decoding compressed", e);
			return null;
		}
	}

	public static Transformer getXXESecureTransformer(String impl) {
		TransformerFactory factory;
		factory = TransformerFactory.newInstance(impl, null);

		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");

			return factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			logger.error("Error creating Transformer", e);
			return null;
		}
	}
}
