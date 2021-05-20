package eu.de4a.connector.as4.domibus.soap;

import java.io.IOException;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.support.MarshallingUtils;
import org.w3c.dom.Document;

import eu.de4a.connector.as4.domibus.soap.auto.LargePayloadType;
import eu.de4a.connector.as4.domibus.soap.auto.ListPendingMessagesRequest;
import eu.de4a.connector.as4.domibus.soap.auto.ListPendingMessagesResponse;
import eu.de4a.connector.as4.domibus.soap.auto.Messaging;
import eu.de4a.connector.as4.domibus.soap.auto.RetrieveMessageRequest;
import eu.de4a.connector.as4.domibus.soap.auto.RetrieveMessageResponse;
import eu.de4a.connector.as4.domibus.soap.auto.SubmitRequest;
import eu.de4a.connector.as4.domibus.soap.auto.SubmitResponse;

public class DomibusClientWS extends WebServiceGatewaySupport {

    private static final Logger LOG = LoggerFactory.getLogger(DomibusClientWS.class);

    public DomibusClientWS(AxiomSoapMessageFactory messageFactory) {
        this.setMessageFactory(messageFactory);
    }

    public ListPendingMessagesResponse getPendindMessages(String domibusEndpoint) {
        LOG.debug("Getting Pending Messages from Domibus");
        ListPendingMessagesRequest request = new ListPendingMessagesRequest();
        getWebServiceTemplate().setDefaultUri(domibusEndpoint);
        return (ListPendingMessagesResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
    }

    public RetrieveMessageResponse getMessage(String id, String domibusEndpoint) {
        LOG.debug("Getting messge from Domibus --> id: {}", id);
        RetrieveMessageRequest request = new RetrieveMessageRequest();
        getWebServiceTemplate().setDefaultUri(domibusEndpoint);
        request.setMessageID(id);

        return (RetrieveMessageResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
    }

    public ResponseAndHeader getMessageWithHeader(String id, String domibusEndpoint) {
        final RetrieveMessageRequest request = new RetrieveMessageRequest();
        getWebServiceTemplate().setDefaultUri(domibusEndpoint);
        request.setMessageID(id);
        return getWebServiceTemplate().sendAndReceive(new WebServiceMessageCallback() {
            public void doWithMessage(WebServiceMessage message) throws IOException {
                MarshallingUtils.marshal(getWebServiceTemplate().getMarshaller(), request, message);
            }
        }, new WebServiceMessageExtractor<ResponseAndHeader>() {
            public ResponseAndHeader extractData(WebServiceMessage message) throws IOException {
                SoapHeader header = ((SoapMessage) message).getSoapHeader();
                SoapHeaderElement messaging = header.examineHeaderElements(
                        new QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "Messaging"))
                        .next();
                return new ResponseAndHeader(
                        (Messaging) getWebServiceTemplate().getUnmarshaller().unmarshal(messaging.getSource()),
                        (RetrieveMessageResponse) MarshallingUtils.unmarshal(getWebServiceTemplate().getUnmarshaller(),
                                message));
            }
        });
    }

    public SubmitResponse submitMessage(final Messaging messsageHeader, List<LargePayloadType> bodies,
            String domibusEndpoint) throws DomibusException {
        LOG.debug("Submitting message between Domibus parties");
        SubmitRequest request = new SubmitRequest();
        getWebServiceTemplate().setDefaultUri(domibusEndpoint);
        request.getPayload().addAll(bodies);
        try {
            return (SubmitResponse) getWebServiceTemplate().marshalSendAndReceive(request,
                    new WebServiceMessageCallback() {
                        public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
                            try {
                                SoapMessage soapMessage = (SoapMessage) message;
                                SoapHeader header = soapMessage.getSoapHeader();
                                DOMSource headerSource = getHeaderMessageSubmit(messsageHeader);
                                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                                Transformer tr = transformerFactory.newTransformer();
                                tr.transform(headerSource, header.getResult());
                            } catch (Exception e) {
                                LOG.error("Domibus SOAP header processing error", e);
                            }
                        }
                    });
        } catch (Exception e) {
            throw new DomibusException("Error SOAP connecting with Domibus - " + e.getMessage(), e);
        }
    }

    private DOMSource getHeaderMessageSubmit(Messaging mess)
            throws JAXBException, TransformerConfigurationException, ParserConfigurationException {
        JAXBContext jc = JAXBContext.newInstance(Messaging.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(mess, document);
        // writing the Document object to console
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = tf.newTransformer();
        // set the properties for a formatted output - if false the output will be on
        // one single line
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return new DOMSource(document);
    }
}
