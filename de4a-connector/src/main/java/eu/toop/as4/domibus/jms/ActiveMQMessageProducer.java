package eu.toop.as4.domibus.jms; 
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
 
public class ActiveMQMessageProducer {
  
 
    private ConnectionFactory connFactory;
    private Connection connection;
    private Session session;
    private Destination destination; 
    private MessageProducer msgProducer;
 
    private String activeMqBrokerUri;
    private String username;
    private String password;
 
    public ActiveMQMessageProducer(final String activeMqBrokerUri, final String username, final String password) {
        super();
        this.activeMqBrokerUri = activeMqBrokerUri;
        this.username = username;
        this.password = password;
    }
 
    public void setup(final boolean transacted, final boolean isDestinationTopic, final String destinationName)  throws JMSException {
        setConnectionFactory(activeMqBrokerUri, username, password);
        setConnection();
        setSession(transacted);
        setDdestination(isDestinationTopic, destinationName);
        setMsgProducer();
    }
 
    public void close() throws JMSException {
        if (msgProducer != null) {
            msgProducer.close();
            msgProducer = null;
        }
 
        if (session != null) {
            session.close();
            session = null;
        }
        if (connection != null) {
            connection.close();
            connection = null;
        }
 
    }
 
    public void commit(final boolean transacted) throws JMSException {
        if (transacted) {
            session.commit();
        }
    }
 
    public void sendMessage(final String actionVal) throws JMSException {
    	MapMessage textMessage = makeMessage(actionVal);
        msgProducer.send(destination, textMessage); 
 
    }
	 private MapMessage makeMessage(String action) throws JMSException {
		 MapMessage messageMap = session.createMapMessage();
		 // Declare message as submit
		 messageMap.setStringProperty("messageType", "submitMessage");
		 // Set up the Communication properties for the message
		 messageMap.setStringProperty("service", "demoService");
		 messageMap.setStringProperty("action", "demoAction");
		 messageMap.setStringProperty("conversationId", "");
		 messageMap.setStringProperty("fromPartyId", "GW1");
		 messageMap.setStringProperty("fromPartyIdType", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
		 messageMap.setStringProperty("fromRole", "buyer");
		 messageMap.setStringProperty("toPartyId", "GW1");
		 messageMap.setStringProperty("toPartyIdType", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
		 messageMap.setStringProperty("toRole", "seller");
		 messageMap.setStringProperty("originalSender", "sending_buyer_id");
		 messageMap.setStringProperty("finalRecipient", "receiving_seller_id");
		 messageMap.setStringProperty("serviceType", "");
		 messageMap.setStringProperty("protocol", "AS4");
		 messageMap.setStringProperty("refToMessageId", "");
		 messageMap.setStringProperty("agreementRef", "");
		 messageMap.setJMSCorrelationID("MESS1");
		 //Set up the payload properties
		 messageMap.setStringProperty("totalNumberOfPayloads", "3");
		 messageMap.setStringProperty("payload_1_mimeContentId", "cid:cid_of_payload_1");
		 messageMap.setStringProperty("payload_2_mimeContentId", "cid:cid_of_payload_2");
		 messageMap.setStringProperty("payload_3_mimeContentId", "cid:cid_of_payload_3");
		 messageMap.setStringProperty("payload_1_mimeType", "application/xml");
		 messageMap.setStringProperty("payload_2_mimeType", "application/xml");
		 messageMap.setStringProperty("payload_3_mimeType", "application/xml");
		 messageMap.setStringProperty("payload_1_description", "description1");
		 messageMap.setStringProperty("payload_2_description", "description2");
		 messageMap.setStringProperty("payload_3_description", "description3");
		 messageMap.setStringProperty("payload_1_fileName", "filenameLocation1");
		 messageMap.setStringProperty("payload_2_fileName", "filenameLocation2");
		 messageMap.setStringProperty("payload_3_fileName", "filenameLocation3");
		 String pay1 = "<XML><test></test></XML>";
		 byte[] payload = pay1.getBytes();
		 messageMap.setBytes("payload_1", payload);
		 messageMap.setBytes("payload_2", payload);
		 messageMap.setBytes("payload_3", payload);
		 return messageMap;
	 }
//    private TextMessage buildTextMessageWithProperty(final String action) throws JMSException {
//        Gson gson = new Gson();
//        String eventMsg = gson.toJson(DataUtil.buildDummyCustomerEvent());
//        TextMessage textMessage = session.createTextMessage(eventMsg);
// 
//        Random rand = new Random();
//        int value = rand.nextInt(100);
//        textMessage.setStringProperty(ACTION_HEADER, action);
//        textMessage.setStringProperty(ACTION_ID_HEADER, String.valueOf(value));
// 
//        return textMessage;
//    }
 
    private void setDdestination(final boolean isDestinationTopic, final String destinationName) throws JMSException {
        if (isDestinationTopic) {
            destination = session.createTopic(destinationName);
        } else {
            destination = session.createQueue(destinationName);
        }
    }
 
    private void setMsgProducer() throws JMSException {
        msgProducer = session.createProducer(destination);
 
    }
 
    private void setSession(final boolean transacted) throws JMSException {
        // transacted=true for better performance to push message in batch mode
        session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
    }
 
    private void setConnection() throws JMSException {
        connection = connFactory.createConnection();
        connection.start();
    }
 
    private void setConnectionFactory(final String activeMqBrokerUri, final String username, final String password) {
        connFactory = new ActiveMQConnectionFactory(username, password, activeMqBrokerUri);
 
        ((ActiveMQConnectionFactory) connFactory).setUseAsyncSend(true);
 
        RedeliveryPolicy policy = ((ActiveMQConnectionFactory) connFactory).getRedeliveryPolicy();
        policy.setInitialRedeliveryDelay(500);
        policy.setBackOffMultiplier(2);
        policy.setUseExponentialBackOff(true);
        policy.setMaximumRedeliveries(2);
    }
 
}