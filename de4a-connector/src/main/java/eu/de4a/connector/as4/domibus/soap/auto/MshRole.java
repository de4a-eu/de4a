
package eu.de4a.connector.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para mshRole.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="mshRole"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SENDING"/&gt;
 *     &lt;enumeration value="RECEIVING"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "mshRole", namespace = "http://org.ecodex.backend/1_1/")
@XmlEnum
public enum MshRole {

    SENDING,
    RECEIVING;

    public String value() {
        return name();
    }

    public static MshRole fromValue(String v) {
        return valueOf(v);
    }

}
