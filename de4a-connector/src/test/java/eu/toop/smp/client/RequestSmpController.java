package eu.toop.smp.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.de4a.connector.model.smp.NodeInfo;
 
@Controller
@RequestMapping("/servicedata")
public class RequestSmpController {
	private static final Logger LOG =  LoggerFactory.getLogger (RequestSmpController.class);
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}", headers="Accept=*/*",  produces = {  "application/xml" }   )
	public @ResponseBody NodeInfo getUserById(@PathVariable String id) 
	{ 
		 return null;
		 
	}
	
}
	 