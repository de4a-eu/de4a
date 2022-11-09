package eu.de4a.connector.utils;

import java.util.Map;

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
  public String getParticipantAddress (final String participantId, final String endpointType, final boolean isRequest)
  {
    ELogMessage eLogMessage;
    if (isRequest)
      eLogMessage = ELogMessage.LOG_DT_PARTICIPANT_LOOKUP;
    else
      eLogMessage = ELogMessage.LOG_DR_PARTICIPANT_LOOKUP;

    KafkaClientWrapper.sendInfo (eLogMessage, participantId, endpointType);

    final Map <String, Map <String, String>> participants = isRequest ? addressesProperties.getDataOwners ()
                                                                      : addressesProperties.getDataEvaluators ();
    if (participants == null)
      throw new IllegalStateException ("Object was not properly initialized");

    // Bracket surrounding is needed for Spring to not ignore the keys colons in
    // the parsing
    final Map <String, String> participantAddress = participants.get (participantId);
    if (participantAddress == null || participantAddress.get (endpointType) == null)
    {
      if (isRequest)
        eLogMessage = ELogMessage.LOG_ERROR_DT_PARTICIPANT_LOOKUP;
      else
        eLogMessage = ELogMessage.LOG_ERROR_DR_PARTICIPANT_LOOKUP;
      KafkaClientWrapper.sendError (eLogMessage, participantId, endpointType);
      return null;
    }
    return participantAddress.get (endpointType);
  }
}
