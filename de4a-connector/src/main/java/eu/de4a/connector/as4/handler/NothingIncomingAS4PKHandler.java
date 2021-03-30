package eu.de4a.connector.as4.handler;

import org.springframework.stereotype.Component;

import eu.toop.connector.api.me.incoming.IMEIncomingHandler;
import eu.toop.connector.api.me.incoming.IncomingEDMErrorResponse;
import eu.toop.connector.api.me.incoming.IncomingEDMRequest;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.incoming.MEIncomingException; 

@Component
public class NothingIncomingAS4PKHandler implements IMEIncomingHandler{
	public void handleIncomingRequest(IncomingEDMRequest aRequest) throws MEIncomingException {
		// TODO Auto-generated method stub
		 
	}

	@Override
	public void handleIncomingResponse(IncomingEDMResponse aResponse) throws MEIncomingException {
		// TODO Auto-generated method stub
		 
	}

	@Override
	public void handleIncomingErrorResponse(IncomingEDMErrorResponse aErrorResponse) throws MEIncomingException {
		// TODO Auto-generated method stub
		
	}

}
