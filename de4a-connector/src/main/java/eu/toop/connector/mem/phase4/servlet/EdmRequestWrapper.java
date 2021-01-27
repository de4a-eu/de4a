package eu.toop.connector.mem.phase4.servlet;

import java.util.List;

import com.helger.phase4.attachment.WSS4JAttachment;

import eu.toop.connector.api.me.incoming.IMEIncomingTransportMetadata;
import eu.toop.connector.api.me.incoming.IncomingEDMRequest;
import eu.toop.edm.EDMRequest;

public class EdmRequestWrapper extends IncomingEDMRequest{
	private List<WSS4JAttachment>attacheds;
	public EdmRequestWrapper(EDMRequest aRequest, String sTopLevelContentID, IMEIncomingTransportMetadata aMetadata,List<WSS4JAttachment>attacheds) {
		super(aRequest, sTopLevelContentID, aMetadata);
		this.attacheds=attacheds;
	}
	public List<WSS4JAttachment> getAttacheds() {
		return attacheds;
	}
	public void setAttacheds(List<WSS4JAttachment> attacheds) {
		this.attacheds = attacheds;
	}

}
