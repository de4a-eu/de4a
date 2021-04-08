package eu.de4a.connector.as4.owner;

import java.lang.reflect.Constructor;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import eu.de4a.connector.model.OwnerAddresses;
import eu.de4a.connector.repository.OwnerAddressesRepository;
import eu.de4a.exception.MessageException; 

@Component
public class OwnerLocator {
	private static final Logger LOGGER = LoggerFactory.getLogger (OwnerLocator.class);
	@Autowired
	private ApplicationContext context; 
	@Autowired
	private OwnerAddressesRepository evidenceEntityRepository;
	
	
	public OwnerAddresses lookupOwnerAddress(String dataOwnerId) throws NoSuchMessageException, MessageException {
		OwnerAddresses evidence=evidenceEntityRepository.findById(dataOwnerId).orElse(null);
		if(evidence==null) {
			throw new MessageException(String.format("Does not exists information for Owner id {}", dataOwnerId)) ;
		}
		return evidence;
	}
	/**
	 * @deprecated - previous solution to retrieve owner through evidenceType
	 * 
	 * @param evidence
	 * @return
	 * @throws ConfigurationException
	 * @throws NoSuchMessageException
	 * @throws MessageException
	 */
	@Deprecated(forRemoval = true)
	public OwnerGateway getOwnerGateway(OwnerAddresses evidence) throws ConfigurationException, NoSuchMessageException, MessageException { 
		String name="";//evidence.getOwnerGateway();	 
		LOGGER.debug("Located owner gateway {} for evidence {}",name,evidence.getAgentUrn());
		try {
			return (OwnerGateway) context.getBean(name);
		}catch(NoSuchBeanDefinitionException nbe) {
			LOGGER.warn("Bean {} not defined, try static instanceof",name);
		    try {
		    	Class<?> clazz = Class.forName(name);
		    	Constructor<?> ctor = clazz.getConstructor(String.class);
		    	return (OwnerGateway) ctor.newInstance(new Object[] { });

			}catch(Throwable e) {
			    	LOGGER.error("Error getting bean or class {}",name,e);
			    	throw new ConfigurationException("Wrong configuration of Owner�s Gateway:"+e.getMessage());
			}
		} 
	}
}
