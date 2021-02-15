
package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para messageStatus.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="messageStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="READY_TO_SEND"/&gt;
 *     &lt;enumeration value="READY_TO_PULL"/&gt;
 *     &lt;enumeration value="BEING_PULLED"/&gt;
 *     &lt;enumeration value="SEND_ENQUEUED"/&gt;
 *     &lt;enumeration value="SEND_IN_PROGRESS"/&gt;
 *     &lt;enumeration value="WAITING_FOR_RECEIPT"/&gt;
 *     &lt;enumeration value="ACKNOWLEDGED"/&gt;
 *     &lt;enumeration value="ACKNOWLEDGED_WITH_WARNING"/&gt;
 *     &lt;enumeration value="SEND_ATTEMPT_FAILED"/&gt;
 *     &lt;enumeration value="SEND_FAILURE"/&gt;
 *     &lt;enumeration value="NOT_FOUND"/&gt;
 *     &lt;enumeration value="WAITING_FOR_RETRY"/&gt;
 *     &lt;enumeration value="RECEIVED"/&gt;
 *     &lt;enumeration value="RECEIVED_WITH_WARNINGS"/&gt;
 *     &lt;enumeration value="DELETED"/&gt;
 *     &lt;enumeration value="DOWNLOADED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "messageStatus", namespace = "http://org.ecodex.backend/1_1/")
@XmlEnum
public enum MessageStatus {

    READY_TO_SEND,
    READY_TO_PULL,
    BEING_PULLED,
    SEND_ENQUEUED,
    SEND_IN_PROGRESS,
    WAITING_FOR_RECEIPT,
    ACKNOWLEDGED,
    ACKNOWLEDGED_WITH_WARNING,
    SEND_ATTEMPT_FAILED,
    SEND_FAILURE,
    NOT_FOUND,
    WAITING_FOR_RETRY,
    RECEIVED,
    RECEIVED_WITH_WARNINGS,
    DELETED,
    DOWNLOADED;

    public String value() {
        return name();
    }

    public static MessageStatus fromValue(String v) {
        return valueOf(v);
    }

}
