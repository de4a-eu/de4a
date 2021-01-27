//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.01.25 a las 10:54:25 PM CET 
//


package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ServiceInputParameterTypeType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ServiceInputParameterTypeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Date"/&gt;
 *     &lt;enumeration value="InputeText"/&gt;
 *     &lt;enumeration value="YesNo"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ServiceInputParameterTypeType")
@XmlEnum
public enum ServiceInputParameterTypeType {

    @XmlEnumValue("Date")
    DATE("Date"),
    @XmlEnumValue("InputeText")
    INPUTE_TEXT("InputeText"),
    @XmlEnumValue("YesNo")
    YES_NO("YesNo");
    private final String value;

    ServiceInputParameterTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ServiceInputParameterTypeType fromValue(String v) {
        for (ServiceInputParameterTypeType c: ServiceInputParameterTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
