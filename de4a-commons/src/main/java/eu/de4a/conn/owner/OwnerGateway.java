package eu.de4a.conn.owner;

import java.util.List;

import eu.de4a.exception.MessageException;
import eu.toop.connector.api.rest.TCPayload;

public interface OwnerGateway {
	public List<TCPayload>   sendEvidenceRequest(org.w3c.dom.Element evidenceRequest) throws MessageException;
}
