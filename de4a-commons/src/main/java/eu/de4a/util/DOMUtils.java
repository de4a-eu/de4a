package eu.de4a.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.exception.MessageException; 

public class DOMUtils {
	private static final Logger logger = LogManager.getLogger(DOMUtils.class);
 
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

	public static byte[] documentToByte(Document document) throws MessageException {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();		
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	
	        StringWriter writer = new StringWriter();
	        Result result = new StreamResult(writer);
	        DOMSource source = new DOMSource(document);
			transformer.transform(source, result);
			return writer.getBuffer().toString().getBytes();
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			String err="Error convert bytes from DOM";
			logger.error(err,e);
			throw new MessageException(err+ e.getMessage());
		}
	}
	
	public static Document byteToDocument(byte[] documentoXml)throws MessageException  {
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
		      factory.setNamespaceAware(true);
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
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception e) {
			logger.error("Error doc->string",e);
		}
	    return null;
	 }
	 
	public static String loadString(Document doc) throws Exception {
		try {
			Source xmlInput = new StreamSource(new StringReader(documentToString(doc)));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			logger.error("Error doc -> Formatted string", e);
		}
		return null;
	}
	 
	 public static Document stringToDocument(String xml) {
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder builder;  
	        try 
	        {  
	        	factory.setNamespaceAware(true);
	            builder = factory.newDocumentBuilder();  
	            Document doc = builder.parse( new InputSource( new StringReader( xml )) ); 
	            return doc; 
	        } catch (Exception e) {  
	        	logger.error("Error string -> doc",e);
	        } 
	  
		  
		return null; 
	 }
	 public static byte[]serializeJaxbObject(Class<?> clazz,Object o){
		  ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
		  try {
		         JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		         Marshaller marshaller = jaxbContext.createMarshaller(); 
		         marshaller.marshal(o, xmlStream);
		         return xmlStream.toByteArray();
		  } catch ( Exception e) {
		       	logger.error("Error marshalling jaxb object",e);
		       	return null;
		  }
	 }
	 
	public static <T> String jaxbObjectToXML(T xmlObj, Class<? extends T> aClass) {
		String xmlString = "";
		try {
			JAXBContext context = JAXBContext.newInstance(aClass);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(xmlObj, sw);
			return sw.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
			return xmlString;
		}
	}
	 
	public static Object unmarshall(Class<?> clazz, Document doc) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			javax.xml.bind.Unmarshaller jaxbMarshaller = (Unmarshaller) jaxbContext.createUnmarshaller();
			return jaxbMarshaller.unmarshal(doc);
		} catch (Exception e) {
			logger.error("Error unmarshalling to jaxb object", e);
			return null;
		}
	}

	public static Document marshall(Class<?> clazz, Object obj) {
		try {
			JAXBContext jc = JAXBContext.newInstance(clazz);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();

			// Marshal Object to the Document
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(obj, document);
			return document;
		} catch (JAXBException | ParserConfigurationException e) {
			logger.error("Error marshalling object to DOM", e);
			return null;
		}
	}
	 
//	 public static byte[] encodeCompressed(Document doc) {
//			  
//	        try {
//	        	 byte[] input = DOMUtils.documentToString( doc).getBytes(); 
//			     ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//			     GZIPOutputStream outputStream = new GZIPOutputStream(arrayOutputStream);
//			     outputStream.write(input,0, input.length);
//			     outputStream.close();
//			     return Base64.encodeBase64(arrayOutputStream.toByteArray());
//	        } catch (Throwable t) {
//	        	logger.error("Error encoding compressed",t);
//		       	return null;
//	        } 
//	}
	 public static byte[] encodeCompressed(Document doc) {
		// return Base64.encodeBase64(DOMUtils.documentToString( doc).getBytes());
	        try {
	        	 byte[] input = DOMUtils.documentToString( doc).getBytes();  
			     ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			     GZIPOutputStream outputStream = new GZIPOutputStream(arrayOutputStream);
			     outputStream.write(input,0, input.length);
			     outputStream.close();
			     outputStream.flush(); 
			     return Base64.encodeBase64(arrayOutputStream.toByteArray());
	        } catch (Throwable t) {
	        	logger.error("Error encoding compressed",t);
		       	return null;
	        } 
	}
	 
//	 public static Document decodeCompressed(byte[] data) {
//		  
//	        try {
//	        	byte[]decoded=Base64.decodeBase64(data);
//			    GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(decoded));
//		        byte[] targetArray = IOUtils .toByteArray(gis);
//		        String s=new String(targetArray
//		   );
//			    return DOMUtils.byteToDocument(targetArray);
//	        } catch (Throwable t) {
//	        	logger.error("Error decoding compressed",t);
//		       	return null;
//	        }
//	}
	 
	 public static Document decodeCompressed(byte[] data) {
//		try {
//			return DOMUtils.byteToDocument( Base64.decodeBase64(data));
//		} catch (MessageException e) {
//			return null;
//		}
	        try {
	        	byte[]decoded=Base64.decodeBase64( data); 
			    GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(decoded));
		        byte[] targetArray = IOUtils .toByteArray(gis);  
			    return DOMUtils.byteToDocument(targetArray);
	        } catch (Throwable t) {
	        	logger.error("Error decoding compressed",t);
		       	return null;
	        }
	}
}
