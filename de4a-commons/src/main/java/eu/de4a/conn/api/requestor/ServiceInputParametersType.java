//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.01 a las 02:37:33 PM CET 
//


package eu.de4a.conn.api.requestor;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ServiceInputParametersType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ServiceInputParametersType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ServiceInputParameter" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}ServiceInputParameterType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceInputParametersType", propOrder = {
    "serviceInputParameter"
})
public class ServiceInputParametersType {

    @XmlElement(name = "ServiceInputParameter", required = true)
    protected List<ServiceInputParameterType> serviceInputParameter;

    /**
     * Gets the value of the serviceInputParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceInputParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceInputParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServiceInputParameterType }
     * 
     * 
     */
    public List<ServiceInputParameterType> getServiceInputParameter() {
        if (serviceInputParameter == null) {
            serviceInputParameter = new ArrayList<ServiceInputParameterType>();
        }
        return this.serviceInputParameter;
    }

}
