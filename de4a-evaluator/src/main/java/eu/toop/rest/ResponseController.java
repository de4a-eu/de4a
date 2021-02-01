package eu.toop.rest;


import java.net.URI;

import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import eu.toop.controller.GreetingController;
import eu.toop.controller.ResponseManager; 
@Controller 
@Scope( "session")
public class ResponseController {
	private static final Logger logger = LogManager.getLogger(ResponseController.class);
	@Autowired
	private ResponseManager responseManager;
	@Autowired
	private ServletContext context; 
	@Autowired
	private Client c;
	@Autowired
	private GreetingController greetingController;
	@PostMapping( value="/response", headers="Accept=*/*",  consumes = {  "application/xml" , "application/json"} , produces= {  "application/xml" } )
	public ResponseEntity<Void> sendRequest(@RequestBody  Document response) 
	{ 
		logger.debug("Evidence response received");
		HttpHeaders headers = new HttpHeaders(); 
		 
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}
	 
}
	   