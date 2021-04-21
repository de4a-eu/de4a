package eu.de4a.connector.as4.client;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.mime.MimeTypeParser;
import com.helger.commons.mime.MimeTypeParserException;
import com.helger.commons.string.StringHelper;

import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.FileUtils;
import eu.toop.connector.api.TCIdentifierFactory;
import eu.toop.connector.api.me.EMEProtocol;
import eu.toop.connector.api.me.IMessageExchangeSPI;
import eu.toop.connector.api.me.MessageExchangeManager;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.model.MEMessage;
import eu.toop.connector.api.me.model.MEPayload;
import eu.toop.connector.api.me.outgoing.IMERoutingInformation;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.me.outgoing.MERoutingInformation;
import eu.toop.connector.api.rest.TCOutgoingMessage;
import eu.toop.connector.api.rest.TCOutgoingMetadata;
import eu.toop.connector.api.rest.TCPayload;
import eu.toop.connector.api.rest.TCRestJAXB;

@Component
public class Phase4GatewayClient implements As4GatewayInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(Phase4GatewayClient.class);
	static {
		org.apache.xml.security.Init.init();
	}

	public void sendMessage(String sender, NodeInfo receiver, String dataOwnerId, Element requestUsuario,
			List<TCPayload> payloads, boolean isRequest) throws MEOutgoingException {
		final TCOutgoingMessage aOM = new TCOutgoingMessage();
		{

			/***
			 * "AS4 Submit message: - partyIdAuthCredentials - service - action -
			 * conversationId - originalSender - finalRecipient - payload"
			 */
			final TCOutgoingMetadata aMetadata = new TCOutgoingMetadata();
			aMetadata.setSenderID(TCRestJAXB.createTCID(TCIdentifierFactory.PARTICIPANT_SCHEME, sender));
			aMetadata.setReceiverID(
					TCRestJAXB.createTCID(TCIdentifierFactory.PARTICIPANT_SCHEME, receiver.getParticipantIdentifier()));
			aMetadata.setDocTypeID(TCRestJAXB.createTCID(DE4AConstants.DOCTYPE_SCHEME, receiver.getDocumentIdentifier()));
			aMetadata.setProcessID(TCRestJAXB.createTCID(DE4AConstants.PROCESS_SCHEME, receiver.getProcessIdentifier()));
			aMetadata.setTransportProtocol(EMEProtocol.AS4.getTransportProfileID());
			aMetadata.setEndpointURL(receiver.getEndpointURI());
			aMetadata.setReceiverCertificate(receiver.getCertificate());
			aOM.setMetadata(aMetadata);
		}
		{
			final TCPayload aPayload = new TCPayload();
			try {
				aPayload.setValue(DOMUtils.documentToByte(requestUsuario.getOwnerDocument()));
			} catch (MessageException e) {
				//TODO error handler
				LOGGER.error("Error convert bytes from DOM");
			}
			aPayload.setMimeType(CMimeType.APPLICATION_XML.getAsString());
			aPayload.setContentID("message");
			aOM.addPayload(aPayload);
			if (payloads != null) {
				payloads.forEach(p -> {
					final TCPayload aPayloadTmp = new TCPayload();
					aPayloadTmp.setValue(p.getValue());
					aPayloadTmp.setMimeType(p.getMimeType());
					aPayloadTmp.setContentID(p.getContentID());
					aOM.addPayload(aPayloadTmp);
				});
			}
		}

		LOGGER.info(TCRestJAXB.outgoingMessage().getAsString(aOM));
		send(aOM);
	}

	private void send(TCOutgoingMessage aOutgoingMsg) throws MEOutgoingException {
		// These fields are optional in the XSD but required here
		if (StringHelper.hasNoText(aOutgoingMsg.getMetadata().getEndpointURL()))
			throw new MEOutgoingException(
					"The 'OutgoingMessage/Metadata/EndpointURL' element MUST be present and not empty");
		if (ArrayHelper.isEmpty(aOutgoingMsg.getMetadata().getReceiverCertificate()))
			throw new MEOutgoingException(
					"The 'OutgoingMessage/Metadata/ReceiverCertificate' element MUST be present and not empty");

		// Convert metadata
		final IMERoutingInformation aRoutingInfo;
		try {
			aRoutingInfo = MERoutingInformation.createFrom(aOutgoingMsg.getMetadata());
		} catch (final CertificateException ex) {
			throw new MEOutgoingException("Invalid routing information provided: " + ex.getMessage());
		}

		// Add payloads
		final MEMessage.Builder aMessage = MEMessage.builder();
		for (final TCPayload aPayload : aOutgoingMsg.getPayload()) {
			try {
				aMessage.addPayload(MEPayload.builder().mimeType(MimeTypeParser.parseMimeType(aPayload.getMimeType()))
						.contentID(StringHelper.getNotEmpty(aPayload.getContentID(), MEPayload.createRandomContentID()))
						.data(aPayload.getValue()));
			} catch (MimeTypeParserException e) {
				throw new MEOutgoingException("Invalid parsing MimeType: " + e.getMessage());
			}
		}
		IMessageExchangeSPI aMEM = MessageExchangeManager.getConfiguredImplementation();
		aMEM.sendOutgoing(aRoutingInfo, aMessage.build());
	}

	public ResponseWrapper processResponseAs4(IncomingEDMResponse responseas4) {
		LOGGER.debug("Processing AS4 response...");
		ResponseWrapper responsewrapper = new ResponseWrapper();
		responseas4.getAllAttachments().forEachValue(a -> {
			try {
				responsewrapper.addAttached(FileUtils.getMultipart(a.getContentID(), a.getMimeTypeString(), a.getData().bytes()));
			} catch (IOException e) {
				LOGGER.error("Error attaching files", e);
			}
			Document evidence = null;
			String requestId = null;
			try {
				evidence = DOMUtils.byteToDocument(a.getData().bytes());
				requestId = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, evidence.getDocumentElement());
			} catch (MessageException e1) {
				LOGGER.error("Error managing evidence dom", e1);
			}
			responsewrapper.setTagDataId(a.getContentID());
			responsewrapper.setId(requestId);
			responsewrapper.setResponseDocument(evidence);
		});
		return responsewrapper;
	}

}
