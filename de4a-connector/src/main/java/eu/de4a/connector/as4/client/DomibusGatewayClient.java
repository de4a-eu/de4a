package eu.de4a.connector.as4.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.mime.MimeTypeParser;
import com.helger.commons.mime.MimeTypeParserException;
import com.helger.commons.string.StringHelper;

import eu.de4a.connector.as4.domibus.soap.ClienteWS;
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
import eu.de4a.connector.as4.owner.OwnerMessageEventPublisher;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.connector.model.DomibusRequest;
import eu.de4a.connector.model.smp.NodeInfo;
import eu.de4a.connector.repository.DomibusRequestRepository;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.XDE4ACanonicalEvidenceType;
import eu.de4a.util.XDE4AMarshaller;
import eu.toop.connector.api.me.IMessageExchangeSPI;
import eu.toop.connector.api.me.MessageExchangeManager;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.model.MEMessage;
import eu.toop.connector.api.me.model.MEPayload;
import eu.toop.connector.api.me.outgoing.IMERoutingInformation;
import eu.toop.connector.api.me.outgoing.MEOutgoingException;
import eu.toop.connector.api.me.outgoing.MERoutingInformation;
import eu.toop.connector.api.rest.TCOutgoingMessage;
import eu.toop.connector.api.rest.TCPayload;

@Component
public class DomibusGatewayClient implements As4GatewayInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(DomibusGatewayClient.class);
	@Autowired
	private ClienteWS clienteWS;
	@Autowired
	private OwnerMessageEventPublisher publisher;
	@Autowired
	private DomibusRequestRepository domibusRequestRepository;
	@Value("${as4.gateway.implementation.bean}")
	private String nameAs4Gateway;

	@Scheduled(fixedRate = 1000)
	public void lookUpPendingMessage() {
		if (nameAs4Gateway.equalsIgnoreCase(DomibusGatewayClient.class.getSimpleName())) {
			// Prepared for future configuration of dynamic as4 in database. In future no
			// properties
			ListPendingMessagesResponse messagesPendingList = clienteWS.getPendindMessages();
			messagesPendingList.getMessageID().forEach(id -> {
				DomibusRequest req = domibusRequestRepository.findById(id).orElse(null);
				if (req == null) {
					req = new DomibusRequest();
					req.setIdrequest(id);
					domibusRequestRepository.save(req);
					ResponseAndHeader response = clienteWS.getMessageWithHeader(id);
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
									RequestWrapper request = new RequestWrapper();
									request.setRequest(doc.getDocumentElement());
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
										request.setId(idrequest);
										request.setEvidenceServiceUri(
												response.getInfo().getUserMessage().getCollaborationInfo().getAction());
										publisher.publishCustomEvent(request);
									}

								} catch (ParserConfigurationException e) {
									LOGGER.error("Response has not a valid content type", e);
								} catch (SAXException e) {
									LOGGER.error("Response has not a valid content type", e);
								} catch (IOException e) {
									LOGGER.error("Response has not a valid content type", e);
								}
							} catch (IOException e) {
								LOGGER.error("Error retrieving bytes from domibus response", e);
							}

						}

					} else {

						LOGGER.error("Error getting message from Domibus. ID=" + id);
					}
				}

			});
		}

	}

	public void sendMessage(String sender, NodeInfo receiver, String evidenceServiceUri, Element requestUsuario,
			List<TCPayload> payloads, boolean isRequest) throws MEOutgoingException {

		// String messageId="request00000001@de4a";
		List<PartInfo> attacheds = new ArrayList<PartInfo>();
		PartInfo partInfo = new PartInfo();
		PartInfo partInfo2 = new PartInfo();
		String idmessageattached = "cid:message";
		String idcanonical = "cid:"
				+ (isRequest ? DE4AConstants.TAG_EVIDENCE_REQUEST : DE4AConstants.TAG_EVIDENCE_RESPONSE);
		// String idmessageattached2="cid:EvidenceResponse";// esto de aqui
		// tocarlo..attacheds..para dependiendo de si peticion o respuesta ..
		partInfo.setHref(idmessageattached);
		partInfo2.setHref(idcanonical);
		Property prop = new Property();
		prop.setName("MimeType");
		prop.setValue("application/xml");
		PartProperties props = new PartProperties();
		props.getProperty().add(prop);
		partInfo.setPartProperties(props);
		partInfo2.setPartProperties(props);
		attacheds.add(partInfo);
		attacheds.add(partInfo2);
		// sender="red_gw";
		// String receiverid="9914:tc-ng-test-sender";
		String requestId = "";
		Messaging messageHeader = MessageFactory.makeMessage(sender, receiver.getParticipantIdentifier(), requestId,
				evidenceServiceUri, attacheds);
		List<LargePayloadType> bodies = new ArrayList<LargePayloadType>();
		LargePayloadType payload = new LargePayloadType();
		payload.setContentType("application/xml");
		payload.setPayloadId(idmessageattached);
		DataSource source = new ByteArrayDataSource(documentToByte(requestUsuario.getOwnerDocument()),
				"application/xml");
		LOGGER.error("!!!!!!!!!! FROM ------>"
				+ messageHeader.getUserMessage().getPartyInfo().getFrom().getPartyId().getValue());
		LOGGER.error("!!!!!!!!!! TO ------>"
				+ messageHeader.getUserMessage().getPartyInfo().getTo().getPartyId().getValue());
		LOGGER.error("!!!!!!!!!! ACTION ------>" + messageHeader.getUserMessage().getCollaborationInfo().getAction());
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
			LOGGER.error("boody  con id " + b.getPayloadId());
		});
		try {
			clienteWS.submitMessage(messageHeader, bodies);
		} catch (DomibusException e) {
			LOGGER.error("Error submitting as4 to Domibus gateway", e);
			throw new MEOutgoingException(e.getMessage(), e);
		}
	}

