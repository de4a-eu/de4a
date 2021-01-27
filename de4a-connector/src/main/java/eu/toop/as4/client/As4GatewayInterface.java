package eu.toop.as4.client;

import java.util.List;

import org.w3c.dom.Element;

import eu.de4a.conn.api.smp.NodeInfo;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload; 

public interface As4GatewayInterface {
	  public   void sendMessage(String sender,NodeInfo receiver, String evidenceServiceUri,Element requestUsuario,List<TCPayload>payloads,boolean request) throws MEOutgoingException ;
	  public   ResponseWrapper processResponseAs4(IncomingEDMResponse data);
	  
}
