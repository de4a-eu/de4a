package eu.de4a.conn.api.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="node")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER) 
public class Ack {
	public static final String OK="OK";
	public static final String FAIL="FAIL";
	private String message;
	private String code;
	@XmlElement(name = "message", required = true)
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@XmlElement(name = "code", required = true)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
