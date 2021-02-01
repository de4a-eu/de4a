package eu.toop.as4.owner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import eu.toop.service.EvidenceTransferorManager;

@Component
public class ContextRefreshedListener  implements ApplicationListener<ContextRefreshedEvent> {
 /**
  * Listener asincrono de spring events para la gestion de las peticiones a servicio externo
  * monitorizadas
  * */ 

  private static final Log LOG = LogFactory.getLog(ContextRefreshedListener.class); 
 @Autowired
 private EvidenceTransferorManager evidenceTransferorManager;
  public void onApplicationEvent(ContextRefreshedEvent cse) { 
	  if(cse instanceof MessageOwner ==false) {
		  LOG.warn("Event received is not instance of MessageOwner de DOM, do not process. "+cse.getClass().getName());
	  }else { 
		  MessageOwner request= (MessageOwner)cse; 
		  evidenceTransferorManager.yourfather(request);
	  } 
  }
}