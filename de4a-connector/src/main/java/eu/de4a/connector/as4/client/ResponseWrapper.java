package eu.de4a.connector.as4.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

/**
 * Encapsulates the information from the AS4 response and its identifier
 */
public class ResponseWrapper extends ContextRefreshedEvent {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private Document responseDocument;
    private String tagDataId;
    private List<MultipartFile> attacheds;

    public ResponseWrapper(ApplicationContext context) {
        super(context);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Document getResponseDocument() {
        return responseDocument;
    }

    public void setResponseDocument(Document responseDocument) {
        this.responseDocument = responseDocument;
    }

    public String getTagDataId() {
        return tagDataId;
    }

    public void setTagDataId(String tagDataId) {
        this.tagDataId = tagDataId;
    }

    public List<MultipartFile> getAttacheds() {
        return attacheds;
    }

    public void setAttacheds(List<MultipartFile> attacheds) {
        this.attacheds = attacheds;
    }

    public void addAttached(MultipartFile file) {
        if (attacheds == null)
            attacheds = new ArrayList<MultipartFile>();
        attacheds.add(file);
    }
}
