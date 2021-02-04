//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.03 a las 09:19:58 PM CET 
//


package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Un-coded Current address of the natural person.
 * 				<eidas:LocatorDesignator>22</eidas:LocatorDesignator>
 * 				<eidas:Thoroughfare>Arcacia Avenue</eidas:Thoroughfare>
 * 				<eidas:PostName>London</eidas:PostName>
 * 				<eidas:PostCode>SW1A 1AA</eidas:Postcode>
 * 			
 * 
 * <p>Clase Java para CurrentAddressStructuredType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CurrentAddressStructuredType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PoBox" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LocatorDesignator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LocatorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CvaddressArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Thoroughfare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PostName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AdminunitFirstline" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AdminunitSecondline" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PostCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrentAddressStructuredType", namespace = "http://eidas.europa.eu/attributes/naturalperson", propOrder = {
    "poBox",
    "locatorDesignator",
    "locatorName",
    "cvaddressArea",
    "thoroughfare",
    "postName",
    "adminunitFirstline",
    "adminunitSecondline",
    "postCode"
})
public class CurrentAddressStructuredType {

    @XmlElement(name = "PoBox")
    protected String poBox;
    @XmlElement(name = "LocatorDesignator")
    protected String locatorDesignator;
    @XmlElement(name = "LocatorName")
    protected String locatorName;
    @XmlElement(name = "CvaddressArea")
    protected String cvaddressArea;
    @XmlElement(name = "Thoroughfare")
    protected String thoroughfare;
    @XmlElement(name = "PostName")
    protected String postName;
    @XmlElement(name = "AdminunitFirstline")
    protected String adminunitFirstline;
    @XmlElement(name = "AdminunitSecondline")
    protected String adminunitSecondline;
    @XmlElement(name = "PostCode")
    protected String postCode;

    /**
     * Obtiene el valor de la propiedad poBox.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPoBox() {
        return poBox;
    }

    /**
     * Define el valor de la propiedad poBox.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPoBox(String value) {
        this.poBox = value;
    }

    /**
     * Obtiene el valor de la propiedad locatorDesignator.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocatorDesignator() {
        return locatorDesignator;
    }

    /**
     * Define el valor de la propiedad locatorDesignator.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocatorDesignator(String value) {
        this.locatorDesignator = value;
    }

    /**
     * Obtiene el valor de la propiedad locatorName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocatorName() {
        return locatorName;
    }

    /**
     * Define el valor de la propiedad locatorName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocatorName(String value) {
        this.locatorName = value;
    }

    /**
     * Obtiene el valor de la propiedad cvaddressArea.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCvaddressArea() {
        return cvaddressArea;
    }

    /**
     * Define el valor de la propiedad cvaddressArea.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCvaddressArea(String value) {
        this.cvaddressArea = value;
    }

    /**
     * Obtiene el valor de la propiedad thoroughfare.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThoroughfare() {
        return thoroughfare;
    }

    /**
     * Define el valor de la propiedad thoroughfare.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThoroughfare(String value) {
        this.thoroughfare = value;
    }

    /**
     * Obtiene el valor de la propiedad postName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostName() {
        return postName;
    }

    /**
     * Define el valor de la propiedad postName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostName(String value) {
        this.postName = value;
    }

    /**
     * Obtiene el valor de la propiedad adminunitFirstline.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdminunitFirstline() {
        return adminunitFirstline;
    }

    /**
     * Define el valor de la propiedad adminunitFirstline.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdminunitFirstline(String value) {
        this.adminunitFirstline = value;
    }

    /**
     * Obtiene el valor de la propiedad adminunitSecondline.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdminunitSecondline() {
        return adminunitSecondline;
    }

    /**
     * Define el valor de la propiedad adminunitSecondline.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdminunitSecondline(String value) {
        this.adminunitSecondline = value;
    }

    /**
     * Obtiene el valor de la propiedad postCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Define el valor de la propiedad postCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCode(String value) {
        this.postCode = value;
    }

}
