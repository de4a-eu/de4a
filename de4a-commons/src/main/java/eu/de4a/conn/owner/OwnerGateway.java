package eu.de4a.conn.owner;

import org.springframework.context.event.ApplicationEventMulticaster;
import org.w3c.dom.Element;

import eu.de4a.exception.MessageException;

public interface OwnerGateway {
	public Element   sendEvidenceRequest(Element evidenceRequest) throws MessageException;
	public default void  sendEvidenceRequestAsynchronous(Element evidenceRequest,ApplicationEventMulticaster applicationEventMulticaster) throws MessageException{
		//override in USI-pattern owner
	}
}
