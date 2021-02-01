//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.01.29 a las 09:39:05 PM CET 
//


package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Legal Entity eIDAS mandatory dataset and optional dataset
 * 
 * <p>Clase Java para LegalEntityIdentifierType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LegalEntityIdentifierType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="LegalEntityIdentifier" type="{http://eidas.europa.eu/attributes/legalperson}LegalPersonIdentifierType"/&gt;
 *         &lt;element name="LegalEntityName" type="{http://eidas.europa.eu/attributes/legalperson}LegalNameType"/&gt;
 *         &lt;element name="LegalAddress" type="{http://eidas.europa.eu/attributes/legalperson}LegalPersonAddressType" minOccurs="0"/&gt;
 *         &lt;element name="VATRegistration" type="{http://eidas.europa.eu/attributes/legalperson}VATRegistrationNumberType" minOccurs="0"/&gt;
 *         &lt;element name="TaxReference" type="{http://eidas.europa.eu/attributes/legalperson}TaxReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="D-2012-17-EUIdentifier" type="{http://eidas.europa.eu/attributes/legalperson}D-2012-17-EUIdentifierType" minOccurs="0"/&gt;
 *         &lt;element name="LEI" type="{http://eidas.europa.eu/attributes/legalperson}LEIType" minOccurs="0"/&gt;
 *         &lt;element name="EORI" type="{http://eidas.europa.eu/attributes/legalperson}EORIType" minOccurs="0"/&gt;
 *         &lt;element name="SEED" type="{http://eidas.europa.eu/attributes/legalperson}SEEDType" minOccurs="0"/&gt;
 *         &lt;element name="SIC" type="{http://eidas.europa.eu/attributes/legalperson}SICType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegalEntityIdentifierType", propOrder = {
    "legalEntityIdentifier",
    "legalEntityName",
    "legalAddress",
    "vatRegistration",
    "taxReference",
    "d201217EUIdentifier",
    "lei",
    "eori",
    "seed",
    "sic"
})
public class LegalEntityIdentifierType {

    @XmlElement(name = "LegalEntityIdentifier", required = true)
    protected String legalEntityIdentifier;
    @XmlElement(name = "LegalEntityName", required = true)
    protected LegalNameType legalEntityName;
    @XmlElement(name = "LegalAddress")
    protected String legalAddress;
    @XmlElement(name = "VATRegistration")
    protected String vatRegistration;
    @XmlElement(name = "TaxReference")
    protected String taxReference;
    @XmlElement(name = "D-2012-17-EUIdentifier")
    protected String d201217EUIdentifier;
    @XmlElement(name = "LEI")
    protected String lei;
    @XmlElement(name = "EORI")
    protected String eori;
    @XmlElement(name = "SEED")
    protected String seed;
    @XmlElement(name = "SIC")
    protected String sic;

    /**
     * Obtiene el valor de la propiedad legalEntityIdentifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLegalEntityIdentifier() {
        return legalEntityIdentifier;
    }

    /**
     * Define el valor de la propiedad legalEntityIdentifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLegalEntityIdentifier(String value) {
        this.legalEntityIdentifier = value;
    }

    /**
     * Obtiene el valor de la propiedad legalEntityName.
     * 
     * @return
     *     possible object is
     *     {@link LegalNameType }
     *     
     */
    public LegalNameType getLegalEntityName() {
        return legalEntityName;
    }

    /**
     * Define el valor de la propiedad legalEntityName.
     * 
     * @param value
     *     allowed object is
     *     {@link LegalNameType }
     *     
     */
    public void setLegalEntityName(LegalNameType value) {
        this.legalEntityName = value;
    }

    /**
     * Obtiene el valor de la propiedad legalAddress.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLegalAddress() {
        return legalAddress;
    }

    /**
     * Define el valor de la propiedad legalAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLegalAddress(String value) {
        this.legalAddress = value;
    }

    /**
     * Obtiene el valor de la propiedad vatRegistration.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVATRegistration() {
        return vatRegistration;
    }

    /**
     * Define el valor de la propiedad vatRegistration.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVATRegistration(String value) {
        this.vatRegistration = value;
    }

    /**
     * Obtiene el valor de la propiedad taxReference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxReference() {
        return taxReference;
    }

    /**
     * Define el valor de la propiedad taxReference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxReference(String value) {
        this.taxReference = value;
    }

    /**
     * Obtiene el valor de la propiedad d201217EUIdentifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getD201217EUIdentifier() {
        return d201217EUIdentifier;
    }

    /**
     * Define el valor de la propiedad d201217EUIdentifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setD201217EUIdentifier(String value) {
        this.d201217EUIdentifier = value;
    }

    /**
     * Obtiene el valor de la propiedad lei.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLEI() {
        return lei;
    }

    /**
     * Define el valor de la propiedad lei.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLEI(String value) {
        this.lei = value;
    }

    /**
     * Obtiene el valor de la propiedad eori.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEORI() {
        return eori;
    }

    /**
     * Define el valor de la propiedad eori.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEORI(String value) {
        this.eori = value;
    }

    /**
     * Obtiene el valor de la propiedad seed.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEED() {
        return seed;
    }

    /**
     * Define el valor de la propiedad seed.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEED(String value) {
        this.seed = value;
    }

    /**
     * Obtiene el valor de la propiedad sic.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSIC() {
        return sic;
    }

    /**
     * Define el valor de la propiedad sic.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSIC(String value) {
        this.sic = value;
    }

}
