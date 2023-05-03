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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import eu.de4a.connector.api.manager.MessageExchangeManager;
import eu.de4a.connector.api.service.model.MessageExchangeWrapper;

/**
 * Asynchronous listener for spring events. It manage requests
 * from an external monitored service
 *
 */
@Component
public class ContextEventsListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextEventsListener.class);

    @Autowired
    private MessageExchangeManager messageExchangeManager;

    public void onApplicationEvent(final ContextRefreshedEvent cse) {
        LOGGER.info("Processing event received: " + cse.getClass().getName());

        if(MessageExchangeWrapper.class.isAssignableFrom(cse.getClass())) {
                final MessageExchangeWrapper message = (MessageExchangeWrapper) cse;
                this.messageExchangeManager.processMessageExchange(message);
        } else {
                LOGGER.warn("Event received is not an instance of MessageExchangeWrapper, "
                        + "it won´t be processed. " + cse.getClass().getName());
        }
    }
}
