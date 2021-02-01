//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.01 a las 02:37:33 PM CET 
//


package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para IssuingTypeType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="IssuingTypeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="OriginalIssuing"/&gt;
 *     &lt;enumeration value="MultinlingualFormIssuing"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "IssuingTypeType")
@XmlEnum
public enum IssuingTypeType {

    @XmlEnumValue("OriginalIssuing")
    ORIGINAL_ISSUING("OriginalIssuing"),
    @XmlEnumValue("MultinlingualFormIssuing")
    MULTINLINGUAL_FORM_ISSUING("MultinlingualFormIssuing");
    private final String value;

    IssuingTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IssuingTypeType fromValue(String v) {
        for (IssuingTypeType c: IssuingTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
