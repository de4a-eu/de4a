/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Austrian Federal Computing Center (BRZ)
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.de4a.connector.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import eu.de4a.connector.api.service.AS4SendingService;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.kafkaclient.model.ELogMessage;

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
                                          final DE4ACoreMarshaller <T> marshaller,
                                          final String requestId,
                                          final String... metadata)
  {
    if (metadata.length > 0)
    {
      KafkaClientWrapper.sendInfo (eLogMessage,
                                   requestObj.getClass ().getSimpleName (),
                                   requestId,
                                   messageDTO.getSenderID (),
                                   messageDTO.getReceiverID (),
                                   metadata[0]);

    }
    else
    {
      KafkaClientWrapper.sendInfo (eLogMessage,
                                   requestObj.getClass ().getSimpleName (),
                                   requestId,
                                   messageDTO.getSenderID (),
                                   messageDTO.getReceiverID ());
    }

    final Document doc = marshaller.getAsDocument (requestObj);

    // Sends AS4 message - it can trigger an error response via exceptions
    // advised
    this.as4Service.sendMessage (messageDTO.withMessage (doc));
  }
}
