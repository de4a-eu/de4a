
package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RequestGroundsType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RequestGroundsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="LawELIPermanentLink" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}LawELIPermanentLinkType"/&gt;
 *         &lt;element name="ExplicitRequest" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}ExplicitRequestType"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestGroundsType", propOrder = {
    "lawELIPermanentLink",
    "explicitRequest"
})
public class RequestGroundsType {

    @XmlElement(name = "LawELIPermanentLink")
    @XmlSchemaType(name = "anyURI")
    protected String lawELIPermanentLink;
    @XmlElement(name = "ExplicitRequest")
    @XmlSchemaType(name = "token")
    protected ExplicitRequestType explicitRequest;

    /**
     * Obtiene el valor de la propiedad lawELIPermanentLink.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLawELIPermanentLink() {
        return lawELIPermanentLink;
    }

    /**
     * Define el valor de la propiedad lawELIPermanentLink.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLawELIPermanentLink(String value) {
        this.lawELIPermanentLink = value;
    }

    /**
     * Obtiene el valor de la propiedad explicitRequest.
     * 
     * @return
     *     possible object is
     *     {@link ExplicitRequestType }
     *     
     */
    public ExplicitRequestType getExplicitRequest() {
        return explicitRequest;
    }

    /**
     * Define el valor de la propiedad explicitRequest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExplicitRequestType }
     *     
     */
    public void setExplicitRequest(ExplicitRequestType value) {
        this.explicitRequest = value;
    }

}
