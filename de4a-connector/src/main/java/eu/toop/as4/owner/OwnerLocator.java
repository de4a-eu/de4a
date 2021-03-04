package eu.toop.as4.owner;

import java.lang.reflect.Constructor;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eu.de4a.conn.owner.OwnerGateway;
import eu.de4a.exception.MessageException;
import eu.de4a.model.EvidenceEntity;
import eu.de4a.repository.EvidenceEntityRepository; 

@Component
public class OwnerLocator {
	private static final Logger LOGGER = LoggerFactory.getLogger (OwnerLocator.class);
	@Autowired
	private ApplicationContext context; 
	@Autowired
	private EvidenceEntityRepository evidenceEntityRepository;	
	@Autowired
	private MessageSource messageSource;
	public EvidenceEntity lookupEvidence(String evidenceService) throws NoSuchMessageException, MessageException {
		EvidenceEntity evidence=evidenceEntityRepository.findById(evidenceService).orElse(null);
		if(evidence==null) {
			String[]arg= {evidenceService};
			throw new MessageException(messageSource.getMessage("error.transferor.evidence.not.exists", arg,LocaleContextHolder.getLocale()) ) ;
		}
		return evidence;
	}
	public OwnerGateway getOwnerGateway(EvidenceEntity evidence) throws ConfigurationException, NoSuchMessageException, MessageException { 
		String name=evidence.getOwnerGateway();	 
		LOGGER.debug("Located owner gateway {} for evidence {}",name,evidence.getIdEvidence());
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
