package eu.de4a.conn.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.de4a.exception.MessageException;

public class DOMUtils {
	private static final Logger logger = LogManager.getLogger(DOMUtils.class);
	public static String encodeDocBase64(Document doc) throws TransformerException { 
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		String data=writer.toString();
		return  Base64.getEncoder().encodeToString(data.getBytes());
	}
	public static Node changeNodo(Document request,String expression,String value)   { 
		Node node = null;
		try{
			XPath xpath = XPathFactory.newInstance().newXPath();
			node = (Node)xpath.evaluate(expression,request,XPathConstants.NODE);
			node.setTextContent(value);
		}catch(XPathExpressionException e){
			logger.error(String.format("No se ha podido acceder al elemento indicado '%s'",expression),e);
			 
		}
		return node;
	}
	public static String getValueFromXpath(String xpath, Element message) throws MessageException {
		try{
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList)xPath.evaluate(xpath, message , XPathConstants.NODESET);
			if(nodes!=null && nodes.getLength()>0){
				return nodes.item(0).getTextContent();
			}
			return null;
		} catch (XPathExpressionException e) { 
			String err="xpath error in building wrapping message.";
			logger.error(err,e);
			throw new MessageException(err+ e.getMessage());
		} 
	}
	public static Node getNodeFromXpath(String xpath, Element message) throws MessageException {
		try{
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList)xPath.evaluate(xpath, message , XPathConstants.NODESET);
			if(nodes!=null && nodes.getLength()>0){
				return nodes.item(0) ;
			}
			return null;
		} catch (XPathExpressionException e) { 
			String err="xpath error in building wrapping message.";
			logger.error(err,e);
			throw new MessageException(err+ e.getMessage());
		} 
	}
	public static byte[] documentToByte(Document document)
	  { 
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      org.apache.xml.security.utils.XMLUtils.outputDOM(document, baos, true);
	      return baos.toByteArray();
	  }
	public static Document byteToDocument(  byte[] documentoXml)throws MessageException  {
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setNamespaceAware(true);
		    DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
			    return builder.parse(new ByteArrayInputStream(documentoXml)); 
			} catch (ParserConfigurationException | SAXException | IOException e) {
				String err="xpath error in building DOM from bytes";
				logger.error(err,e);
				throw new MessageException(err+ e.getMessage());
			}
	}
	 public static Document newDocumentFromInputStream(InputStream in) {
		    DocumentBuilderFactory factory = null;
		    DocumentBuilder builder = null;
		    Document ret = null;

		    try {
		      factory = DocumentBuilderFactory.newInstance();
		      builder = factory.newDocumentBuilder();
		    } catch (ParserConfigurationException e) {
		    	String err="Error parsing DOM.";
				logger.error(err,e);
		    }

		    try {
		      ret = builder.parse(new InputSource(in));
		    } catch (SAXException e) {
		    	String err="Error parsing DOM.";
				logger.error(err,e);
		    } catch (IOException e) {
		    	String err="Error parsing DOM.";
				logger.error(err,e);
		    }
		    return ret;
		  }
	 public static String documentToString(Document doc) {
		
	    try {
	    	DOMSource domSource = new DOMSource(doc);
		    StringWriter writer = new StringWriter();
		    StreamResult result = new StreamResult(writer);
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception e) {
			logger.error("Error doc->string",e);
		}
	    return null;
	 }
	 public static Document stringToDocument(String xml) {
		 DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			return db.parse(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Error string -> doc",e);
		}
		return null; 
	 }
}
