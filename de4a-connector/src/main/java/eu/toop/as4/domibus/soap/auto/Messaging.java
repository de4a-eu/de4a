//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.04 a las 09:31:57 AM CET 
//


package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UserMessage" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}UserMessage" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ns1:mustUnderstand" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "userMessage"
})
@XmlRootElement(name = "Messaging")
public class Messaging {

    @XmlElement(name = "UserMessage")
    protected UserMessage userMessage;
    @XmlAttribute(name = "ns1:mustUnderstand")
    protected Boolean ns1MustUnderstand;

    /**
     * Obtiene el valor de la propiedad userMessage.
     * 
     * @return
     *     possible object is
     *     {@link UserMessage }
     *     
     */
    public UserMessage getUserMessage() {
        return userMessage;
    }

    /**
     * Define el valor de la propiedad userMessage.
     * 
     * @param value
     *     allowed object is
     *     {@link UserMessage }
     *     
     */
    public void setUserMessage(UserMessage value) {
        this.userMessage = value;
    }

    /**
     * Obtiene el valor de la propiedad ns1MustUnderstand.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNs1MustUnderstand() {
        return ns1MustUnderstand;
    }

    /**
     * Define el valor de la propiedad ns1MustUnderstand.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNs1MustUnderstand(Boolean value) {
        this.ns1MustUnderstand = value;
    }

}
