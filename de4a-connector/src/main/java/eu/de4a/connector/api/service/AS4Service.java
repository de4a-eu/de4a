package eu.de4a.connector.api.service;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.mime.CMimeType;
import com.helger.dcng.api.me.EMEProtocol;
import com.helger.dcng.api.rest.DCNGPayload;
import com.helger.dcng.webapi.as4.ApiPostLookendAndSend;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import eu.de4a.connector.config.DE4AConstants;
import eu.de4a.connector.dto.AS4MessageDTO;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.connector.error.model.ELogMessages;
import eu.de4a.connector.utils.DOMUtils;
import eu.de4a.connector.utils.KafkaClientWrapper;

@Service
public class AS4Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(AS4Service.class);

    // Tags for Json returned by the AS4 API - Strong change dependence
    private static final String JSON_TAG_SUCCESS = "success";
    private static final String JSON_TAG_RESPONSE = "response";
    private static final String JSON_TAG_RESULT_LOOKUP = "lookup-results";
    private static final String JSON_TAG_RESULT_SEND = "sending-results";
    private static final String JSON_TAG_EXCEPTION = "exception";
    private static final String JSON_TAG_MESSAGE = "message";

    /**
     * Invoke the message exchange via API
     * {@link com.helger.dcng.webapi.as4.ApiPostLookendAndSend}
     *
     * @param messageDTO
     * @return if message is successfully sent
     */
    public boolean sendMessage(final AS4MessageDTO messageDTO) {
        final IParticipantIdentifier rPI = SimpleIdentifierFactory.INSTANCE
                .parseParticipantIdentifier(messageDTO.getReceiverID().toLowerCase(Locale.ROOT));
        final IParticipantIdentifier sPI = SimpleIdentifierFactory
                .INSTANCE.parseParticipantIdentifier(messageDTO.getSenderID().toLowerCase(Locale.ROOT));

        final IProcessIdentifier aProcessID = SimpleIdentifierFactory.INSTANCE
                .createProcessIdentifier(DE4AConstants.PROCESS_SCHEME, messageDTO.getProcessID());
        final IDocumentTypeIdentifier aDocumentTypeID = SimpleIdentifierFactory.INSTANCE
                .parseDocumentTypeIdentifier(messageDTO.getDocTypeId());

        final ICommonsList<DCNGPayload> aPayloads = new CommonsArrayList<>();
        final DCNGPayload a = new DCNGPayload();
        a.setValue(DOMUtils.documentToByte(messageDTO.getMessage()));
        a.setMimeType(CMimeType.APPLICATION_XML.getAsString());
        a.setContentID(messageDTO.getContentID());
        aPayloads.add(a);

        KafkaClientWrapper.sendInfo(ELogMessages.LOG_AS4_MSG_SENT, sPI.getURIEncoded(),
                sPI.getURIEncoded(), messageDTO.getContentID());

        final IJsonObject aJson = ApiPostLookendAndSend.perform(sPI, rPI, aDocumentTypeID, aProcessID,
                EMEProtocol.AS4.getTransportProfileID(), aPayloads);
        //Process json response
        manageAs4SendingResult(aJson);

        return true;
    }

    /**
     *
     * After the AS4 message exchange via API
     * {@link com.helger.dcng.webapi.as4.ApiPosLookendAndSend.java}
     * The results object is managed here
     *
     * @param aJson - Execution results in json format from the dcng-web-api
     */
    private void manageAs4SendingResult(final IJsonObject aJson) {
        LOGGER.debug("AS4 Sending result:\n {}",
                aJson.getAsJsonString (JsonWriterSettings.DEFAULT_SETTINGS_FORMATTED));

        // Base exception to be thrown
        final ConnectorException ex = new ConnectorException().withLayer(ELayerError.COMMUNICATIONS)
                .withFamily(EFamilyErrorType.AS4_ERROR_COMMUNICATION);

        if(!aJson.get(JSON_TAG_SUCCESS).getAsValue().getAsBoolean()) {
            //A problem occurs sending the AS4 message
            if(aJson.containsKey(JSON_TAG_EXCEPTION)) {
                final String message = aJson.get(JSON_TAG_EXCEPTION).getAsObject()
                        .get(JSON_TAG_MESSAGE).getAsValue().getAsString();
                throw ex.withModule(EExternalModuleError.AS4) .withMessageArg(message);
            }

            final IJsonObject lookupResults = (IJsonObject) aJson.get(JSON_TAG_RESULT_LOOKUP);
            final IJsonObject sendResults = (IJsonObject) aJson.get(JSON_TAG_RESULT_SEND);
            if(!lookupResults.get(JSON_TAG_SUCCESS).getAsValue().getAsBoolean()) {
                    final String smpErrMsg;
                    if(lookupResults.containsKey(JSON_TAG_RESPONSE))
                        smpErrMsg = lookupResults.get(JSON_TAG_RESPONSE)
                                .getAsValue().getAsString();
                    else
                        smpErrMsg = "Found no matching SMP service metadata";
                    throw ex.withModule(EExternalModuleError.SMP) .withMessageArg(smpErrMsg);
            }
            if(!sendResults.get(JSON_TAG_SUCCESS).getAsValue().getAsBoolean())
                    throw ex.withModule(EExternalModuleError.AS4)
                        .withMessageArg("Error with AS4 communications");
        }
    }
}
