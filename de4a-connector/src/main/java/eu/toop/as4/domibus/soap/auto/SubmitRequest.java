//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.01 a las 02:37:52 PM CET 
//


package eu.toop.as4.domibus.soap.auto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bodyload" type="{http://org.ecodex.backend/1_1/}LargePayloadType" minOccurs="0"/&gt;
 *         &lt;element name="payload" type="{http://org.ecodex.backend/1_1/}LargePayloadType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "bodyload",
    "payload"
})
@XmlRootElement(name = "submitRequest", namespace = "http://org.ecodex.backend/1_1/")
public class SubmitRequest {

    @XmlElement(namespace = "")
    protected LargePayloadType bodyload;
    @XmlElement(namespace = "", nillable = true)
    protected List<LargePayloadType> payload;

    /**
     * Obtiene el valor de la propiedad bodyload.
     * 
     * @return
     *     possible object is
     *     {@link LargePayloadType }
     *     
     */
    public LargePayloadType getBodyload() {
        return bodyload;
    }

    /**
     * Define el valor de la propiedad bodyload.
     * 
     * @param value
     *     allowed object is
     *     {@link LargePayloadType }
     *     
     */
    public void setBodyload(LargePayloadType value) {
        this.bodyload = value;
    }

    /**
     * Gets the value of the payload property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the payload property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayload().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LargePayloadType }
     * 
     * 
     */
    public List<LargePayloadType> getPayload() {
        if (payload == null) {
            payload = new ArrayList<LargePayloadType>();
        }
        return this.payload;
    }

}
