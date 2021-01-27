//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.01.27 a las 03:08:25 PM CET 
//


package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para errorCode.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="errorCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="EBMS_0001"/&gt;
 *     &lt;enumeration value="EBMS_0002"/&gt;
 *     &lt;enumeration value="EBMS_0003"/&gt;
 *     &lt;enumeration value="EBMS_0004"/&gt;
 *     &lt;enumeration value="EBMS_0005"/&gt;
 *     &lt;enumeration value="EBMS_0006"/&gt;
 *     &lt;enumeration value="EBMS_0007"/&gt;
 *     &lt;enumeration value="EBMS_0008"/&gt;
 *     &lt;enumeration value="EBMS_0009"/&gt;
 *     &lt;enumeration value="EBMS_0010"/&gt;
 *     &lt;enumeration value="EBMS_0011"/&gt;
 *     &lt;enumeration value="EBMS_0101"/&gt;
 *     &lt;enumeration value="EBMS_0102"/&gt;
 *     &lt;enumeration value="EBMS_0103"/&gt;
 *     &lt;enumeration value="EBMS_0201"/&gt;
 *     &lt;enumeration value="EBMS_0202"/&gt;
 *     &lt;enumeration value="EBMS_0301"/&gt;
 *     &lt;enumeration value="EBMS_0302"/&gt;
 *     &lt;enumeration value="EBMS_0303"/&gt;
 *     &lt;enumeration value="EBMS_0020"/&gt;
 *     &lt;enumeration value="EBMS_0021"/&gt;
 *     &lt;enumeration value="EBMS_0022"/&gt;
 *     &lt;enumeration value="EBMS_0023"/&gt;
 *     &lt;enumeration value="EBMS_0030"/&gt;
 *     &lt;enumeration value="EBMS_0031"/&gt;
 *     &lt;enumeration value="EBMS_0040"/&gt;
 *     &lt;enumeration value="EBMS_0041"/&gt;
 *     &lt;enumeration value="EBMS_0042"/&gt;
 *     &lt;enumeration value="EBMS_0043"/&gt;
 *     &lt;enumeration value="EBMS_0044"/&gt;
 *     &lt;enumeration value="EBMS_0045"/&gt;
 *     &lt;enumeration value="EBMS_0046"/&gt;
 *     &lt;enumeration value="EBMS_0047"/&gt;
 *     &lt;enumeration value="EBMS_0048"/&gt;
 *     &lt;enumeration value="EBMS_0049"/&gt;
 *     &lt;enumeration value="EBMS_0050"/&gt;
 *     &lt;enumeration value="EBMS_0051"/&gt;
 *     &lt;enumeration value="EBMS_0052"/&gt;
 *     &lt;enumeration value="EBMS_0053"/&gt;
 *     &lt;enumeration value="EBMS_0054"/&gt;
 *     &lt;enumeration value="EBMS_0055"/&gt;
 *     &lt;enumeration value="EBMS_0060"/&gt;
 *     &lt;enumeration value="EBMS_0065"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "errorCode", namespace = "http://org.ecodex.backend/1_1/")
@XmlEnum
public enum ErrorCode {

    EBMS_0001,
    EBMS_0002,
    EBMS_0003,
    EBMS_0004,
    EBMS_0005,
    EBMS_0006,
    EBMS_0007,
    EBMS_0008,
    EBMS_0009,
    EBMS_0010,
    EBMS_0011,
    EBMS_0101,
    EBMS_0102,
    EBMS_0103,
    EBMS_0201,
    EBMS_0202,
    EBMS_0301,
    EBMS_0302,
    EBMS_0303,
    EBMS_0020,
    EBMS_0021,
    EBMS_0022,
    EBMS_0023,
    EBMS_0030,
    EBMS_0031,
    EBMS_0040,
    EBMS_0041,
    EBMS_0042,
    EBMS_0043,
    EBMS_0044,
    EBMS_0045,
    EBMS_0046,
    EBMS_0047,
    EBMS_0048,
    EBMS_0049,
    EBMS_0050,
    EBMS_0051,
    EBMS_0052,
    EBMS_0053,
    EBMS_0054,
    EBMS_0055,
    EBMS_0060,
    EBMS_0065;

    public String value() {
        return name();
    }

    public static ErrorCode fromValue(String v) {
        return valueOf(v);
    }

}
