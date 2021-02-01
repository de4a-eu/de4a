package eu.toop.as4.domibus.soap;

import eu.toop.as4.domibus.soap.auto.CollaborationInfo;
import eu.toop.as4.domibus.soap.auto.Messaging;
import eu.toop.as4.domibus.soap.auto.RetrieveMessageResponse;

public class ResponseAndHeader {
	private Messaging info;
	private RetrieveMessageResponse message;
	public ResponseAndHeader(Messaging info,RetrieveMessageResponse message) {
		this.info=info;
		this.message=message;
	}
	 
	public Messaging getInfo() {
		return info;
	}

	public void setInfo(Messaging info) {
		this.info = info;
	}

	public RetrieveMessageResponse getMessage() {
		return message;
	}
	public void setMessage(RetrieveMessageResponse message) {
		this.message = message;
	}
	
}
