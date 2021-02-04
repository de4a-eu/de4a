package eu.de4a.scsp.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.commons.CommonsMultipartResolver; 
@Configuration    
@PropertySource("classpath:application-pid.properties")
@ComponentScan("eu.de4a.scsp")   
@Order(Ordered.LOWEST_PRECEDENCE)
public class ConfPid    {   
	   @Bean 
	   public CommonsMultipartResolver createMultipartResolver() {
	       CommonsMultipartResolver resolver=new CommonsMultipartResolver();
	       resolver.setDefaultEncoding("UTF-8");
	       return resolver;
	   }
}
