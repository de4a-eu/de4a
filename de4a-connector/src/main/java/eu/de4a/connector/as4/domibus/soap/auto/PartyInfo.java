
package eu.de4a.connector.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para PartyInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PartyInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="From" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}From"/&gt;
 *         &lt;element name="To" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}To" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyInfo", propOrder = {

})
public class PartyInfo {

    @XmlElement(name = "From", required = true)
    protected From from;
    @XmlElement(name = "To")
    protected To to;

    /**
     * Obtiene el valor de la propiedad from.
     * 
     * @return
     *     possible object is
     *     {@link From }
     *     
     */
    public From getFrom() {
        return from;
    }

    /**
     * Define el valor de la propiedad from.
     * 
     * @param value
     *     allowed object is
     *     {@link From }
     *     
     */
    public void setFrom(From value) {
        this.from = value;
    }

    /**
     * Obtiene el valor de la propiedad to.
     * 
     * @return
     *     possible object is
     *     {@link To }
     *     
     */
    public To getTo() {
        return to;
    }

    /**
     * Define el valor de la propiedad to.
     * 
     * @param value
     *     allowed object is
     *     {@link To }
     *     
     */
    public void setTo(To value) {
        this.to = value;
    }

}
