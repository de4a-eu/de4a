//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.04 a las 04:55:46 PM CET 
//


package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para UserMessage complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="UserMessage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="MessageInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}MessageInfo" minOccurs="0"/&gt;
 *         &lt;element name="PartyInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PartyInfo"/&gt;
 *         &lt;element name="CollaborationInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}CollaborationInfo"/&gt;
 *         &lt;element name="MessageProperties" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}MessageProperties" minOccurs="0"/&gt;
 *         &lt;element name="PayloadInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PayloadInfo" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *       &lt;attribute name="mpc" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserMessage", propOrder = {

})
public class UserMessage {

    @XmlElement(name = "MessageInfo")
    protected MessageInfo messageInfo;
    @XmlElement(name = "PartyInfo", required = true)
    protected PartyInfo partyInfo;
    @XmlElement(name = "CollaborationInfo", required = true)
    protected CollaborationInfo collaborationInfo;
    @XmlElement(name = "MessageProperties")
    protected MessageProperties messageProperties;
    @XmlElement(name = "PayloadInfo")
    protected PayloadInfo payloadInfo;
    @XmlAttribute(name = "mpc")
    @XmlSchemaType(name = "anyURI")
    protected String mpc;

    /**
     * Obtiene el valor de la propiedad messageInfo.
     * 
     * @return
     *     possible object is
     *     {@link MessageInfo }
     *     
     */
    public MessageInfo getMessageInfo() {
        return messageInfo;
    }

    /**
     * Define el valor de la propiedad messageInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageInfo }
     *     
     */
    public void setMessageInfo(MessageInfo value) {
        this.messageInfo = value;
    }

    /**
     * Obtiene el valor de la propiedad partyInfo.
     * 
     * @return
     *     possible object is
     *     {@link PartyInfo }
     *     
     */
    public PartyInfo getPartyInfo() {
        return partyInfo;
    }

    /**
     * Define el valor de la propiedad partyInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyInfo }
     *     
     */
    public void setPartyInfo(PartyInfo value) {
        this.partyInfo = value;
    }

    /**
     * Obtiene el valor de la propiedad collaborationInfo.
     * 
     * @return
     *     possible object is
     *     {@link CollaborationInfo }
     *     
     */
    public CollaborationInfo getCollaborationInfo() {
        return collaborationInfo;
    }

    /**
     * Define el valor de la propiedad collaborationInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link CollaborationInfo }
     *     
     */
    public void setCollaborationInfo(CollaborationInfo value) {
        this.collaborationInfo = value;
    }

    /**
     * Obtiene el valor de la propiedad messageProperties.
     * 
     * @return
     *     possible object is
     *     {@link MessageProperties }
     *     
     */
    public MessageProperties getMessageProperties() {
        return messageProperties;
    }

    /**
     * Define el valor de la propiedad messageProperties.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageProperties }
     *     
     */
    public void setMessageProperties(MessageProperties value) {
        this.messageProperties = value;
    }

    /**
     * Obtiene el valor de la propiedad payloadInfo.
     * 
     * @return
     *     possible object is
     *     {@link PayloadInfo }
     *     
     */
    public PayloadInfo getPayloadInfo() {
        return payloadInfo;
    }

    /**
     * Define el valor de la propiedad payloadInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link PayloadInfo }
     *     
     */
    public void setPayloadInfo(PayloadInfo value) {
        this.payloadInfo = value;
    }

    /**
     * Obtiene el valor de la propiedad mpc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMpc() {
        return mpc;
    }

    /**
     * Define el valor de la propiedad mpc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMpc(String value) {
        this.mpc = value;
    }

}
