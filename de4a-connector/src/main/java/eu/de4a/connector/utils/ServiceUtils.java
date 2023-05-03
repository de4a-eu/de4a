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
package eu.de4a.connector.utils;

import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.de4a.connector.config.AddressesProperties;
import eu.de4a.kafkaclient.model.ELogMessage;

@Component
public class ServiceUtils
{

  @Autowired
  private AddressesProperties addressesProperties;

  private ServiceUtils ()
  {}

  public void reloadParticipantAddresses ()
  {
    addressesProperties.init ();
  }

  /**
   * Get the participant's endpoint
   *
   * @param participantId
   *        - Participant identifier URI encoded
   * @param endpointType
   *        - determines the data flow which receives the message
   * @param isRequest
   *        - if true Data Owners are selected, if not Data Evaluators
   * @return String - URL with the participant endpoint
   */
  @Nullable
  public String getParticipantAddress (final String participantId, final String endpointType, final boolean isRequest)
  {
    KafkaClientWrapper.sendInfo (isRequest ? ELogMessage.LOG_DT_PARTICIPANT_LOOKUP
                                           : ELogMessage.LOG_DR_PARTICIPANT_LOOKUP,
                                 participantId,
                                 endpointType);

    final Map <String, Map <String, String>> participants = isRequest ? addressesProperties.getDataOwners ()
                                                                      : addressesProperties.getDataEvaluators ();
    if (participants == null)
      throw new IllegalStateException ("Object was not properly initialized");

    // Bracket surrounding is needed for Spring to not ignore the keys colons in
    // the parsing
    final Map <String, String> participantAddress = participants.get (participantId);
    if (participantAddress == null || participantAddress.get (endpointType) == null)
    {
      KafkaClientWrapper.sendError (isRequest ? ELogMessage.LOG_ERROR_DT_PARTICIPANT_LOOKUP
                                              : ELogMessage.LOG_ERROR_DR_PARTICIPANT_LOOKUP,
                                    participantId,
                                    endpointType);
      return null;
    }
    return participantAddress.get (endpointType);
  }
}
