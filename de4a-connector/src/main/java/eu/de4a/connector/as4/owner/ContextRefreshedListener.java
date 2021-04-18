package eu.de4a.connector.as4.owner;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import eu.de4a.connector.api.manager.EvidenceTransferorManager;

/**
 * Asynchronous listener from spring events for manage requests
 * to external monitored service
 *
 */
@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Log LOG = LogFactory.getLog(ContextRefreshedListener.class);
	@Autowired
	private EvidenceTransferorManager evidenceTransferorManager;

	enum EventMessages {
		MESSAGE_OWNER("MessageOwner"), MESSAGE_RESPONSE_OWNER("MessageResponseOwner"), CONTEXT_REFRESHED_EVENT("ContextRefreshedEvent");
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
			case MESSAGE_OWNER:
				MessageOwner request = (MessageOwner) cse;
				evidenceTransferorManager.queueMessage(request);
				break;
			case MESSAGE_RESPONSE_OWNER:
				MessageResponseOwner response = (MessageResponseOwner) cse;
				evidenceTransferorManager.queueMessageResponse(response);
				break;
			default:
				LOG.warn("Event received is not instance of MessageOwner or MessageResponseOwner de DOM, do not process. "
						+ cse.getClass().getName());
				break;
		}
	}
}
