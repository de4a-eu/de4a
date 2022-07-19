package eu.de4a.connector.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import eu.de4a.connector.api.service.AS4SendingService;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.model.ELogMessage;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.iem.core.DE4ACoreMarshaller;

@Component
public class APIManager
{
  @Autowired
  private AS4SendingService as4Service;

  /*
   * Common processing before sending the AS4 message. Here the AS4 service is
   * invoked to send the message. If this method returns, the message has been
   * sent
   */
  public <T> void processIncomingMessage (final ELogMessage eLogMessage,
		  								  final T requestObj,
                                          final AS4MessageDTO messageDTO,
                                          final String requestId,
                                          final String kafkaMsg,
                                          final DE4ACoreMarshaller <T> marshaller)
  {
    KafkaClientWrapper.sendInfo (eLogMessage,
                                 kafkaMsg,
                                 requestId,
                                 messageDTO.getDocTypeID (),
                                 messageDTO.getSenderID (),
                                 messageDTO.getReceiverID ());

    final Document doc = marshaller.getAsDocument (requestObj);

    // Sends AS4 message - it can trigger an error response via exceptions
    // advised
    this.as4Service.sendMessage (messageDTO.withMessage (doc));
  }
}
