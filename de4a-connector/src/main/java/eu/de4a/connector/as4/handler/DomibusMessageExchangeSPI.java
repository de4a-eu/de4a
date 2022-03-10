package eu.de4a.connector.as4.handler;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.dcng.api.me.IMessageExchangeSPI;
import com.helger.dcng.api.me.incoming.IMEIncomingHandler;
import com.helger.dcng.api.me.model.MEMessage;
import com.helger.dcng.api.me.outgoing.IMERoutingInformation;
import com.helger.dcng.api.me.outgoing.MEOutgoingException;

public class DomibusMessageExchangeSPI implements IMessageExchangeSPI {
    
    public static final String ID = "domibus";
    private static final Logger LOGGER = LoggerFactory.getLogger (DomibusMessageExchangeSPI.class);

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public void init(ServletContext aServletContext, IMEIncomingHandler aIncomingHandler) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendOutgoing(IMERoutingInformation aRoutingInfo, MEMessage aMessage) throws MEOutgoingException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void shutdown(ServletContext aServletContext) {
        // TODO Auto-generated method stub
        
    }
    
}
