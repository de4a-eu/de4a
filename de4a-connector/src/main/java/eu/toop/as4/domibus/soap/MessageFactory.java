package eu.toop.as4.domibus.soap;
 
import java.util.List;

import eu.toop.as4.domibus.soap.auto.CollaborationInfo;
import eu.toop.as4.domibus.soap.auto.From;
import eu.toop.as4.domibus.soap.auto.MessageProperties;
import eu.toop.as4.domibus.soap.auto.Messaging;
import eu.toop.as4.domibus.soap.auto.PartInfo;
import eu.toop.as4.domibus.soap.auto.PartyId;
import eu.toop.as4.domibus.soap.auto.PartyInfo;
import eu.toop.as4.domibus.soap.auto.PayloadInfo;
import eu.toop.as4.domibus.soap.auto.Property;
import eu.toop.as4.domibus.soap.auto.Service;
import eu.toop.as4.domibus.soap.auto.To;
import eu.toop.as4.domibus.soap.auto.UserMessage;

public class MessageFactory {

	private static final String ROLE_FROM="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator";
	private static final String ROLE_TO="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder";
	private static final String PARTY_TYPE_ID_DEFAULT="urn:oasis:names:tc:ebcore:partyid-type:unregistered";
	private static final String SERVICE_TYPE_DEFAULT="toop-procid-agreement";
	private static final String SERVICE_VALUE_DEFAULT="bdx:noprocess";
	private static final String ACTION_VALUE_DEFAULT="toop-doctypeid-qns::";
	private static final String MESSAGE_PRO_NAME_SENDER="originalSender";
	private static final String MESSAGE_PRO_VALUE_SENDER_DEFAULT="urn:oasis:names:tc:ebcore:partyid-type:unregistered:C1";
	private static final String MESSAGE_PRO_NAME_RECIPIENT="finalRecipient";
	private static final String MESSAGE_PRO_VALUE_RECIPIENT_DEFAULT="urn:oasis:names:tc:ebcore:partyid-type:unregistered:C4";
	
	
	public static Messaging makeMessage(String domibusMeId,String domibusOtherId,String conversationId,  String evidenceService,List<PartInfo> attacheds) {
		Messaging messaging = new Messaging(); 
		UserMessage userMessage=new UserMessage();
		messaging.setUserMessage(userMessage);
		 
    	PartyInfo partyInfo=new PartyInfo();
    	From from = new From();
    	from.setRole(ROLE_FROM); 
    	from.setPartyId(getPartyId(domibusMeId));
    	To to = new To();
    	to.setRole(ROLE_TO); 
    	to.setPartyId(getPartyId(domibusOtherId));
    	partyInfo.setTo(to); 
    	partyInfo.setFrom(from);
    	userMessage.setPartyInfo(partyInfo);
    	
    	userMessage.setCollaborationInfo(getCollaborationInfo(conversationId,evidenceService));
    	MessageProperties messagesProperties=new MessageProperties();
    	Property pro1=new Property();
    	pro1.setName(MESSAGE_PRO_NAME_SENDER);
    	pro1.setValue(MESSAGE_PRO_VALUE_SENDER_DEFAULT);
    	messagesProperties.getProperty().add(pro1);
    	Property pro2=new Property();
    	pro2.setName(MESSAGE_PRO_NAME_RECIPIENT);
    	pro2.setValue(MESSAGE_PRO_VALUE_RECIPIENT_DEFAULT);
    	messagesProperties.getProperty().add(pro2);
    	userMessage.setMessageProperties(messagesProperties);
    	PayloadInfo payloadInfo=new  PayloadInfo(); 
    	payloadInfo.getPartInfo().addAll(attacheds);
    	userMessage.setPayloadInfo(payloadInfo); 
    	messaging.setUserMessage(userMessage);
		return messaging;
	}
	private static CollaborationInfo getCollaborationInfo(String conversationId,String evidenceService) {
		CollaborationInfo col=new CollaborationInfo(); 
		Service service=new Service();
		service.setType(SERVICE_TYPE_DEFAULT);
		service.setValue(SERVICE_VALUE_DEFAULT);
		col.setService(service);
		String action=evidenceService.startsWith(ACTION_VALUE_DEFAULT)?evidenceService:ACTION_VALUE_DEFAULT+evidenceService;
		col.setAction(action);
		col.setConversationId(conversationId); 
		return col;
	}
	 
/*RetrieveMessageRequest request = new RetrieveMessageRequest(); 
    	getWebServiceTemplate().setDefaultUri(ENDPOINT_DOMIBUS);
    	request.setMessageID(messageId);
    	Messaging mes=new Messaging();
    	mes.setMustUnderstand(true);
    	UserMessage userMessage=new UserMessage();
    	PartyInfo partyInfo=new PartyInfo();
    	From from = new From();
    	from.setRole(ROLE_FROM); 
    	from.setPartyId(getPartyId(domibusMeId));
    	To to = new To();
    	to.setRole(ROLE_FROM); 
    	to.setPartyId(getPartyId(domibusOtherId));
    	partyInfo.setTo(to); 
    	userMessage.setPartyInfo(partyInfo);
    	
    	
    	mes.setUserMessage(userMessage);*/
	public static UserMessage instance(){
		return new UserMessage();
	}
	private static PartyId getPartyId(String id ) {
	    	PartyId partyId =new PartyId();
	    	partyId.setValue(id);
	    	partyId.setType(PARTY_TYPE_ID_DEFAULT);
	    	return partyId;
	    }
}
