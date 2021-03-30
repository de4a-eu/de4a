package eu.de4a.connector.as4.owner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import eu.de4a.connector.service.EvidenceTransferorManager;

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {
	/**
	 * Listener asincrono de spring events para la gestion de las peticiones a
	 * servicio externo monitorizadas
	 */

	private static final Log LOG = LogFactory.getLog(ContextRefreshedListener.class);
	@Autowired
	private EvidenceTransferorManager evidenceTransferorManager;
	
	enum EventMessages {		
		MessageOwner, MessageResponseOwner, ContextRefreshedEvent;
	}

	public void onApplicationEvent(ContextRefreshedEvent cse) {
		LOG.info("Processing event received: " + cse.getClass().getName());
		EventMessages eventClass = EventMessages.valueOf(cse.getClass().getSimpleName());
		switch(eventClass) {
			case MessageOwner:
				MessageOwner request = (MessageOwner) cse;
				evidenceTransferorManager.queueMessage(request);
				break;
			case MessageResponseOwner:
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