package eu.de4a.connector.as4.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import eu.de4a.connector.api.manager.EvidenceTransferorManager;
import eu.de4a.connector.api.manager.ResponseManager;
import eu.de4a.connector.as4.client.ResponseWrapper;
import eu.de4a.connector.as4.owner.MessageRequestOwner;

/**
 * Asynchronous listener from spring events for manage requests
 * to external monitored service
 *
 */
@Component
public class ContextEventsListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Log LOG = LogFactory.getLog(ContextEventsListener.class);
    @Autowired
    private EvidenceTransferorManager evidenceTransferorManager;
    @Autowired
    private ResponseManager responseManager;

    enum EventMessages {
        MESSAGE_REQUEST_OWNER("MessageRequestOwner"), RESPONSE_WRAPPER("ResponseWrapper"), CONTEXT_REFRESHED_EVENT("ContextRefreshedEvent");
        private String name;
        private static Map<String, EventMessages> lookup = new HashMap<>();

        static {
            for (EventMessages obj : EventMessages.values()) {
                lookup.put(obj.getName(), obj);
            }
        }

        EventMessages(String messageName) {
            name = messageName;
        }

        public String getName() {
            return name;
        }

        public static EventMessages fromValue(String v) {
            return lookup.get(v);
        }
    }

    public void onApplicationEvent(ContextRefreshedEvent cse) {
        LOG.info("Processing event received: " + cse.getClass().getName());
        
        EventMessages eventClass = EventMessages.fromValue(cse.getClass().getSimpleName());
        switch(eventClass) {
            case MESSAGE_REQUEST_OWNER:
                MessageRequestOwner request = (MessageRequestOwner) cse;
                evidenceTransferorManager.queueMessage(request);
                break;
            case RESPONSE_WRAPPER:
                ResponseWrapper response = (ResponseWrapper) cse;
                responseManager.processAS4Response(response);
                break;
            default:
                LOG.warn("Event received is not an instance of MessageOwner, it won´t be processed. "
                        + cse.getClass().getName());
                break;
        }
    }
}
