package eu.de4a.commons;

import static org.junit.Assert.assertNotNull;

import java.io.StringReader;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.de4a.exception.MessageException;
import eu.de4a.util.DOMUtils;

public class TestDOMUtils {
	String xmlString = "<test>Hello World</test>";
	StreamSource src = new StreamSource(new StringReader(xmlString));

	// @Ignore
	@Test
	public void testXalanTransformer() throws TransformerException, MessageException {
		Document doc = DOMUtils.stringToDocument(xmlString);
		assertNotNull(doc);
		String str = DOMUtils.documentToString(doc);
		assertNotNull(str);
		byte[] encode = DOMUtils.documentToByte(doc);
		assertNotNull(encode);
		Document doc2 = DOMUtils.byteToDocument(encode);
		assertNotNull(doc2);
		byte[] encondeCompressed = DOMUtils.encodeCompressed(doc2);
		assertNotNull(encondeCompressed);
		Document doc3 = DOMUtils.decodeCompressed(encondeCompressed);
		assertNotNull(doc3);
		Node node = DOMUtils.getNodeFromXpath("//*[local-name()='test']", doc.getDocumentElement());
		assertNotNull(node);
		String nodeValue = DOMUtils.nodeToString(node, true);
		assertNotNull(nodeValue);
		String value = DOMUtils.getValueFromXpath("//*[local-name()='test']", doc.getDocumentElement());
		assertNotNull(value);
	}
	
}
