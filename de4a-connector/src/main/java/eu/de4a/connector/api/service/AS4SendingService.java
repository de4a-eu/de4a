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

import java.util.Locale;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.helger.commons.mime.CMimeType;
import com.helger.dcng.api.DcngConfig;
import com.helger.dcng.api.DcngIdentifierFactory;
import com.helger.dcng.api.me.EMEProtocol;
import com.helger.dcng.api.rest.DCNGPayload;
import com.helger.dcng.webapi.as4.ApiPostLookupAndSendIt2;
import com.helger.dcng.webapi.as4.LookupAndSendingResult;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;

import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.connector.utils.DOMUtils;
import eu.de4a.kafkaclient.model.EExternalModule;

@Service
public class AS4SendingService
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AS4SendingService.class);

  /**
   * Invoke the message exchange via API
   * {@link com.helger.dcng.webapi.as4.ApiPostLookupAndSendIt2}
   *
   * @param messageDTO
   *        The message to be send
   */
  public void sendMessage (@Nonnull final AS4MessageDTO messageDTO)
  {
    final IIdentifierFactory aIF = DcngConfig.getIdentifierFactory ();
    final IParticipantIdentifier aSendingPI = aIF.parseParticipantIdentifier (messageDTO.getSenderID ().toLowerCase (Locale.ROOT));
    if (aSendingPI == null)
      throw new IllegalStateException ("Failed to parse sending Participant ID '" + messageDTO.getSenderID () + "'");

    final IParticipantIdentifier aReceiverPI = aIF.parseParticipantIdentifier (messageDTO.getReceiverID ().toLowerCase (Locale.ROOT));
    if (aReceiverPI == null)
      throw new IllegalStateException ("Failed to parse receiving Participant ID '" + messageDTO.getReceiverID () + "'");

    final IDocumentTypeIdentifier aDocumentTypeID = messageDTO.getDocTypeID ();
    if (aDocumentTypeID == null)
      throw new IllegalStateException ("Failed to parse Document Type ID '" + messageDTO.getDocTypeID () + "'");

    final IProcessIdentifier aProcessID = aIF.createProcessIdentifier (DcngIdentifierFactory.PROCESS_SCHEME, messageDTO.getProcessID ());
    if (aProcessID == null)
      throw new IllegalStateException ("Failed to parse Process ID '" + messageDTO.getProcessID () + "'");

    final DCNGPayload aPayload = new DCNGPayload ();
    aPayload.setValue (DOMUtils.documentToByte (messageDTO.getMessage ()));
    aPayload.setMimeType (CMimeType.APPLICATION_XML.getAsString ());

    /*
    KafkaClientWrapper.sendInfo (ELogMessage.LOG_AS4_MSG_SENT,
                                 aSendingPI.getURIEncoded (),
                                 aReceiverPI.getURIEncoded (),
                                 aDocumentTypeID.getURIEncoded (),
                                 aProcessID.getURIEncoded ());
                                 */

    // Perform SMP client lookup and send the AS4 message in one call
    final LookupAndSendingResult aResult = ApiPostLookupAndSendIt2.perform (aSendingPI,
                                                                            aReceiverPI,
                                                                            aDocumentTypeID,
                                                                            aProcessID,
                                                                            EMEProtocol.AS4.getTransportProfileID (),
                                                                            aPayload);

    // Process json response
    _manageAs4SendingResult (aResult);
  }

  /**
   * After the AS4 message exchange via API
   * {@link com.helger.dcng.webapi.as4.ApiPosLookendAndSend.java} The results
   * object is managed here
   *
   * @param aJson
   *        Execution results in format from the dcng-web-api
   */
  private static void _manageAs4SendingResult (@Nonnull final LookupAndSendingResult aResult)
  {
    if (aResult.isOverallSuccess ())
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("AS4 Sending result:\n " + aResult.getAsJson ().getAsJsonString (JsonWriterSettings.DEFAULT_SETTINGS_FORMATTED));
    }
    else
    {
        LOGGER.warn ("AS4 Sending result:\n " + aResult.getAsJson ().getAsJsonString (JsonWriterSettings.DEFAULT_SETTINGS_FORMATTED));

      // Base exception to be thrown
      final ConnectorException ex = new ConnectorException ().withLayer (ELayerError.COMMUNICATIONS)
                                                             .withFamily (EFamilyErrorType.AS4_ERROR_COMMUNICATION);

      // A problem occurs sending the AS4 message
      if (aResult.hasException ())
        throw ex.withModule (EExternalModule.AS4).withMessageArg (aResult.getException ().getMessage ());

      if (!aResult.isLookupSuccess ())
      {
        final String smpErrMsg;
        if (aResult.hasLookupServiceMetadata ())
          smpErrMsg = "Found the SMP Participant and Document Type, but failed to select based on Process and Transport Profile.";
        else
          smpErrMsg = "Found no matching SMP Participant and/or Document Type";

        //KafkaClientWrapper.sendError(EFamilyErrorType.AS4_ERROR_COMMUNICATION, EExternalModule.SMP, smpErrMsg);

        throw ex.withModule (EExternalModule.SMP).withMessageArg (smpErrMsg);
      }
      if (!aResult.isSendingSuccess ())
        throw ex.withModule (EExternalModule.AS4).withMessageArg ("Error with AS4 communications");
    }
  }
}
