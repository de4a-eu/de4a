package eu.de4a.connector.dto;

import org.w3c.dom.Document;

import lombok.Data;
import lombok.NonNull;

/**
 * AS4 message data representation for DE4AConnector
 *
 */
@Data
public class AS4MessageDTO {
    
    @NonNull
    private String senderID;
    @NonNull
    private String receiverID;
    private Document message;
    private String docTypeId;
    private String processID;
    private String contentID;
    
    public AS4MessageDTO withMessage(Document message) {
        this.message = message;
        return this;
    }
    
    public AS4MessageDTO withDocTypeID(String docTypeID) {
        this.docTypeId = docTypeID;
        return this;
    }
    
    public AS4MessageDTO withProcessID(final String processID) {
        this.processID = processID;
        return this;        
    }
    
    public AS4MessageDTO withContentID(final String contentID) {
        this.contentID = contentID;
        return this;
    }
}