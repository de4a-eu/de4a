package eu.de4a.connector.api.service.model;

import javax.annotation.Nullable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import com.helger.dcng.api.me.model.MEMessage;

public class MessageExchangeWrapper extends ContextRefreshedEvent
{
  private transient MEMessage meMessage;

  public MessageExchangeWrapper (final ApplicationContext source, @Nullable final MEMessage aMessage)
  {
    super (source);
    meMessage = aMessage;
  }

  @Nullable
  public MEMessage getMeMessage ()
  {
    return meMessage;
  }
}
