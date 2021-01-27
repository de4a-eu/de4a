package eu.toop.as4.owner;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import eu.de4a.conn.owner.OwnerGateway; 

@Component
public class OwnerLocator {
	private static final Logger LOGGER = LoggerFactory.getLogger (OwnerLocator.class);
	@Autowired
	private ApplicationContext context;
	public OwnerGateway getOwnerGateway(String evidenceService) throws ConfigurationException {
		//TODO cambiar la forma de localizacion delpuntonde acceso de los owner
		Properties prop = new Properties();
		try {
		    //load a properties file from class path, inside static method
		    prop.load(OwnerLocator.class.getClassLoader().getResourceAsStream("owners.properties"));
		    String name=(String) prop.get(evidenceService);
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
			    	throw new ConfigurationException("Wrong configuration of Owner´s Gateway:"+e.getMessage());
			    }
		    }
		} 
		catch (IOException ex) {
			throw new ConfigurationException("Not Located owners.properties:"+ex.getMessage());
		}
	}
}
