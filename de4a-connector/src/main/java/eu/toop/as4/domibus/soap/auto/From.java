
package eu.toop.as4.domibus.soap.auto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para From complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="From"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="PartyId" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PartyId"/&gt;
 *         &lt;element name="Role" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}max255-non-empty-string"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "From", propOrder = {

})
public class From {

    @XmlElement(name = "PartyId", required = true)
    protected PartyId partyId;
    @XmlElement(name = "Role", required = true)
    protected String role;

    /**
     * Obtiene el valor de la propiedad partyId.
     * 
     * @return
     *     possible object is
     *     {@link PartyId }
     *     
     */
    public PartyId getPartyId() {
        return partyId;
    }

    /**
     * Define el valor de la propiedad partyId.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyId }
     *     
     */
    public void setPartyId(PartyId value) {
        this.partyId = value;
    }

    /**
     * Obtiene el valor de la propiedad role.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Define el valor de la propiedad role.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

}
