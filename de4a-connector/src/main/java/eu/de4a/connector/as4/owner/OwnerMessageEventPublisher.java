package eu.de4a.connector.as4.owner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import eu.de4a.connector.as4.client.RequestWrapper;


@Component
public class OwnerMessageEventPublisher {
	  private static final Log LOG = LogFactory.getLog(OwnerMessageEventPublisher.class);
	    @Autowired
	    private ApplicationEventMulticaster applicationEventMulticaster;
	    @Autowired
	    private ApplicationContext context;
	    public void publishCustomEvent(RequestWrapper request) {
	    	LOG.debug("Publishing as4 request");
	    	MessageOwner customSpringEvent = new MessageOwner(context);
	    	customSpringEvent.setMessage((Element)request.getRequest());
	    	customSpringEvent.setId(request.getId());
	    	customSpringEvent.setSenderId(request.getSenderId());
	    	customSpringEvent.setReceiverId(request.getReceiverId());
	    	customSpringEvent.setReturnService(request.getReturnServiceUri());
	    	applicationEventMulticaster.multicastEvent( customSpringEvent);
	    }
}
