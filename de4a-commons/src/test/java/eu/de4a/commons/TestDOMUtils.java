package eu.de4a.commons;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.de4a.exception.MessageException;
import eu.de4a.util.DOMUtils;

public class TestDOMUtils
{
  private final String xmlString = "<test>Hello World</test>";
  private final StreamSource src = new StreamSource (new StringReader (xmlString));

  // @Ignore
  @Test
  public void test1 () throws MessageException
  {
    final Document doc = DOMUtils.stringToDocument (xmlString);
    assertNotNull (doc);
    String str = DOMUtils.documentToString (doc);
    assertNotNull (str);
    final byte [] encode = DOMUtils.documentToByte (doc);
    assertNotNull (encode);
    final Document doc2 = DOMUtils.byteToDocument (encode);
    assertNotNull (doc2);
    final Node node = DOMUtils.getNodeFromXpath ("//*[local-name()='test']", doc.getDocumentElement ());
    assertNotNull (node);
    final String nodeValue = DOMUtils.nodeToString (node, true);
    assertNotNull (nodeValue);
    final String value = DOMUtils.getValueFromXpath ("//*[local-name()='test']", doc.getDocumentElement ());
    assertNotNull (value);
    str = DOMUtils.loadString (doc2);
    assertNotNull (str);
    final InputStream is = new ByteArrayInputStream (xmlString.getBytes ());
    final Document doc4 = DOMUtils.newDocumentFromInputStream (is);
    assertNotNull (doc4);
    final Node node1 = DOMUtils.changeNode (doc4, "//*[local-name()='test']", "Bye world");
    assertNotNull (node1);
  }

}
