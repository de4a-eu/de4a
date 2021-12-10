package eu.de4a.connector.mem.phase4.servlet;


import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.http.HttpHeaderMap;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.mime.MimeTypeParser;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.phase4.CAS4;
import com.helger.phase4.attachment.AS4DecompressException;
import com.helger.phase4.attachment.WSS4JAttachment;
import com.helger.phase4.ebms3header.Ebms3Error;
import com.helger.phase4.ebms3header.Ebms3Property;
import com.helger.phase4.ebms3header.Ebms3SignalMessage;
import com.helger.phase4.ebms3header.Ebms3UserMessage;
import com.helger.phase4.error.EEbmsError;
import com.helger.phase4.messaging.IAS4IncomingMessageMetadata;
import com.helger.phase4.model.pmode.IPMode;
import com.helger.phase4.servlet.IAS4MessageState;
import com.helger.phase4.servlet.spi.AS4MessageProcessorResult;
import com.helger.phase4.servlet.spi.AS4SignalMessageProcessorResult;
import com.helger.phase4.servlet.spi.IAS4ServletMessageProcessorSPI;
import com.helger.xml.serialize.write.XMLWriter;

import eu.de4a.connector.api.manager.ApplicationContextProvider;
import eu.de4a.connector.as4.handler.IncomingAS4PKHandler;
import eu.toop.connector.api.TCConfig;
import eu.toop.connector.api.me.incoming.IMEIncomingHandler;
import eu.toop.connector.api.me.incoming.IncomingEDMErrorResponse;
import eu.toop.connector.api.me.incoming.IncomingEDMResponse;
import eu.toop.connector.api.me.incoming.MEIncomingTransportMetadata;
import eu.toop.connector.api.me.model.MEPayload;
import eu.toop.connector.mem.phase4.Phase4Config;
import eu.toop.edm.EDMErrorResponse;
import eu.toop.edm.EDMRequest;
import eu.toop.edm.EDMResponse;
import eu.toop.edm.IEDMTopLevelObject;
import eu.toop.edm.xml.EDMPayloadDeterminator;
import eu.toop.kafkaclient.ToopKafkaClient;

