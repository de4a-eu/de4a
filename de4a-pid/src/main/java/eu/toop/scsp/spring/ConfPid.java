package eu.toop.scsp.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order; 
@Configuration    
@PropertySource("classpath:application-pid.properties")
@ComponentScan("eu.de4a.scsp")   
@Order(Ordered.LOWEST_PRECEDENCE)
public class ConfPid    {   
}
