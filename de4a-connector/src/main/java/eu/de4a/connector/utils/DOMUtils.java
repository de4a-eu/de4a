/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Austrian Federal Computing Center (BRZ)
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.de4a.connector.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;
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

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

@Immutable
public final class DOMUtils
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DOMUtils.class);
  private static final String XML_TRANSFORMER_IMPL = "net.sf.saxon.TransformerFactoryImpl";

  private DOMUtils ()
  {}

  @Nullable
  public static String getValueFromXpath (final String xpath, final Element message)
  {
    try
    {
      final XPath xPath = XPathFactory.newInstance ().newXPath ();
      final NodeList nodes = (NodeList) xPath.evaluate (xpath, message, XPathConstants.NODESET);
      if (nodes != null && nodes.getLength () > 0)
      {
        return nodes.item (0).getTextContent ();
      }
    }
    catch (final XPathExpressionException e)
    {
      final String err = "Error getting Xpath " + xpath;
      LOGGER.error (err, e);
    }
    return null;
  }

  @Nullable
  public static Node getNodeFromXpath (final String xpath, final Element message)
  {
    try
    {
      final XPath xPath = XPathFactory.newInstance ().newXPath ();
      final NodeList nodes = (NodeList) xPath.evaluate (xpath, message, XPathConstants.NODESET);
      if (nodes != null && nodes.getLength () > 0)
      {
        return nodes.item (0);
      }
    }
    catch (final XPathExpressionException e)
    {
      LOGGER.error ("Error getting Xpath '" + xpath + "'", e);
    }
    return null;
  }

  @Nonnull
  public static byte [] documentToByte (final Document document)
  {
    try
    {
      final Transformer transformer = _getXXESecureTransformer (XML_TRANSFORMER_IMPL);
      if (transformer != null)
      {
        transformer.setOutputProperty (OutputKeys.INDENT, "yes");
        transformer.setOutputProperty (OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty (OutputKeys.OMIT_XML_DECLARATION, "yes");

        try (final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ())
        {
          final Result result = new StreamResult (baos);
          final DOMSource source = new DOMSource (document);
          transformer.transform (source, result);
          return baos.toByteArray ();
        }
      }
    }
    catch (final TransformerFactoryConfigurationError | TransformerException e)
    {
      LOGGER.error ("Error convert bytes from DOM", e);
    }
    return ArrayHelper.EMPTY_BYTE_ARRAY;
  }

  @Nullable
  public static Document byteToDocument (final byte [] documentoXml)
  {
    try
    {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
      factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      factory.setFeature (XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setNamespaceAware (true);
      final DocumentBuilder builder = factory.newDocumentBuilder ();
      return builder.parse (new NonBlockingByteArrayInputStream (documentoXml));
    }
    catch (final ParserConfigurationException | SAXException | IOException e)
    {
      LOGGER.error ("Failed to parse XML to DOM from bytes", e);
    }
    return null;
  }

  @Nullable
  public static Document newDocumentFromInputStream (@Nonnull @WillNotClose final InputStream in)
  {
    try
    {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
      factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      factory.setFeature (XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setNamespaceAware (true);

      final DocumentBuilder builder = factory.newDocumentBuilder ();
      final InputSource is = new InputSource (in);
      is.setEncoding (StandardCharsets.UTF_8.toString ());
      return builder.parse (is);
    }
    catch (final ParserConfigurationException | SAXException | IOException e)
    {
      LOGGER.error ("Error parsing XML", e);
    }
    return null;
  }

  public static String documentToString (final Document doc)
  {

    try
    {
      final DOMSource domSource = new DOMSource (doc);
      final StringWriter writer = new StringWriter ();
      final StreamResult result = new StreamResult (writer);
      final Transformer transformer = _getXXESecureTransformer (XML_TRANSFORMER_IMPL);
      if (transformer != null)
      {
        transformer.setOutputProperty (OutputKeys.INDENT, "yes");
        transformer.transform (domSource, result);
        return writer.toString ();
      }
    }
    catch (final Exception e)
    {
      LOGGER.error ("Error doc->string", e);
    }
    return null;
  }

  public static String loadString (final Document doc)
  {
    try
    {
      final Source xmlInput = new StreamSource (new StringReader (documentToString (doc)));
      final StringWriter stringWriter = new StringWriter ();
      final StreamResult xmlOutput = new StreamResult (stringWriter);
      final Transformer transformer = _getXXESecureTransformer (XML_TRANSFORMER_IMPL);
      if (transformer != null)
      {
        transformer.setOutputProperty ("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty (OutputKeys.INDENT, "yes");
        transformer.transform (xmlInput, xmlOutput);
        return xmlOutput.getWriter ().toString ();
      }
    }
    catch (final Exception e)
    {
      LOGGER.error ("Error doc -> Formatted string", e);
    }
    return null;
  }

  public static String nodeToString (final Node node, final boolean omitXMLDeclaration)
  {
    final StringWriter writer = new StringWriter ();
    try
    {
      final Transformer t = _getXXESecureTransformer (XML_TRANSFORMER_IMPL);
      if (t != null)
      {
        t.setOutputProperty (OutputKeys.OMIT_XML_DECLARATION, omitXMLDeclaration ? "yes" : "no");
        t.setOutputProperty (OutputKeys.INDENT, "yes");
        t.transform (new DOMSource (node), new StreamResult (writer));
      }
    }
    catch (final Exception e)
    {
      LOGGER.error ("Error node -> Formatted string", e);
    }
    return writer.toString ();
  }

  public static Document stringToDocument (final String xml)
  {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();

    try
    {
      factory.setFeature (XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      DocumentBuilder builder;

      factory.setNamespaceAware (true);
      builder = factory.newDocumentBuilder ();
      return builder.parse (new InputSource (new StringReader (xml)));
    }
    catch (final Exception e)
    {
      LOGGER.error ("Error string -> doc", e);
    }

    return null;
  }

  @Nullable
  private static Transformer _getXXESecureTransformer (final String impl)
  {
    try
    {
      final TransformerFactory factory = TransformerFactory.newInstance (impl, null);
      factory.setFeature (XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
      factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_DTD, "");

      return factory.newTransformer ();
    }
    catch (final TransformerConfigurationException e)
    {
      LOGGER.error ("Error creating Transformer", e);
      return null;
    }
  }

  public static Document newDocumentFromNode (final Element element, final String nodeName) throws ParserConfigurationException
  {
    final NodeList nodes = element.getOwnerDocument ().getDocumentElement ().getElementsByTagNameNS ("*", nodeName);

    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
    factory.setNamespaceAware (true);
    factory.setFeature (XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute (XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    final Document newXmlDocument = factory.newDocumentBuilder ().newDocument ();
    final Element root = (Element) nodes.item (0);
    final Node copyNode = newXmlDocument.importNode (root, true);
    newXmlDocument.appendChild (copyNode);

    return newXmlDocument;
  }
}
