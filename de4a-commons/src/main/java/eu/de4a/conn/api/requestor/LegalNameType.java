
package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * 
 * 				Current legal name for the legal person or organisation.
 * 			
 * 
 * <p>Clase Java para LegalNameType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LegalNameType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute ref="{http://eidas.europa.eu/attributes/legalperson}LatinScript"/&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegalNameType", namespace = "http://eidas.europa.eu/attributes/legalperson", propOrder = {
    "value"
})
public class LegalNameType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "LatinScript", namespace = "http://eidas.europa.eu/attributes/legalperson")
    protected Boolean latinScript;

    /**
     * Obtiene el valor de la propiedad value.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Define el valor de la propiedad value.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor de la propiedad latinScript.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isLatinScript() {
        if (latinScript == null) {
            return true;
        } else {
            return latinScript;
        }
    }

    /**
     * Define el valor de la propiedad latinScript.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLatinScript(Boolean value) {
        this.latinScript = value;
    }

}
