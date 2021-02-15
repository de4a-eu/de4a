
package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ExplicitRequestType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ExplicitRequestType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="SDGR14"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ExplicitRequestType")
@XmlEnum
public enum ExplicitRequestType {

    @XmlEnumValue("SDGR14")
    SDGR_14("SDGR14");
    private final String value;

    ExplicitRequestType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExplicitRequestType fromValue(String v) {
        for (ExplicitRequestType c: ExplicitRequestType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