//  public   void sendMessage2(String sender,NodeInfo receiver,String requestId,Element requestUsuario) throws MEOutgoingException {
//    final TCOutgoingMessage aOM = new TCOutgoingMessage ();
//    {
//    	
//    	/***"AS4 Submit message:
//- partyIdAuthCredentials
//- service
//- action
//- conversationId
//- originalSender
//- finalRecipient
//- payload"
//*/
//      final TCOutgoingMetadata aMetadata = new TCOutgoingMetadata ();
//      aMetadata.setSenderID (TCRestJAXB.createTCID (TCIdentifierFactory.PARTICIPANT_SCHEME, sender ));
//      aMetadata.setReceiverID (TCRestJAXB.createTCID (TCIdentifierFactory.PARTICIPANT_SCHEME, receiver.getId()));
//      aMetadata.setDocTypeID (TCRestJAXB.createTCID (TCIdentifierFactory.DOCTYPE_SCHEME,    "TC1Leg1"));
//      aMetadata.setProcessID (TCRestJAXB.createTCID (TCIdentifierFactory.PROCESS_SCHEME, "bdx:noprocess"));
//      aMetadata.setTransportProtocol (EMEProtocol.AS4.getTransportProfileID ()); 
//      aMetadata.setEndpointURL (receiver.getEndpoint()); 
//      aMetadata.setReceiverCertificate (Base64.decodeBase64(  receiver.getX509()) );
//      aOM.setMetadata (aMetadata);
//    }
//    {
//      final TCPayload aPayload = new TCPayload (); 
//      aPayload.setValue (documentToByte(requestUsuario.getOwnerDocument())); 
//      aPayload.setMimeType (CMimeType.APPLICATION_XML.getAsString ());
//      aPayload.setContentID (requestId);
//      aOM.addPayload (aPayload);
//    }
//
//    LOGGER.info (TCRestJAXB.outgoingMessage ().getAsString (aOM));
//    send(aOM); 
//  }
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

	private byte[] documentToByte(Document document) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		org.apache.xml.security.utils.XMLUtils.outputDOM(document, baos, true);
		return baos.toByteArray();
	}

	public ResponseWrapper processResponseAs4(IncomingEDMResponse responseas4) {
		LOGGER.debug("Processing AS4 response...");
		ResponseWrapper responsewrapper = new ResponseWrapper();
		responseas4.getAllAttachments().forEachValue(a -> {
			responsewrapper.addAttached(getMultipart(a.getContentID(), a.getMimeTypeString(), a.getData().bytes()));
			Document evidence = null;
			String canonicalEvidenceId = null;
			try {
				evidence = DOMUtils.byteToDocument(a.getData().bytes());
				canonicalEvidenceId = DOMUtils.getValueFromXpath(DE4AConstants.XPATH_CANONICAL_EVICENCE_ID, evidence.getDocumentElement());
			} catch (MessageException e1) {
				LOGGER.error("Error managing evidence dom", e1);
			}
			if (a.getContentID().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)) {								
				ResponseTransferEvidenceType responseTransferEvidence = XDE4AMarshaller
						.drImResponseMarshaller(XDE4ACanonicalEvidenceType.getXDE4CanonicalEvidenceType(canonicalEvidenceId)).read(evidence);
				responsewrapper.setTagDataId(DE4AConstants.TAG_EVIDENCE_RESPONSE);
				responsewrapper.setId(responseTransferEvidence.getRequestId());
				responsewrapper.setResponseDocument(evidence);
			} else if (a.getContentID().equals(DE4AConstants.TAG_FORWARD_EVIDENCE_REQUEST)) {			
				RequestForwardEvidenceType requestForwardEvidence = XDE4AMarshaller
						.deUsiRequestMarshaller(XDE4ACanonicalEvidenceType.getXDE4CanonicalEvidenceType(canonicalEvidenceId)).read(evidence);
				responsewrapper.setTagDataId(DE4AConstants.TAG_FORWARD_EVIDENCE_REQUEST);
				responsewrapper.setId(requestForwardEvidence.getRequestId());
				responsewrapper.setResponseDocument(evidence);
			}
		});
		return responsewrapper;
	}

	private MultipartFile getMultipart(String label, String mimetype, byte[] data) {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("de4a-", null);

			DiskFileItem item = new org.apache.commons.fileupload.disk.DiskFileItem(label, mimetype, false, label, 1,
					tempFile.getParentFile());
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			OutputStream out = item.getOutputStream();
			IOUtils.copy(in, out);
			in.close();
			out.close();
			return new CommonsMultipartFile(item);
		} catch (IOException e1) {
			LOGGER.error("Error attaching files", e1);
		}
		return null;

	}
}
