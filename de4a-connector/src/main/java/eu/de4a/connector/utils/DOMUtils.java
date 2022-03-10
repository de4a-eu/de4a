package eu.de4a.connector.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

import eu.de4a.connector.error.exceptions.MessageException;

public class DOMUtils {

    private static final Logger logger = LoggerFactory.getLogger(DOMUtils.class);

    private static final String XML_TRANSFORMER_IMPL = "net.sf.saxon.TransformerFactoryImpl";

    private DOMUtils() {
        // empty private constructor
    }

    public static Node changeNode(final Document request, final String expression, final String value) {
        Node node = null;
        try {
            final XPath xpath = XPathFactory.newInstance().newXPath();
            node = (Node) xpath.evaluate(expression, request, XPathConstants.NODE);
            node.setTextContent(value);
        } catch (final XPathExpressionException e) {
            logger.error(String.format("Error accessing indicated element '%s'", expression), e);

        }
        return node;
    }

    public static String getValueFromXpath(final String xpath, final Element message) {
        try {
            final XPath xPath = XPathFactory.newInstance().newXPath();
            final NodeList nodes = (NodeList) xPath.evaluate(xpath, message, XPathConstants.NODESET);
            if (nodes != null && nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
        } catch (final XPathExpressionException e) {
            final String err = "Error getting Xpath " + xpath;
            logger.error(err, e);
        }
        return null;
    }

    public static Node getNodeFromXpath(final String xpath, final Element message) {
        try {
            final XPath xPath = XPathFactory.newInstance().newXPath();
            final NodeList nodes = (NodeList) xPath.evaluate(xpath, message, XPathConstants.NODESET);
            if (nodes != null && nodes.getLength() > 0) {
                return nodes.item(0);
            }            
        } catch (final XPathExpressionException e) {
            final String err = "Error getting Xpath " + xpath;
            logger.error(err, e);
        }
        return null;
    }

    public static byte[] documentToByte(final Document document) {
        Transformer transformer;
        try {
            transformer = getXXESecureTransformer(XML_TRANSFORMER_IMPL);
            if (transformer != null) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                try (final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream()) {
                    final Result result = new StreamResult(baos);
                    final DOMSource source = new DOMSource(document);
                    transformer.transform(source, result);
                    return baos.toByteArray();
                }
            }
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            final String err = "Error convert bytes from DOM";
            logger.error(err, e);
        }
        return new byte[0];
    }

    public static Document byteToDocument(final byte[] documentoXml) throws MessageException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder;

            builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(documentoXml));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            final String err = "xpath error in building DOM from bytes";
            logger.error(err, e);
            throw new MessageException(err, e);
        }
    }

    public static Document newDocumentFromInputStream(final InputStream in) {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document ret = null;
        final String err = "Error parsing DOM.";

        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            logger.error(err, e);
            return null;
        }

        try {
            InputSource is = new InputSource(in);
            is.setEncoding(StandardCharsets.UTF_8.toString());
            ret = builder.parse(is);
        } catch (SAXException | IOException e) {
            logger.error(err, e);
        }
        return ret;
    }

    public static String documentToString(final Document doc) {

        try {
            final DOMSource domSource = new DOMSource(doc);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final Transformer transformer = DOMUtils.getXXESecureTransformer(XML_TRANSFORMER_IMPL);
            if (transformer != null) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(domSource, result);
                return writer.toString();
            }
        } catch (final Exception e) {
            logger.error("Error doc->string", e);
        }
        return null;
    }

    public static String loadString(final Document doc) {
        try {
            final Source xmlInput = new StreamSource(new StringReader(documentToString(doc)));
            final StringWriter stringWriter = new StringWriter();
            final StreamResult xmlOutput = new StreamResult(stringWriter);
            final Transformer transformer = getXXESecureTransformer(XML_TRANSFORMER_IMPL);
            if (transformer != null) {
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString();
            }
        } catch (final Exception e) {
            logger.error("Error doc -> Formatted string", e);
        }
        return null;
    }

    public static String nodeToString(final Node node, final boolean omitXMLDeclaration) {
        final StringWriter writer = new StringWriter();
        try {
            final Transformer t = getXXESecureTransformer(XML_TRANSFORMER_IMPL);
            if (t != null) {
                t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXMLDeclaration ? "yes" : "no");
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.transform(new DOMSource(node), new StreamResult(writer));
            }
        } catch (final Exception e) {
            logger.error("Error node -> Formatted string", e);
        }
        return writer.toString();
    }

    public static Document stringToDocument(final String xml) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder builder;

            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (final Exception e) {
            logger.error("Error string -> doc", e);
        }

        return null;
    }

    public static Transformer getXXESecureTransformer(final String impl) {
        TransformerFactory factory;
        factory = TransformerFactory.newInstance(impl, null);

        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");

            return factory.newTransformer();
        } catch (final TransformerConfigurationException e) {
            logger.error("Error creating Transformer", e);
            return null;
        }
    }

    public static Document newDocumentFromNode(Element element, String nodeName) throws ParserConfigurationException {
        NodeList nodes = element.getOwnerDocument().getDocumentElement().getElementsByTagNameNS("*", nodeName);

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        Document newXmlDocument = factory.newDocumentBuilder().newDocument();
        Element root = (Element) nodes.item(0);
        Node copyNode = newXmlDocument.importNode(root, true);
        newXmlDocument.appendChild(copyNode);

        return newXmlDocument;
    }
}
