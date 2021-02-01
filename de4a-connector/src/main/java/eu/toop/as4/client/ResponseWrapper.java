package eu.toop.as4.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import eu.de4a.conn.api.requestor.ResponseTransferEvidence;

/**
 * Encapsulates the information from the AS4 response and its identifier
 * */
public class ResponseWrapper {
	private String id;
	private ResponseTransferEvidence canonicalEvidence;
	private List< MultipartFile> attacheds;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	 
	public ResponseTransferEvidence getCanonicalEvidence() {
		return canonicalEvidence;
	}
	public void setCanonicalEvidence(ResponseTransferEvidence canonicalEvidence) {
		this.canonicalEvidence = canonicalEvidence;
	}
	public List<org.springframework.web.multipart.MultipartFile> getAttacheds() {
		return attacheds;
	}
	public void setAttacheds(List<org.springframework.web.multipart.MultipartFile> attacheds) {
		this.attacheds = attacheds;
	}
	public void addAttached(MultipartFile file) {
		if(attacheds==null)attacheds=new ArrayList<MultipartFile>();
		attacheds.add(file);
	}
}
