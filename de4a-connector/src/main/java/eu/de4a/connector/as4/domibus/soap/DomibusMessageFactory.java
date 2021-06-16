package eu.de4a.connector.as4.domibus.soap;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import eu.de4a.connector.as4.domibus.soap.auto.CollaborationInfo;
import eu.de4a.connector.as4.domibus.soap.auto.From;
import eu.de4a.connector.as4.domibus.soap.auto.MessageProperties;
import eu.de4a.connector.as4.domibus.soap.auto.Messaging;
import eu.de4a.connector.as4.domibus.soap.auto.PartInfo;
import eu.de4a.connector.as4.domibus.soap.auto.PartyId;
import eu.de4a.connector.as4.domibus.soap.auto.PartyInfo;
import eu.de4a.connector.as4.domibus.soap.auto.PayloadInfo;
import eu.de4a.connector.as4.domibus.soap.auto.Property;
import eu.de4a.connector.as4.domibus.soap.auto.Service;
import eu.de4a.connector.as4.domibus.soap.auto.To;
import eu.de4a.connector.as4.domibus.soap.auto.UserMessage;
import eu.de4a.util.DE4AConstants;
import eu.toop.connector.api.TCIdentifierFactory;

@Component
public class DomibusMessageFactory {

    private static final String ROLE_FROM = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator";
    private static final String ROLE_TO = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder";
    private static final String MESSAGE_PRO_NAME_SENDER = "originalSender";
    private static final String MESSAGE_PRO_NAME_RECIPIENT = "finalRecipient";
    
    @Value("${phase4.send.fromparty.id:#{null}}")
    private String fromParty;
    @Value("${phase4.send.toparty.id:#{null}}")
    private String toParty;
    @Value("${phase4.send.fromparty.id.type:#{null}}")
    private String fromPartyType;
    @Value("${phase4.send.toparty.id.type:#{null}}")
    private String toPartyType;
    
    public Messaging makeMessage(String sender, String receiver, String documentIdentifier, 
            String processIdentifier, List<PartInfo> attacheds) {
        Messaging messaging = new Messaging();
        UserMessage userMessage = new UserMessage();
        messaging.setUserMessage(userMessage);
        userMessage.setPartyInfo(getPartyInfo());

        userMessage.setCollaborationInfo(getCollaborationInfo(documentIdentifier, processIdentifier));
        MessageProperties messagesProperties = new MessageProperties();
        Property pro1 = new Property();
        pro1.setName(MESSAGE_PRO_NAME_SENDER);
        pro1.setType(TCIdentifierFactory.PARTICIPANT_SCHEME);
        pro1.setValue(sender);
        messagesProperties.getProperty().add(pro1);
        Property pro2 = new Property();
        pro2.setName(MESSAGE_PRO_NAME_RECIPIENT);
        pro2.setType(TCIdentifierFactory.PARTICIPANT_SCHEME);
        pro2.setValue(receiver);
        messagesProperties.getProperty().add(pro2);
        userMessage.setMessageProperties(messagesProperties);
        PayloadInfo payloadInfo = new PayloadInfo();
        payloadInfo.getPartInfo().addAll(attacheds);
        userMessage.setPayloadInfo(payloadInfo);
        messaging.setUserMessage(userMessage);
        return messaging;
    }

    private static CollaborationInfo getCollaborationInfo(String documentIdentifier, String processIdentifier) {
        CollaborationInfo col = new CollaborationInfo();
        Service service = new Service();
        service.setType(DE4AConstants.PROCESS_SCHEME);
        service.setValue(processIdentifier);
        col.setService(service);
        col.setAction(DE4AConstants.DOCTYPE_SCHEME + "::" + documentIdentifier);
        return col;
    }

    public static UserMessage instance() {
        return new UserMessage();
    }

    private static PartyId getPartyId(String id, String partyType) {
        PartyId partyId = new PartyId();
        partyId.setValue(id);
        partyId.setType(partyType);
        return partyId;
    }
    
    private PartyInfo getPartyInfo() {
        PartyInfo partyInfo = new PartyInfo();
        From from = new From();
        from.setRole(ROLE_FROM);
        from.setPartyId(getPartyId(this.fromParty, this.fromPartyType));
        To to = new To();
        if(!ObjectUtils.isEmpty(this.toParty)) {
            to.setRole(ROLE_TO);
            to.setPartyId(getPartyId(this.toParty, this.toPartyType));
        }
        partyInfo.setTo(to);
        partyInfo.setFrom(from);
        
        return partyInfo;
    }
}
