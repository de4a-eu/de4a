//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.01.25 a las 10:54:25 PM CET 
//


package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * 
 * 				Current family name of the natural person.
 * 				<saml:Attribute
 * 					FriendlyName="FamilyName"
 * 					Name="http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName"
 * 					NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
 * 						<saml:AttributeValue xsi:type="eidas:CurrentFamilyNameType">
 * 							Chalk
 * 						</saml:AttributeValue>
 * 				</saml:Attribute>
 * 			
 * 
 * <p>Clase Java para CurrentFamilyNameType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CurrentFamilyNameType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute ref="{http://eidas.europa.eu/attributes/naturalperson}LatinScript"/&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrentFamilyNameType", namespace = "http://eidas.europa.eu/attributes/naturalperson", propOrder = {
    "value"
})
public class CurrentFamilyNameType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "LatinScript", namespace = "http://eidas.europa.eu/attributes/naturalperson")
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
