package eu.de4a.connector.as4.client;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.mime.MimeTypeParser;
import com.helger.commons.mime.MimeTypeParserException;
import com.helger.commons.string.StringHelper;

import eu.de4a.connector.as4.handler.Phase4MessageExchangeSPI;
import eu.de4a.connector.as4.owner.OwnerMessageEventPublisher;
import eu.de4a.connector.error.exceptions.ConnectorException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceException;
import eu.de4a.connector.error.exceptions.ResponseTransferEvidenceUSIDTException;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.FileUtils;
import eu.toop.connector.api.TCIdentifierFactory;
import eu.toop.connector.api.me.EMEProtocol;
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
	
	@Autowired
    private ApplicationContext context;
    @Autowired
    private OwnerMessageEventPublisher publisher;

	public void sendMessage(String sender, NodeInfo receiver, Element requestUsuario,
			List<TCPayload> payloads, String msgTag) throws MEOutgoingException {
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
			    String error = "Error on conversion of request payload";
				LOGGER.error(error);
				throw new MEOutgoingException(error);
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
		Phase4MessageExchangeSPI aMEM = new Phase4MessageExchangeSPI();
		aMEM.sendOutgoing(aRoutingInfo, aMessage.build());
	}

	public void processResponseAs4(IncomingEDMResponse responseas4) {		
		ConnectorException ex = new ConnectorException().withLayer(LayerError.INTERNAL_FAILURE)
            .withFamily(FamilyErrorType.CONVERSION_ERROR)
            .withModule(ExternalModuleError.CONNECTOR_DR);
		
		ResponseWrapper responsewrapper = new ResponseWrapper(context);
		responseas4.getAllAttachments().forEachValue(a -> {
			try {
				responsewrapper.addAttached(FileUtils.getMultipart(a.getContentID(), a.getMimeTypeString(), a.getData().bytes()));
			} catch (IOException e) {
				LOGGER.error("Error attaching files to response wrapper", e);
			}
			Document evidence = null;
			String requestId = "";
			try {
				evidence = DOMUtils.byteToDocument(a.getData().bytes());
				requestId = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID, evidence.getDocumentElement());
			} catch (MessageException e1) {
			    String errorMsg = "Error managing evidence DOM on AS4 response";
				LOGGER.error(errorMsg, e1);
			    if(DE4AConstants.TAG_EVIDENCE_RESPONSE.equals(a.getContentID())) {
			        throw (ResponseTransferEvidenceException) ex.withMessageArg(errorMsg);
                } else {
                    throw (ResponseTransferEvidenceUSIDTException) ex.withMessageArg(errorMsg);
                }
			}
			responsewrapper.setTagDataId(a.getContentID());
			responsewrapper.setId(requestId);
			responsewrapper.setResponseDocument(evidence);
		});
		publisher.publishCustomEvent(responsewrapper);
	}

}
