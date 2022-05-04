package eu.de4a.connector.dto;

import javax.annotation.Nonnull;
import org.w3c.dom.Document;
import com.helger.commons.annotation.Nonempty;

/**
 * AS4 message data representation for DE4AConnector
 *
 */
public class AS4MessageDTO {
    private final String senderID;
    private final String receiverID;
    private Document message;
    private String docTypeID;
    private String processID;
    private String contentID;

    public AS4MessageDTO (@Nonnull @Nonempty final String sSenderID, @Nonnull @Nonempty final String sReceiverID) {
      senderID = sSenderID;
      receiverID = sReceiverID;
    }

    @Nonnull @Nonempty
    public String getSenderID (){
      return senderID;
    }

    @Nonnull @Nonempty
    public String getReceiverID (){
      return receiverID;
    }

    public Document getMessage ()
    {
      return message;
    }

    public AS4MessageDTO withMessage(final Document message) {
        this.message = message;
        return this;
    }

    public String getDocTypeId (){
      return docTypeID;
    }

    public AS4MessageDTO withDocTypeID(final String docTypeID) {
        this.docTypeID = docTypeID;
        return this;
    }

    public String getProcessID (){
      return processID;
    }

    public AS4MessageDTO withProcessID(final String processID) {
        this.processID = processID;
        return this;
    }

    public String getContentID ()
    {
      return contentID;
    }

    public AS4MessageDTO withContentID(final String contentID) {
        this.contentID = contentID;
        return this;
    }
}