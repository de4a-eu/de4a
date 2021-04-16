package eu.de4a.conn.api.rest;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.http.HttpStatus;
/**
 * Modeling of exceptions due to non-compliance of the REST service API.
 *
 * */
@XmlRootElement(name="ApiError")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class ApiError {
	private HttpStatus status;
    private String message;
    private String code;
    public ApiError() {

    }
    public ApiError(HttpStatus status, String message, String code) {
        super();
        this.status = status;
        this.message = message;
        this.code = code;
    }


	@XmlElement(name = "status", required = true)
	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

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
