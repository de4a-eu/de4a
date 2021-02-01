//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.01 a las 02:37:52 PM CET 
//


package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Clase Java para AgreementRef complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="AgreementRef"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/&gt;max255-non-empty-string"&gt;
 *       &lt;attribute name="type" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}max255-non-empty-string" /&gt;
 *       &lt;attribute name="pmode" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}max255-non-empty-string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AgreementRef", propOrder = {
    "value"
})
public class AgreementRef {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "pmode")
    protected String pmode;

    /**
     * Obtiene el valor de la propiedad value.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Define el valor de la propiedad value.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor de la propiedad type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Define el valor de la propiedad type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtiene el valor de la propiedad pmode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPmode() {
        return pmode;
    }

    /**
     * Define el valor de la propiedad pmode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPmode(String value) {
        this.pmode = value;
    }

}
