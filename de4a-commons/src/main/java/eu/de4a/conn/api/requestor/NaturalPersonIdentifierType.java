//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.01.25 a las 10:54:25 PM CET 
//


package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * 
 * 				Natural Person eIDAS mandatory dataset and optional dataset
 * 
 * <p>Clase Java para NaturalPersonIdentifierType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="NaturalPersonIdentifierType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Identifier" type="{http://eidas.europa.eu/attributes/naturalperson}PersonIdentifierType"/&gt;
 *         &lt;element name="GivenName" type="{http://eidas.europa.eu/attributes/naturalperson}CurrentGivenNameType"/&gt;
 *         &lt;element name="FamilyName" type="{http://eidas.europa.eu/attributes/naturalperson}CurrentFamilyNameType"/&gt;
 *         &lt;element name="DateOfBirth" type="{http://eidas.europa.eu/attributes/naturalperson}DateOfBirthType"/&gt;
 *         &lt;element name="Gender" type="{http://eidas.europa.eu/attributes/naturalperson}GenderType" minOccurs="0"/&gt;
 *         &lt;element name="BirthName" type="{http://eidas.europa.eu/attributes/naturalperson}BirthNameType" minOccurs="0"/&gt;
 *         &lt;element name="PlaceOfBirth" type="{http://eidas.europa.eu/attributes/naturalperson}PlaceOfBirthType" minOccurs="0"/&gt;
 *         &lt;element name="CurrentAddress" type="{http://eidas.europa.eu/attributes/naturalperson}CurrentAddressType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NaturalPersonIdentifierType", propOrder = {
    "identifier",
    "givenName",
    "familyName",
    "dateOfBirth",
    "gender",
    "birthName",
    "placeOfBirth",
    "currentAddress"
})
public class NaturalPersonIdentifierType {

    @XmlElement(name = "Identifier", required = true)
    protected String identifier;
    @XmlElement(name = "GivenName", required = true)
    protected CurrentGivenNameType givenName;
    @XmlElement(name = "FamilyName", required = true)
    protected CurrentFamilyNameType familyName;
    @XmlElement(name = "DateOfBirth", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlElement(name = "Gender")
    @XmlSchemaType(name = "string")
    protected GenderType gender;
    @XmlElement(name = "BirthName")
    protected BirthNameType birthName;
    @XmlElement(name = "PlaceOfBirth")
    protected PlaceOfBirthType placeOfBirth;
    @XmlElement(name = "CurrentAddress")
    protected String currentAddress;

    /**
     * Obtiene el valor de la propiedad identifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Define el valor de la propiedad identifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Obtiene el valor de la propiedad givenName.
     * 
     * @return
     *     possible object is
     *     {@link CurrentGivenNameType }
     *     
     */
    public CurrentGivenNameType getGivenName() {
        return givenName;
    }

    /**
     * Define el valor de la propiedad givenName.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrentGivenNameType }
     *     
     */
    public void setGivenName(CurrentGivenNameType value) {
        this.givenName = value;
    }

    /**
     * Obtiene el valor de la propiedad familyName.
     * 
     * @return
     *     possible object is
     *     {@link CurrentFamilyNameType }
     *     
     */
    public CurrentFamilyNameType getFamilyName() {
        return familyName;
    }

    /**
     * Define el valor de la propiedad familyName.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrentFamilyNameType }
     *     
     */
    public void setFamilyName(CurrentFamilyNameType value) {
        this.familyName = value;
    }

    /**
     * Obtiene el valor de la propiedad dateOfBirth.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Define el valor de la propiedad dateOfBirth.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Obtiene el valor de la propiedad gender.
     * 
     * @return
     *     possible object is
     *     {@link GenderType }
     *     
     */
    public GenderType getGender() {
        return gender;
    }

    /**
     * Define el valor de la propiedad gender.
     * 
     * @param value
     *     allowed object is
     *     {@link GenderType }
     *     
     */
    public void setGender(GenderType value) {
        this.gender = value;
    }

    /**
     * Obtiene el valor de la propiedad birthName.
     * 
     * @return
     *     possible object is
     *     {@link BirthNameType }
     *     
     */
    public BirthNameType getBirthName() {
        return birthName;
    }

    /**
     * Define el valor de la propiedad birthName.
     * 
     * @param value
     *     allowed object is
     *     {@link BirthNameType }
     *     
     */
    public void setBirthName(BirthNameType value) {
        this.birthName = value;
    }

    /**
     * Obtiene el valor de la propiedad placeOfBirth.
     * 
     * @return
     *     possible object is
     *     {@link PlaceOfBirthType }
     *     
     */
    public PlaceOfBirthType getPlaceOfBirth() {
        return placeOfBirth;
    }

    /**
     * Define el valor de la propiedad placeOfBirth.
     * 
     * @param value
     *     allowed object is
     *     {@link PlaceOfBirthType }
     *     
     */
    public void setPlaceOfBirth(PlaceOfBirthType value) {
        this.placeOfBirth = value;
    }

    /**
     * Obtiene el valor de la propiedad currentAddress.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentAddress() {
        return currentAddress;
    }

    /**
     * Define el valor de la propiedad currentAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentAddress(String value) {
        this.currentAddress = value;
    }

}
