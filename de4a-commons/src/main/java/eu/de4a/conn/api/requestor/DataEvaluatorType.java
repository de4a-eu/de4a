
package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DataEvaluatorType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DataEvaluatorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="Agent" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}AgentCVType"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataEvaluatorType", propOrder = {

})
public class DataEvaluatorType {

    @XmlElement(name = "Agent", required = true)
    protected AgentCVType agent;

    /**
     * Obtiene el valor de la propiedad agent.
     * 
     * @return
     *     possible object is
     *     {@link AgentCVType }
     *     
     */
    public AgentCVType getAgent() {
        return agent;
    }

    /**
     * Define el valor de la propiedad agent.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentCVType }
     *     
     */
    public void setAgent(AgentCVType value) {
        this.agent = value;
    }

}