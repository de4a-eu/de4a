package eu.de4a.connector.api.service.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;

/**
 * Enum for controlling correspondences between message types (root XML element name) 
 * and DE/DO addresses properties {@link application.yml}. If something changes 
 * on {@link eu.de4a.iem.core} it forces to update this class and keep it up to date
 *
 */
@Getter
public enum EMessageServiceTypes {
    
    IM(eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceIM_QNAME.getLocalPart(), true),
    USI(eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceUSI_QNAME.getLocalPart(), true),
    LU(eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceLU_QNAME.getLocalPart(), true),
    SN(eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestEventSubscription_QNAME.getLocalPart(), true),
    RESPONSE(eu.de4a.iem.core.jaxb.dt.ObjectFactory._ResponseTransferEvidence_QNAME.getLocalPart(), false),
    SUBSCRIPTION_RESP(eu.de4a.iem.core.jaxb.dt.ObjectFactory._ResponseEventSubscription_QNAME.getLocalPart(), false),
    REDIRECT(eu.de4a.iem.core.jaxb.dt.ObjectFactory._USIRedirectUser_QNAME.getLocalPart(), false),
    NOTIFICATION(eu.de4a.iem.core.jaxb.dt.ObjectFactory._EventNotification_QNAME.getLocalPart(), false);
    
    
    private static final Map<String, EMessageServiceTypes> lookup = new HashMap<>();
    static {
        for(EMessageServiceTypes item : EMessageServiceTypes.values()) {
            lookup.put(item.getType(), item);
        }
    }
    
    private String type;
    private boolean isRequest;
    
    EMessageServiceTypes(final String type, final boolean isRequest) {
        this.type = type;
        this.isRequest = isRequest; 
    }
    
    public static EMessageServiceTypes getByType(String type) {
        return lookup.get(type);
    }
    
    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ROOT);
    }
}
