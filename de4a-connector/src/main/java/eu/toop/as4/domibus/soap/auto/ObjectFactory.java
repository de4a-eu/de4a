//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.01 a las 02:37:52 PM CET 
//


package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.toop.as4.domibus.soap.auto package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetStatusRequest_QNAME = new QName("http://org.ecodex.backend/1_1/", "getStatusRequest");
    private final static QName _StatusRequest_QNAME = new QName("http://org.ecodex.backend/1_1/", "statusRequest");
    private final static QName _GetStatusResponse_QNAME = new QName("http://org.ecodex.backend/1_1/", "getStatusResponse");
    private final static QName _GetErrorsRequest_QNAME = new QName("http://org.ecodex.backend/1_1/", "getErrorsRequest");
    private final static QName _GetMessageErrorsResponse_QNAME = new QName("http://org.ecodex.backend/1_1/", "getMessageErrorsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.toop.as4.domibus.soap.auto
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Messaging }
     * 
     */
    public Messaging createMessaging() {
        return new Messaging();
    }

    /**
     * Create an instance of {@link UserMessage }
     * 
     */
    public UserMessage createUserMessage() {
        return new UserMessage();
    }

    /**
     * Create an instance of {@link MessageInfo }
     * 
     */
    public MessageInfo createMessageInfo() {
        return new MessageInfo();
    }

    /**
     * Create an instance of {@link PartyInfo }
     * 
     */
    public PartyInfo createPartyInfo() {
        return new PartyInfo();
    }

    /**
     * Create an instance of {@link PartyId }
     * 
     */
    public PartyId createPartyId() {
        return new PartyId();
    }

    /**
     * Create an instance of {@link From }
     * 
     */
    public From createFrom() {
        return new From();
    }

    /**
     * Create an instance of {@link To }
     * 
     */
    public To createTo() {
        return new To();
    }

    /**
     * Create an instance of {@link CollaborationInfo }
     * 
     */
    public CollaborationInfo createCollaborationInfo() {
        return new CollaborationInfo();
    }

    /**
     * Create an instance of {@link Service }
     * 
     */
    public Service createService() {
        return new Service();
    }

    /**
     * Create an instance of {@link AgreementRef }
     * 
     */
    public AgreementRef createAgreementRef() {
        return new AgreementRef();
    }

    /**
     * Create an instance of {@link PayloadInfo }
     * 
     */
    public PayloadInfo createPayloadInfo() {
        return new PayloadInfo();
    }

    /**
     * Create an instance of {@link PartInfo }
     * 
     */
    public PartInfo createPartInfo() {
        return new PartInfo();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link PartProperties }
     * 
     */
    public PartProperties createPartProperties() {
        return new PartProperties();
    }

    /**
     * Create an instance of {@link MessageProperties }
     * 
     */
    public MessageProperties createMessageProperties() {
        return new MessageProperties();
    }

    /**
     * Create an instance of {@link FaultDetail }
     * 
     */
    public FaultDetail createFaultDetail() {
        return new FaultDetail();
    }

    /**
     * Create an instance of {@link RetrieveMessageRequest }
     * 
     */
    public RetrieveMessageRequest createRetrieveMessageRequest() {
        return new RetrieveMessageRequest();
    }

    /**
     * Create an instance of {@link RetrieveMessageResponse }
     * 
     */
    public RetrieveMessageResponse createRetrieveMessageResponse() {
        return new RetrieveMessageResponse();
    }

    /**
     * Create an instance of {@link LargePayloadType }
     * 
     */
    public LargePayloadType createLargePayloadType() {
        return new LargePayloadType();
    }

    /**
     * Create an instance of {@link ListPendingMessagesRequest }
     * 
     */
    public ListPendingMessagesRequest createListPendingMessagesRequest() {
        return new ListPendingMessagesRequest();
    }

    /**
     * Create an instance of {@link ListPendingMessagesResponse }
     * 
     */
    public ListPendingMessagesResponse createListPendingMessagesResponse() {
        return new ListPendingMessagesResponse();
    }

    /**
     * Create an instance of {@link MessageErrorsRequest }
     * 
     */
    public MessageErrorsRequest createMessageErrorsRequest() {
        return new MessageErrorsRequest();
    }

    /**
     * Create an instance of {@link MessageStatusRequest }
     * 
     */
    public MessageStatusRequest createMessageStatusRequest() {
        return new MessageStatusRequest();
    }

    /**
     * Create an instance of {@link SubmitRequest }
     * 
     */
    public SubmitRequest createSubmitRequest() {
        return new SubmitRequest();
    }

    /**
     * Create an instance of {@link SubmitResponse }
     * 
     */
    public SubmitResponse createSubmitResponse() {
        return new SubmitResponse();
    }

    /**
     * Create an instance of {@link GetStatusRequest }
     * 
     */
    public GetStatusRequest createGetStatusRequest() {
        return new GetStatusRequest();
    }

    /**
     * Create an instance of {@link StatusRequest }
     * 
     */
    public StatusRequest createStatusRequest() {
        return new StatusRequest();
    }

    /**
     * Create an instance of {@link GetErrorsRequest }
     * 
     */
    public GetErrorsRequest createGetErrorsRequest() {
        return new GetErrorsRequest();
    }

    /**
     * Create an instance of {@link ErrorResultImplArray }
     * 
     */
    public ErrorResultImplArray createErrorResultImplArray() {
        return new ErrorResultImplArray();
    }

    /**
     * Create an instance of {@link PayloadType }
     * 
     */
    public PayloadType createPayloadType() {
        return new PayloadType();
    }

    /**
     * Create an instance of {@link ErrorResultImpl }
     * 
     */
    public ErrorResultImpl createErrorResultImpl() {
        return new ErrorResultImpl();
    }

    /**
     * Create an instance of {@link PayloadURLType }
     * 
     */
    public PayloadURLType createPayloadURLType() {
        return new PayloadURLType();
    }

    /**
     * Create an instance of {@link Base64Binary }
     * 
     */
    public Base64Binary createBase64Binary() {
        return new Base64Binary();
    }

    /**
     * Create an instance of {@link HexBinary }
     * 
     */
    public HexBinary createHexBinary() {
        return new HexBinary();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatusRequest }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetStatusRequest }{@code >}
     */
    @XmlElementDecl(namespace = "http://org.ecodex.backend/1_1/", name = "getStatusRequest")
    public JAXBElement<GetStatusRequest> createGetStatusRequest(GetStatusRequest value) {
        return new JAXBElement<GetStatusRequest>(_GetStatusRequest_QNAME, GetStatusRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusRequest }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link StatusRequest }{@code >}
     */
    @XmlElementDecl(namespace = "http://org.ecodex.backend/1_1/", name = "statusRequest")
    public JAXBElement<StatusRequest> createStatusRequest(StatusRequest value) {
        return new JAXBElement<StatusRequest>(_StatusRequest_QNAME, StatusRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageStatus }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link MessageStatus }{@code >}
     */
    @XmlElementDecl(namespace = "http://org.ecodex.backend/1_1/", name = "getStatusResponse")
    public JAXBElement<MessageStatus> createGetStatusResponse(MessageStatus value) {
        return new JAXBElement<MessageStatus>(_GetStatusResponse_QNAME, MessageStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetErrorsRequest }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetErrorsRequest }{@code >}
     */
    @XmlElementDecl(namespace = "http://org.ecodex.backend/1_1/", name = "getErrorsRequest")
    public JAXBElement<GetErrorsRequest> createGetErrorsRequest(GetErrorsRequest value) {
        return new JAXBElement<GetErrorsRequest>(_GetErrorsRequest_QNAME, GetErrorsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorResultImplArray }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ErrorResultImplArray }{@code >}
     */
    @XmlElementDecl(namespace = "http://org.ecodex.backend/1_1/", name = "getMessageErrorsResponse")
    public JAXBElement<ErrorResultImplArray> createGetMessageErrorsResponse(ErrorResultImplArray value) {
        return new JAXBElement<ErrorResultImplArray>(_GetMessageErrorsResponse_QNAME, ErrorResultImplArray.class, null, value);
    }

}
