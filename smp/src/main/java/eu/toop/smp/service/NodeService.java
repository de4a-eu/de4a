package eu.toop.smp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.toop.smp.db.NodeRepository;

@Service  
public class NodeService {

	@Autowired  
	private NodeRepository nodeRepository;

	public NodeRepository getNodeRepository() {
		return nodeRepository;
	}

	public void setNodeRepository(NodeRepository nodeRepository) {
		this.nodeRepository = nodeRepository;
	} 
	
}
