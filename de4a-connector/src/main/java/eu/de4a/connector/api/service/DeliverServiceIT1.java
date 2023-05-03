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
package eu.de4a.connector.api.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.helger.commons.string.StringHelper;

import eu.de4a.connector.api.legacy.LegacyAPIHelper;
import eu.de4a.connector.api.service.model.EMessageServiceType;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.utils.APIRestUtils;
import eu.de4a.connector.utils.DOMUtils;
import eu.de4a.connector.utils.KafkaClientWrapper;
import eu.de4a.kafkaclient.model.ELogMessage;

@Service
public class DeliverServiceIT1
{
  private static final String XPATH_REQUEST_ID = "//*[local-name()='RequestId']/text()";
  private static final Logger LOGGER = LoggerFactory.getLogger (DeliverServiceIT1.class);

  private static String legacyDOURL = "";

  /**
   * Deliver a message to the corresponding participant based on the receiver
   * internal configuration resolved by
   * {@link eu.de4a.connector.config.AddressesProperties}
   *
   * @param eMessageServiceType
   *        Message service type
   * @param docMsg
   *        DOM Document with the message
   * @param senderID
   *        Sender participant identifier
   * @param receiverID
   *        Receiver participant identifier
   * @param logMessage
   *        Log tag for i18n
   * @param requestMetadata
   *        Optional logging metadata
   * @return ResponseEntity with the response of the external service
   */
  public ResponseEntity <byte []> pushMessage (@Nonnull final EMessageServiceType eMessageServiceType,
                                               @Nonnull final Document docMsg,
                                               @Nonnull final String senderID,
                                               @Nonnull final String receiverID,
                                               @Nonnull final ELogMessage logMessage,
                                               @Nullable final String requestMetadata)

  {
    // Generic way for all request IDs
    final String sRequestID = DOMUtils.getValueFromXpath (XPATH_REQUEST_ID, docMsg.getDocumentElement ());
    if (StringHelper.hasText (sRequestID))
      LegacyAPIHelper.rememberFinalized_DR (sRequestID, docMsg);

    // Get where has to be sent depending of the content
    final String url = legacyDOURL;
    LOGGER.info ("Legacy URL for DO: '" + url + "'");

    if (StringHelper.hasNoText (url))
      throw new IllegalStateException ("Failed to determine DE/DO URL for receiver '" +
                                       receiverID +
                                       "' and message type " +
                                       eMessageServiceType);

    KafkaClientWrapper.sendInfo (logMessage,
                                 eMessageServiceType.getElementLocalName (),
                                 sRequestID,
                                 senderID,
                                 receiverID,
                                 url,
                                 requestMetadata);

    // Send message
    return APIRestUtils.postRestObjectWithCatching (url,
                                                    DOMUtils.documentToByte (docMsg),
                                                    new ConnectorException ().withModule (logMessage.getModule ()));
  }

  @Nullable
  public static String getLegacyDOURL ()
  {
    return legacyDOURL;
  }

  public static void setLegacyDOURL (@Nullable final String legacyDOURL)
  {
    DeliverServiceIT1.legacyDOURL = legacyDOURL;
  }
}
