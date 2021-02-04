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
 * <p>Clase Java para EvidenceServiceDataType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="EvidenceServiceDataType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="EvidenceServiceURI" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}EvidenceServiceURIType"/&gt;
 *         &lt;element name="ServiceInputParameters" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}ServiceInputParametersType" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvidenceServiceDataType", propOrder = {

})
public class EvidenceServiceDataType {

    @XmlElement(name = "EvidenceServiceURI", required = true)
    protected String evidenceServiceURI;
    @XmlElement(name = "ServiceInputParameters")
    protected ServiceInputParametersType serviceInputParameters;

    /**
     * Obtiene el valor de la propiedad evidenceServiceURI.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvidenceServiceURI() {
        return evidenceServiceURI;
    }

    /**
     * Define el valor de la propiedad evidenceServiceURI.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvidenceServiceURI(String value) {
        this.evidenceServiceURI = value;
    }

    /**
     * Obtiene el valor de la propiedad serviceInputParameters.
     * 
     * @return
     *     possible object is
     *     {@link ServiceInputParametersType }
     *     
     */
    public ServiceInputParametersType getServiceInputParameters() {
        return serviceInputParameters;
    }

    /**
     * Define el valor de la propiedad serviceInputParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceInputParametersType }
     *     
     */
    public void setServiceInputParameters(ServiceInputParametersType value) {
        this.serviceInputParameters = value;
    }

}
