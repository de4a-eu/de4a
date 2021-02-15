package eu.toop.smp.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.de4a.conn.api.smp.NodeInfo;
import eu.toop.smp.db.Node;
import eu.toop.smp.db.NodeRepository;
@Controller 
public class SmpController {
	private static final Logger logger = LogManager.getLogger(SmpController.class);
	@Autowired
	private NodeRepository nodeRepository;
	
	@RequestMapping(method = RequestMethod.GET, value="/whois", headers="Accept=*/*",  produces = {  "application/xml" }   )
	public @ResponseBody NodeInfo getUserById(@RequestParam  String dataOwnerId,@RequestParam String serviceURI) 
	{ 
		Node node= nodeRepository.findById(dataOwnerId).orElse(null);
		if(node==null) {
			logger.debug("No se ha encontrado el nodo con id: "+dataOwnerId);
			NodeInfo n= new NodeInfo();
			n.setId(dataOwnerId);
			return n;
		}
		else {
			logger.debug("Localizado node: "+node);
			return  build(node);
		}
		 
	}
	private  NodeInfo build(Node node) { 
		NodeInfo ni=new NodeInfo();
		ni.setEndpoint(node.getEndpoint());
		ni.setId(node.getId());
		//ni.setX509(node.getX509());
		return ni;
	}
}
	 
