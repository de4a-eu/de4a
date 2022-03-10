package eu.de4a.connector.api.service.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import com.helger.dcng.api.me.model.MEMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageExchangeWrapper extends ContextRefreshedEvent {
    private static final long serialVersionUID = 1L;
    
    public MessageExchangeWrapper(ApplicationContext source) {        
        super(source);
        this.meMessage = null;
    }
    private transient MEMessage meMessage;
    
}