/**
 * TOOP specific implementation of {@link IAS4ServletMessageProcessorSPI}. It
 * takes incoming AS4 messages and forwards it accordingly to the correct TOOP
 * {@link eu.toop.connector.api.me.incoming.IMEIncomingHandler}.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public class AS4MessageProcessorSPICustom implements IAS4ServletMessageProcessorSPI
{
  private static final long serialVersionUID = 1L;
  public static final String ACTION_FAILURE = "Failure";
  private static final Logger LOGGER = LoggerFactory.getLogger (AS4MessageProcessorSPICustom.class);

  private static IMEIncomingHandler s_aIncomingHandler;
  private transient IncomingAS4PKHandler meHandler;
  public static void setIncomingHandler (@Nonnull final IMEIncomingHandler aIncomingHandler)
  {
    ValueEnforcer.notNull (aIncomingHandler, "IncomingHandler");
    ValueEnforcer.isNull (s_aIncomingHandler, "s_aIncomingHandler");
    s_aIncomingHandler = aIncomingHandler;

  }

  @Nullable
  private static IParticipantIdentifier _asPI (@Nullable final Ebms3Property aProp)
  {
    if (aProp == null)
      return null;

    final IIdentifierFactory aIF = TCConfig.getIdentifierFactory ();
    final String sType = aProp.getType ();
    final String sValue = aProp.getValue ();
    if (sType == null)
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("The particpant identifier is provided without a 'type' attribute: '" + sValue + "'");
      return aIF.parseParticipantIdentifier (sValue);
    }
    return aIF.createParticipantIdentifier (sType, sValue);
  }

  @Nonnull
  public AS4MessageProcessorResult processAS4UserMessage (@Nonnull final IAS4IncomingMessageMetadata aMessageMetadata,
                                                          @Nonnull final HttpHeaderMap aHttpHeaders,
                                                          @Nonnull final Ebms3UserMessage aUserMessage,
                                                          @Nonnull final IPMode aPMode,
                                                          @Nullable final Node aPayload,
                                                          @Nullable final ICommonsList <WSS4JAttachment> aIncomingAttachments,
                                                          @Nonnull final IAS4MessageState aState,
                                                          @Nonnull final ICommonsList <Ebms3Error> aProcessingErrors)
  {
	meHandler =ApplicationContextProvider.getApplicationContext().getBean(IncomingAS4PKHandler.class);
    if (Phase4Config.isDebugLogIncoming () && LOGGER.isInfoEnabled ())
    {
      LOGGER.info ("Received AS4 message:");
      LOGGER.info ("  UserMessage: " + aUserMessage);
      LOGGER.info ("  Payload: " + (aPayload == null ? "null" : XMLWriter.getNodeAsString (aPayload)));

      if (aIncomingAttachments != null)
      {
        LOGGER.info ("  Attachments: " + aIncomingAttachments.size ());
        for (final WSS4JAttachment x : aIncomingAttachments)
        {
          LOGGER.info ("    Attachment Content Type: " + x.getMimeType ());
          if (x.getMimeType ().startsWith ("text") || x.getMimeType ().endsWith ("/xml"))
          {
            try (final InputStream aIS = x.getSourceStream ())
            {
              LOGGER.info ("    Attachment Stream Class: " + aIS.getClass ().getName ());
              final String sContent = StreamHelper.getAllBytesAsString (x.getSourceStream (), x.getCharset ());
              LOGGER.info ("    Attachment Content: " + sContent.length () + " chars");
            }
            catch (final Exception ex)
            {
              LOGGER.warn ("    Attachment Content: CANNOT BE READ", ex);
            }
          }
        }
      }
    }

    if (aIncomingAttachments != null && aIncomingAttachments.isNotEmpty ())
    {
      // This is the ASIC
      final WSS4JAttachment aMainPayload = aIncomingAttachments.getFirst ();
      try
      {
        final IIdentifierFactory aIF = TCConfig.getIdentifierFactory ();
        final ICommonsList <Ebms3Property> aProps = new CommonsArrayList <> (aUserMessage.getMessageProperties ().getProperty ());
        final Ebms3Property aPropOS = aProps.findFirst (x -> x.getName ().equals (CAS4.ORIGINAL_SENDER));
        final Ebms3Property aPropFR = aProps.findFirst (x -> x.getName ().equals (CAS4.FINAL_RECIPIENT));

        final MEIncomingTransportMetadata aMetadata = new MEIncomingTransportMetadata (_asPI (aPropOS),
                                                                                       _asPI (aPropFR),
                                                                                       aIF.parseDocumentTypeIdentifier (aUserMessage.getCollaborationInfo ()
                                                                                                                                    .getAction ()),
                                                                                       aIF.createProcessIdentifier (aUserMessage.getCollaborationInfo ()
                                                                                                                                .getService ()
                                                                                                                                .getType (),
                                                                                                                    aUserMessage.getCollaborationInfo ()
                                                                                                                                .getService ()
                                                                                                                                .getValue ()));
        LOGGER.info ("Incoming Transport Metadata: {}", aMetadata);

        final String sTopLevelContentID = aMainPayload.getId ();

        final IEDMTopLevelObject aTopLevel = EDMPayloadDeterminator.parseAndFind (aMainPayload.getSourceStream ());
        if (aTopLevel instanceof EDMRequest)
        {
          // Request
        	meHandler.handleIncomingRequest (new EdmRequestWrapper ((EDMRequest) aTopLevel, sTopLevelContentID, aMetadata,aIncomingAttachments));
        }
        else
          if (aTopLevel instanceof EDMResponse)
          {
            // Response
            final ICommonsList <MEPayload> aAttachments = new CommonsArrayList <> ();
            for (final WSS4JAttachment aItem : aIncomingAttachments)
                aAttachments.add (MEPayload.builder ()
                                           .mimeType (MimeTypeParser.safeParseMimeType (aItem.getMimeType ()))
                                           .contentID (aItem.getId ())
                                           .data (StreamHelper.getAllBytes (aItem.getSourceStream ()))
                                           .build ());
            meHandler.handleIncomingResponse (new IncomingEDMResponse ((EDMResponse) aTopLevel,
                                                                                sTopLevelContentID,
                                                                                aAttachments,
                                                                                aMetadata));
          }
          else
            if (aTopLevel instanceof EDMErrorResponse)
            {
              // Error Response
            	meHandler.handleIncomingErrorResponse (new IncomingEDMErrorResponse ((EDMErrorResponse) aTopLevel,
                                                                                            sTopLevelContentID,
                                                                                            aMetadata));
            }
            else
              ToopKafkaClient.send (EErrorLevel.ERROR, () -> "Unsuspported Message: " + aTopLevel);
      }
      catch (final AS4DecompressException ex)
      {
        final String sErrorMsg = "Error decompressing a compressed attachment";
        aProcessingErrors.add (EEbmsError.EBMS_DECOMPRESSION_FAILURE.getAsEbms3Error (aState.getLocale (),
                                                                                      aState.getMessageID (),
                                                                                      sErrorMsg));
        ToopKafkaClient.send (EErrorLevel.ERROR, () -> "Error handling incoming AS4 message: " + sErrorMsg);
      }
      catch (final Exception ex)
      {
        ToopKafkaClient.send (EErrorLevel.ERROR, () -> "Error handling incoming AS4 message", ex);
      }
    }

    // To test returning with a failure works as intended
    if (aUserMessage.getCollaborationInfo ().getAction ().equals (ACTION_FAILURE))
    {
      return AS4MessageProcessorResult.createFailure (ACTION_FAILURE);
    }
    return AS4MessageProcessorResult.createSuccess ();
  }

  @Nonnull
  public AS4SignalMessageProcessorResult processAS4SignalMessage (@Nonnull final IAS4IncomingMessageMetadata aMessageMetadata,
                                                                  @Nonnull final HttpHeaderMap aHttpHeaders,
                                                                  @Nonnull final Ebms3SignalMessage aSignalMessage,
                                                                  @Nullable final IPMode aPmode,
                                                                  @Nonnull final IAS4MessageState aState,
                                                                  @Nonnull final ICommonsList <Ebms3Error> aProcessingErrors)
  {
    if (aSignalMessage.getReceipt () != null)
    {
      // Receipt - just acknowledge
      return AS4SignalMessageProcessorResult.createSuccess ();
    }

    if (!aSignalMessage.getError ().isEmpty ())
    {
      // Error - just acknowledge
      return AS4SignalMessageProcessorResult.createSuccess ();
    }

    return AS4SignalMessageProcessorResult.createSuccess ();
  }
}
