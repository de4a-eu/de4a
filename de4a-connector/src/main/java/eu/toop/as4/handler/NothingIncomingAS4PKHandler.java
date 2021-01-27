package eu.toop.as4.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.toop.connector.api.me.incoming.IMEIncomingHandler;
import eu.toop.connector.api.me.incoming.IncomingEDMErrorResponse;
import eu.toop.connector.api.me.incoming.IncomingEDMRequest;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.incoming.MEIncomingException; 
@Component
public class NothingIncomingAS4PKHandler implements IMEIncomingHandler{
	private static final Logger logger = LoggerFactory.getLogger (NothingIncomingAS4PKHandler.class); 
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
