
package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para errorResultImpl complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="errorResultImpl"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="errorCode" type="{http://org.ecodex.backend/1_1/}errorCode" minOccurs="0"/&gt;
 *         &lt;element name="errorDetail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="messageInErrorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mshRole" type="{http://org.ecodex.backend/1_1/}mshRole" minOccurs="0"/&gt;
 *         &lt;element name="notified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "errorResultImpl", namespace = "http://org.ecodex.backend/1_1/", propOrder = {
    "errorCode",
    "errorDetail",
    "messageInErrorId",
    "mshRole",
    "notified",
    "timestamp"
})
public class ErrorResultImpl {

    @XmlElement(namespace = "")
    @XmlSchemaType(name = "string")
    protected ErrorCode errorCode;
    @XmlElement(namespace = "")
    protected String errorDetail;
    @XmlElement(namespace = "")
    protected String messageInErrorId;
    @XmlElement(namespace = "")
    @XmlSchemaType(name = "string")
    protected MshRole mshRole;
    @XmlElement(namespace = "")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar notified;
    @XmlElement(namespace = "")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;

    /**
     * Obtiene el valor de la propiedad errorCode.
     * 
     * @return
     *     possible object is
     *     {@link ErrorCode }
     *     
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Define el valor de la propiedad errorCode.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorCode }
     *     
     */
    public void setErrorCode(ErrorCode value) {
        this.errorCode = value;
    }

    /**
     * Obtiene el valor de la propiedad errorDetail.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorDetail() {
        return errorDetail;
    }

    /**
     * Define el valor de la propiedad errorDetail.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorDetail(String value) {
        this.errorDetail = value;
    }

    /**
     * Obtiene el valor de la propiedad messageInErrorId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageInErrorId() {
        return messageInErrorId;
    }

    /**
     * Define el valor de la propiedad messageInErrorId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageInErrorId(String value) {
        this.messageInErrorId = value;
    }

    /**
     * Obtiene el valor de la propiedad mshRole.
     * 
     * @return
     *     possible object is
     *     {@link MshRole }
     *     
     */
    public MshRole getMshRole() {
        return mshRole;
    }

    /**
     * Define el valor de la propiedad mshRole.
     * 
     * @param value
     *     allowed object is
     *     {@link MshRole }
     *     
     */
    public void setMshRole(MshRole value) {
        this.mshRole = value;
    }

    /**
     * Obtiene el valor de la propiedad notified.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNotified() {
        return notified;
    }

    /**
     * Define el valor de la propiedad notified.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNotified(XMLGregorianCalendar value) {
        this.notified = value;
    }

    /**
     * Obtiene el valor de la propiedad timestamp.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Define el valor de la propiedad timestamp.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

}
