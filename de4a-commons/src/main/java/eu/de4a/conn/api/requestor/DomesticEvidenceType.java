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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Clase Java para DomesticEvidenceType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DomesticEvidenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DomesticEvidenceIdRef" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}IdRefType"/&gt;
 *         &lt;element name="IssuingType" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}IssuingTypeType"/&gt;
 *         &lt;element name="MimeType" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}MimeTypeType"/&gt;
 *         &lt;element name="DataLanguage" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}DataLanguageType"/&gt;
 *         &lt;element name="AddtionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomesticEvidenceType", propOrder = {
    "domesticEvidenceIdRef",
    "issuingType",
    "mimeType",
    "dataLanguage",
    "addtionalInfo"
})
public class DomesticEvidenceType {

    @XmlElement(name = "DomesticEvidenceIdRef", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object domesticEvidenceIdRef;
    @XmlElement(name = "IssuingType", required = true)
    @XmlSchemaType(name = "token")
    protected IssuingTypeType issuingType;
    @XmlElement(name = "MimeType", required = true)
    protected String mimeType;
    @XmlElement(name = "DataLanguage", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String dataLanguage;
    @XmlElement(name = "AddtionalInfo")
    protected String addtionalInfo;

    /**
     * Obtiene el valor de la propiedad domesticEvidenceIdRef.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDomesticEvidenceIdRef() {
        return domesticEvidenceIdRef;
    }

    /**
     * Define el valor de la propiedad domesticEvidenceIdRef.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDomesticEvidenceIdRef(Object value) {
        this.domesticEvidenceIdRef = value;
    }

    /**
     * Obtiene el valor de la propiedad issuingType.
     * 
     * @return
     *     possible object is
     *     {@link IssuingTypeType }
     *     
     */
    public IssuingTypeType getIssuingType() {
        return issuingType;
    }

    /**
     * Define el valor de la propiedad issuingType.
     * 
     * @param value
     *     allowed object is
     *     {@link IssuingTypeType }
     *     
     */
    public void setIssuingType(IssuingTypeType value) {
        this.issuingType = value;
    }

    /**
     * Obtiene el valor de la propiedad mimeType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Define el valor de la propiedad mimeType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Obtiene el valor de la propiedad dataLanguage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataLanguage() {
        return dataLanguage;
    }

    /**
     * Define el valor de la propiedad dataLanguage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataLanguage(String value) {
        this.dataLanguage = value;
    }

    /**
     * Obtiene el valor de la propiedad addtionalInfo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddtionalInfo() {
        return addtionalInfo;
    }

    /**
     * Define el valor de la propiedad addtionalInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddtionalInfo(String value) {
        this.addtionalInfo = value;
    }

}
