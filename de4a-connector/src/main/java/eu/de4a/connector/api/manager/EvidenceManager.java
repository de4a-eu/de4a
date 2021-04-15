package eu.de4a.connector.api.manager;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;

import eu.de4a.connector.as4.client.As4GatewayInterface;

public class EvidenceManager {
	private static final Logger logger = LoggerFactory.getLogger (EvidenceManager.class);
	@Autowired
	private ApplicationContext context;
	protected As4GatewayInterface as4Client;
	@Value("${as4.gateway.implementation.bean}")
	private String nameAs4Gateway;
	@PostConstruct
	public void initme() {
		if(nameAs4Gateway==null) {
			String err="Context not initialized. Property 'as4.gateway.implementation.bean' not defined in application.properties";
			logger.error(err);
			throw new ApplicationContextException(err);
		}
		try {
			Object o=context.getBean(nameAs4Gateway);
			if(o instanceof As4GatewayInterface ==false) {
				String err=String.format("Context not initialized. Property 'as4.gateway.implementation.bean'=%s.It`s not a  As4GatewayInterface implementation class",nameAs4Gateway);
				logger.error(err);
				throw new ApplicationContextException(err);
			}
			as4Client=(As4GatewayInterface)o;
		}catch(NoSuchBeanDefinitionException ne) {
			String err=String.format("Context not initialized.Bean '%s' not defined",nameAs4Gateway);
			logger.error(err);
			throw new ApplicationContextException(err);
		}
		
	}
}
