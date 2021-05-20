package eu.de4a.connector.as4.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.peppolid.simple.participant.SimpleParticipantIdentifier;

import eu.de4a.connector.api.manager.EvidenceTransferorManager;
import eu.de4a.connector.as4.domibus.soap.DomibusClientWS;
import eu.de4a.connector.as4.domibus.soap.DomibusException;
import eu.de4a.connector.as4.domibus.soap.MessageFactory;
import eu.de4a.connector.as4.domibus.soap.ResponseAndHeader;
import eu.de4a.connector.as4.domibus.soap.auto.LargePayloadType;
import eu.de4a.connector.as4.domibus.soap.auto.ListPendingMessagesResponse;
import eu.de4a.connector.as4.domibus.soap.auto.Messaging;
import eu.de4a.connector.as4.domibus.soap.auto.PartInfo;
import eu.de4a.connector.as4.domibus.soap.auto.PartProperties;
import eu.de4a.connector.as4.domibus.soap.auto.Property;
import eu.de4a.connector.as4.domibus.soap.auto.RetrieveMessageResponse;
import eu.de4a.connector.as4.owner.MessageOwner;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseErrorException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.model.DomibusRequest;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.repository.DomibusRequestRepository;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.FileUtils;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;

@Component
public class DomibusGatewayClient implements As4GatewayInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusGatewayClient.class);
    @Autowired
    private DomibusClientWS domibusClientWS;
    @Autowired
    private DomibusRequestRepository domibusRequestRepository;
    @Autowired
    private EvidenceTransferorManager evidenceTransferorManager;

    @Value("${as4.gateway.implementation.bean}")
    private String nameAs4Gateway;
    @Value("#{'${domibus.endpoint.jvm:${domibus.endpoint:}}'}")
    private String domibusEndpoint;

    @Scheduled(fixedRate = 2000)
    public void lookUpPendingMessage() {
        if (nameAs4Gateway.equalsIgnoreCase(DomibusGatewayClient.class.getSimpleName())) {
            ListPendingMessagesResponse messagesPendingList = domibusClientWS.getPendindMessages(domibusEndpoint);
            messagesPendingList.getMessageID().forEach(id -> {
                DomibusRequest req = domibusRequestRepository.findById(id).orElseGet(() -> {
                    DomibusRequest domibusRequest = new DomibusRequest();
                    domibusRequest.setIdrequest(id);
                    return domibusRequest;
                });
                domibusRequestRepository.save(req);                    
                processMessage(id);
            });
        }
    }

    @Override
    public void sendMessage(String sender, NodeInfo nodeInfo, Element requestUsuario,
            List<TCPayload> payloads, boolean isRequest) throws MEOutgoingException {
        String idMessageAttached = "cid:message";
        String idCanonical = "cid:"
                + (isRequest ? DE4AConstants.TAG_EVIDENCE_REQUEST : DE4AConstants.TAG_EVIDENCE_RESPONSE);

        Messaging messageHeader = MessageFactory.makeMessage(sender, nodeInfo.getParticipantIdentifier(),
                nodeInfo.getDocumentIdentifier(), nodeInfo.getProcessIdentifier(),
                getAttachments(idMessageAttached, idCanonical));

        List<LargePayloadType> bodies = new ArrayList<>();
        LargePayloadType payload = new LargePayloadType();
        payload.setContentType(MediaType.APPLICATION_XML_VALUE);
        payload.setPayloadId(idMessageAttached);
        DataSource source = null;
        try {
            source = new ByteArrayDataSource(DOMUtils.documentToByte(requestUsuario.getOwnerDocument()),
                    MediaType.APPLICATION_XML_VALUE);
        } catch (MessageException e) {
            throw new MEOutgoingException(e.getMessage(), e);
        }
        payload.setValue(new DataHandler(source));
        bodies.add(payload);
        if (payloads != null) {
            payloads.forEach(p -> {
                LargePayloadType payloadtmp = new LargePayloadType();
                payloadtmp.setContentType(p.getMimeType());
                String cid = p.getContentID().startsWith("cid:") ? p.getContentID() : "cid:" + p.getContentID();
                payloadtmp.setPayloadId(cid);
                DataSource sourcetmp = new ByteArrayDataSource(p.getValue(), p.getMimeType());
                payloadtmp.setValue(new DataHandler(sourcetmp));
                bodies.add(payloadtmp);
            });
        }
        try {
            domibusClientWS.submitMessage(messageHeader, bodies, domibusEndpoint);
        } catch (DomibusException e) {
            throw new MEOutgoingException(e.getMessage(), e);
        }
    }

    public ResponseWrapper processResponseAs4(IncomingEDMResponse responseas4) {
        LOGGER.debug("Processing AS4 response...");
        
        ConnectorException ex = new ConnectorException().withLayer(LayerError.INTERNAL_FAILURE)
            .withFamily(FamilyErrorType.CONVERSION_ERROR)
            .withModule(ExternalModuleError.CONNECTOR_DR)
            .withHttpStatus(HttpStatus.OK);
        
        ResponseWrapper responsewrapper = new ResponseWrapper();
        responseas4.getAllAttachments().forEachValue(a -> {
            try {
                responsewrapper.addAttached(
                        FileUtils.getMultipart(a.getContentID().replace("cid:", ""), a.getMimeTypeString(), a.getData().bytes()));
            } catch (IOException e) {
                LOGGER.error("Error attaching files to response wrapper", e);
            }
            Document evidence = null;
            String requestId = "";
            try {
                evidence = DOMUtils.byteToDocument(a.getData().bytes());
                requestId = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, evidence.getDocumentElement());
            } catch (MessageException e1) {
                String errorMsg = "Error managing evidence DOM on AS4 response";
                LOGGER.error(errorMsg, e1);
                if(a.getContentID() != null && 
                        a.getContentID().contains(DE4AConstants.TAG_EVIDENCE_RESPONSE)) {
                    throw (ResponseTransferEvidenceException) ex.withMessageArg(errorMsg);
                } else {
                    throw (ResponseErrorException) ex.withMessageArg(errorMsg);
                }
            }
            responsewrapper.setTagDataId(a.getContentID());
            responsewrapper.setId(requestId);
            responsewrapper.setResponseDocument(evidence);
        });
        return responsewrapper;
    }

    private List<PartInfo> getAttachments(String idMessageAttached, String idCanonical) {
        List<PartInfo> attachments = new ArrayList<>();
        PartInfo partInfo = new PartInfo();
        PartInfo partInfo2 = new PartInfo();
        partInfo.setHref(idMessageAttached);
        partInfo2.setHref(idCanonical);

        Property prop = new Property();
        prop.setName("MimeType");
        prop.setValue(MediaType.APPLICATION_XML_VALUE);

        PartProperties props = new PartProperties();
        props.getProperty().add(prop);
        partInfo.setPartProperties(props);
        partInfo2.setPartProperties(props);
        attachments.add(partInfo);
        attachments.add(partInfo2);

        return attachments;
    }

    private MessageOwner buildMessageOwner(LargePayloadType payload, ResponseAndHeader response) {
        byte[] targetArray = null;
        try {
            targetArray = IOUtils.toByteArray(payload.getValue().getDataSource().getInputStream());
            if (targetArray != null && Base64.isBase64(targetArray)) {
                targetArray = Base64.decodeBase64(targetArray);
            }

            Document doc = DOMUtils.byteToDocument(targetArray);
            MessageOwner messageOwner = new MessageOwner();
            messageOwner.setMessage(doc.getDocumentElement());
            String idrequest = null;
            idrequest = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, doc.getDocumentElement());
            if (ObjectUtils.isEmpty(idrequest)) {
                LOGGER.error("EvidenceRequest without requestId");
            } else {
                messageOwner.setId(idrequest);
                SimpleParticipantIdentifier receiver = SimpleIdentifierFactory.INSTANCE.createParticipantIdentifier(
                        response.getInfo().getUserMessage().getPartyInfo().getTo().getPartyId().getType(), 
                        response.getInfo().getUserMessage().getPartyInfo().getTo().getPartyId().getValue());
                messageOwner.setReceiverId(receiver.getURIEncoded());
                SimpleParticipantIdentifier sender = SimpleIdentifierFactory.INSTANCE.createParticipantIdentifier(
                        response.getInfo().getUserMessage().getPartyInfo().getFrom().getPartyId().getType(), 
                        response.getInfo().getUserMessage().getPartyInfo().getFrom().getPartyId().getValue());
                messageOwner.setSenderId(sender.getURIEncoded());
                return messageOwner;
            }
        } catch (MessageException | IOException e) {
            LOGGER.error("Error retrieving bytes from domibus response", e);
        }
        return null;
    }
    
    private void processMessage(String requestId) {
        ResponseAndHeader response = domibusClientWS.getMessageWithHeader(requestId, domibusEndpoint);
        if (response != null && response.getInfo() != null) {
            RetrieveMessageResponse message = response.getMessage();

            message.getPayload().stream()
                    .filter(p -> p.getPayloadId().contains(DE4AConstants.TAG_EVIDENCE_REQUEST)).findFirst()
                    .ifPresentOrElse((value) -> evidenceTransferorManager.queueMessage(buildMessageOwner(value, response)), 
                            () -> LOGGER.error("EvidenceRequest not found!"));
        } else {
            LOGGER.error("Error getting message from Domibus. requestId = {}", requestId);
        }
    }
}
