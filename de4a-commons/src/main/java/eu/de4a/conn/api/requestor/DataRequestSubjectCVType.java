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
 * <p>Clase Java para DataRequestSubjectCVType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DataRequestSubjectCVType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="DataSubjectPerson" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}NaturalPersonIdentifierType"/&gt;
 *           &lt;element name="DataSubjectCompany" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}LegalEntityIdentifierType"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="DataSubjectRepresentative" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}NaturalPersonIdentifierType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataRequestSubjectCVType", propOrder = {
    "dataSubjectPerson",
    "dataSubjectCompany",
    "dataSubjectRepresentative"
})
public class DataRequestSubjectCVType {

    @XmlElement(name = "DataSubjectPerson")
    protected NaturalPersonIdentifierType dataSubjectPerson;
    @XmlElement(name = "DataSubjectCompany")
    protected LegalEntityIdentifierType dataSubjectCompany;
    @XmlElement(name = "DataSubjectRepresentative")
    protected NaturalPersonIdentifierType dataSubjectRepresentative;

    /**
     * Obtiene el valor de la propiedad dataSubjectPerson.
     * 
     * @return
     *     possible object is
     *     {@link NaturalPersonIdentifierType }
     *     
     */
    public NaturalPersonIdentifierType getDataSubjectPerson() {
        return dataSubjectPerson;
    }

    /**
     * Define el valor de la propiedad dataSubjectPerson.
     * 
     * @param value
     *     allowed object is
     *     {@link NaturalPersonIdentifierType }
     *     
     */
    public void setDataSubjectPerson(NaturalPersonIdentifierType value) {
        this.dataSubjectPerson = value;
    }

    /**
     * Obtiene el valor de la propiedad dataSubjectCompany.
     * 
     * @return
     *     possible object is
     *     {@link LegalEntityIdentifierType }
     *     
     */
    public LegalEntityIdentifierType getDataSubjectCompany() {
        return dataSubjectCompany;
    }

    /**
     * Define el valor de la propiedad dataSubjectCompany.
     * 
     * @param value
     *     allowed object is
     *     {@link LegalEntityIdentifierType }
     *     
     */
    public void setDataSubjectCompany(LegalEntityIdentifierType value) {
        this.dataSubjectCompany = value;
    }

    /**
     * Obtiene el valor de la propiedad dataSubjectRepresentative.
     * 
     * @return
     *     possible object is
     *     {@link NaturalPersonIdentifierType }
     *     
     */
    public NaturalPersonIdentifierType getDataSubjectRepresentative() {
        return dataSubjectRepresentative;
    }

    /**
     * Define el valor de la propiedad dataSubjectRepresentative.
     * 
     * @param value
     *     allowed object is
     *     {@link NaturalPersonIdentifierType }
     *     
     */
    public void setDataSubjectRepresentative(NaturalPersonIdentifierType value) {
        this.dataSubjectRepresentative = value;
    }

}
