package eu.de4a.connector.as4.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eu.de4a.connector.api.manager.EvidenceTransferorManager;
import eu.de4a.connector.as4.domibus.soap.DomibusClientWS;
import eu.de4a.connector.as4.domibus.soap.DomibusException;
import eu.de4a.connector.as4.domibus.soap.MessageFactory;
import eu.de4a.connector.as4.domibus.soap.ResponseAndHeader;
import eu.de4a.connector.as4.domibus.soap.auto.LargePayloadType;
import eu.de4a.connector.as4.domibus.soap.auto.ListPendingMessagesResponse;
import eu.de4a.connector.as4.domibus.soap.auto.Messaging;
import eu.de4a.connector.as4.domibus.soap.auto.PartInfo;
import eu.de4a.connector.as4.domibus.soap.auto.PartProperties;
import eu.de4a.connector.as4.domibus.soap.auto.Property;
import eu.de4a.connector.as4.domibus.soap.auto.RetrieveMessageResponse;
import eu.de4a.connector.as4.owner.MessageOwner;
import eu.de4a.connector.model.DomibusRequest;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.repository.DomibusRequestRepository;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.FileUtils;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.rest.TCPayload;

@Component
public class DomibusGatewayClient implements As4GatewayInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(DomibusGatewayClient.class);
	@Autowired
	private DomibusClientWS domibusClientWS;
	@Autowired
	private DomibusRequestRepository domibusRequestRepository;
	@Autowired
	private EvidenceTransferorManager evidenceTransferorManager;

	@Value("${as4.gateway.implementation.bean}")
	private String nameAs4Gateway;
	@Value("#{'${domibus.endpoint.jvm:${domibus.endpoint:}}'}")
	private String domibusEndpoint;

	@Scheduled(fixedRate = 1000)
	public void lookUpPendingMessage() {
		if (nameAs4Gateway.equalsIgnoreCase(DomibusGatewayClient.class.getSimpleName())) {
			// Prepared for future configuration of dynamic as4 in database. In future no
			// properties
			ListPendingMessagesResponse messagesPendingList = domibusClientWS.getPendindMessages(domibusEndpoint);
			messagesPendingList.getMessageID().forEach(id -> {
				DomibusRequest req = domibusRequestRepository.findById(id).orElse(null);
				if (req == null) {
					req = new DomibusRequest();
					req.setIdrequest(id);
					domibusRequestRepository.save(req);
					ResponseAndHeader response = domibusClientWS.getMessageWithHeader(id, domibusEndpoint);
					LargePayloadType payload = null;
					if (response != null && response.getInfo() != null) {
						RetrieveMessageResponse message = response.getMessage();
						byte[] targetArray = null;
						payload = message.getPayload().stream()
								.filter(p -> p.getPayloadId().contains(DE4AConstants.TAG_EVIDENCE_REQUEST)).findFirst()
								.orElse(null);
						if (payload == null) {
							LOGGER.error("EvidenceRequest not found!!!!");
						} else {
							try {
								targetArray = IOUtils.toByteArray(payload.getValue().getDataSource().getInputStream());
								if (targetArray != null && Base64.isBase64(targetArray)) {
									targetArray = Base64.decodeBase64(targetArray);
								}
								DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
								factory.setNamespaceAware(true);
								DocumentBuilder builder;
								try {
									builder = factory.newDocumentBuilder();
									Document doc = builder.parse(new ByteArrayInputStream(targetArray));
									MessageOwner messageOwner = new MessageOwner();
									messageOwner.setMessage(doc.getDocumentElement());
									String idrequest = null;
									try {
										idrequest = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_ID,
												doc.getDocumentElement());
									} catch (MessageException e) {
										LOGGER.error("Error getting id from EvidenceRequest", e);
									}
									if (idrequest == null) {
										LOGGER.error("EvidenceRequest without id");
										LOGGER.error(DOMUtils.documentToString(doc));
									} else {
									    messageOwner.setId(idrequest);
									    messageOwner.setReceiverId(
												response.getInfo().getUserMessage().getCollaborationInfo().getAction());
										evidenceTransferorManager.queueMessage(messageOwner);
									}

								} catch (SAXException | IOException | ParserConfigurationException e) {
									LOGGER.error("Response has not a valid content type", e);
								}
							} catch (IOException e) {
								LOGGER.error("Error retrieving bytes from domibus response", e);
							}

						}

					} else {

						LOGGER.error("Error getting message from Domibus. ID = {}", id);
					}
				}

			});
		}

	}

	@Override
	public void sendMessage(String sender, NodeInfo receiver, String evidenceServiceUri, Element requestUsuario,
			List<TCPayload> payloads, boolean isRequest) throws MEOutgoingException {

		List<PartInfo> attacheds = new ArrayList<>();
		PartInfo partInfo = new PartInfo();
		PartInfo partInfo2 = new PartInfo();
		String idmessageattached = "cid:message";
		String idcanonical = "cid:"
				+ (isRequest ? DE4AConstants.TAG_EVIDENCE_REQUEST : DE4AConstants.TAG_EVIDENCE_RESPONSE);
		partInfo.setHref(idmessageattached);
		partInfo2.setHref(idcanonical);
		Property prop = new Property();
		prop.setName("MimeType");
		prop.setValue(MediaType.APPLICATION_XML_VALUE);
		PartProperties props = new PartProperties();
		props.getProperty().add(prop);
		partInfo.setPartProperties(props);
		partInfo2.setPartProperties(props);
		attacheds.add(partInfo);
		attacheds.add(partInfo2);
		String requestId = "";
		Messaging messageHeader = MessageFactory.makeMessage(sender, receiver.getParticipantIdentifier(), requestId,
				evidenceServiceUri, attacheds);
		List<LargePayloadType> bodies = new ArrayList<>();
		LargePayloadType payload = new LargePayloadType();
		payload.setContentType(MediaType.APPLICATION_XML_VALUE);
		payload.setPayloadId(idmessageattached);
		DataSource source = null;
		try {
			source = new ByteArrayDataSource(DOMUtils.documentToByte(requestUsuario.getOwnerDocument()),
					MediaType.APPLICATION_XML_VALUE);
		} catch (MessageException e) {
			LOGGER.error("Error transform document to byte", e);
		}
		LOGGER.debug("!!!!!!!!!! FROM ------> {}",
				messageHeader.getUserMessage().getPartyInfo().getFrom().getPartyId().getValue());
		LOGGER.debug("!!!!!!!!!! TO ------> {}",
				messageHeader.getUserMessage().getPartyInfo().getTo().getPartyId().getValue());
		LOGGER.debug("!!!!!!!!!! ACTION ------> {}", messageHeader.getUserMessage().getCollaborationInfo().getAction());
		payload.setValue(new DataHandler(source));
		bodies.add(payload);
		if (payloads != null) {
			payloads.forEach(p -> {
				LargePayloadType payloadtmp = new LargePayloadType();
				payloadtmp.setContentType(p.getMimeType());
				String cid = p.getContentID().startsWith("cid:") ? p.getContentID() : "cid:" + p.getContentID();
				payloadtmp.setPayloadId(cid);
				DataSource sourcetmp = new ByteArrayDataSource(p.getValue(), p.getMimeType());
				payloadtmp.setValue(new DataHandler(sourcetmp));
				bodies.add(payloadtmp);
			});
		}
		bodies.stream().forEach(b -> {
			LOGGER.error("body id {}", b.getPayloadId());
		});
		try {
			domibusClientWS.submitMessage(messageHeader, bodies, domibusEndpoint);
		} catch (DomibusException e) {
			LOGGER.error("Error submitting as4 to Domibus gateway", e);
			throw new MEOutgoingException(e.getMessage(), e);
		}
	}

	@Override
	public ResponseWrapper processResponseAs4(IncomingEDMResponse responseas4) {
		LOGGER.debug("Processing AS4 response...");
		ResponseWrapper responsewrapper = new ResponseWrapper();
		responseas4.getAllAttachments().forEachValue(a -> {
			try {
				responsewrapper.addAttached(
						FileUtils.getMultipart(a.getContentID(), a.getMimeTypeString(), a.getData().bytes()));
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
