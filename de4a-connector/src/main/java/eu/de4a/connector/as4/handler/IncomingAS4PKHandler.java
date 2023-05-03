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
package eu.de4a.connector.as4.handler;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.helger.dcng.api.me.incoming.IMEIncomingHandler;
import com.helger.dcng.api.me.incoming.MEIncomingException;
import com.helger.dcng.api.me.model.MEMessage;

import eu.de4a.connector.api.service.model.MessageExchangeWrapper;

/**
 * This is the handler for incoming AS4 messages. It is registered on startup
 * and spreads the information.
 */
@Component
public class IncomingAS4PKHandler implements IMEIncomingHandler
{
  private static final Logger LOGGER = LoggerFactory.getLogger (IncomingAS4PKHandler.class);

  @Autowired
  private ApplicationContext context;
  @Autowired
  private MessageEventPublisher publisher;

  @Override
  public void handleIncomingRequest (@Nonnull final MEMessage aMessage) throws MEIncomingException
  {
    if (context == null)
      throw new MEIncomingException ("IncomingAS4PKHandler/ApplicationContext wasn't initialized properly");
    if (publisher == null)
      throw new MEIncomingException ("IncomingAS4PKHandler/MessageEventPublisher wasn't initialized properly");

    LOGGER.info ("[DR-DT] Start handling incoming AS4 message");

    //KafkaClientWrapper.sendInfo (ELogMessage.LOG_AS4_REQ_RECEIPT);

    final MessageExchangeWrapper messageWrapper = new MessageExchangeWrapper (context, aMessage);
    publisher.publishCustomEvent (messageWrapper);

    LOGGER.info ("[DR-DT] Finished handling incoming AS4 message");
  }
}
