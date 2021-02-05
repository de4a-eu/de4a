//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.04 a las 04:55:46 PM CET 
//


package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Clase Java para CollaborationInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CollaborationInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="AgreementRef" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}AgreementRef" minOccurs="0"/&gt;
 *         &lt;element name="Service" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Service"/&gt;
 *         &lt;element name="Action" type="{http://www.w3.org/2001/XMLSchema}token"/&gt;
 *         &lt;element name="ConversationId" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollaborationInfo", propOrder = {

})
public class CollaborationInfo {

    @XmlElement(name = "AgreementRef")
    protected AgreementRef agreementRef;
    @XmlElement(name = "Service", required = true)
    protected Service service;
    @XmlElement(name = "Action", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String action;
    @XmlElement(name = "ConversationId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String conversationId;

    /**
     * Obtiene el valor de la propiedad agreementRef.
     * 
     * @return
     *     possible object is
     *     {@link AgreementRef }
     *     
     */
    public AgreementRef getAgreementRef() {
        return agreementRef;
    }

    /**
     * Define el valor de la propiedad agreementRef.
     * 
     * @param value
     *     allowed object is
     *     {@link AgreementRef }
     *     
     */
    public void setAgreementRef(AgreementRef value) {
        this.agreementRef = value;
    }

    /**
     * Obtiene el valor de la propiedad service.
     * 
     * @return
     *     possible object is
     *     {@link Service }
     *     
     */
    public Service getService() {
        return service;
    }

    /**
     * Define el valor de la propiedad service.
     * 
     * @param value
     *     allowed object is
     *     {@link Service }
     *     
     */
    public void setService(Service value) {
        this.service = value;
    }

    /**
     * Obtiene el valor de la propiedad action.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Define el valor de la propiedad action.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Obtiene el valor de la propiedad conversationId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * Define el valor de la propiedad conversationId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConversationId(String value) {
        this.conversationId = value;
    }

}
