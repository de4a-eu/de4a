//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.01.29 a las 09:39:05 PM CET 
//


package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * 
 * 				Place of birth for a natural person.
 * 				<saml:Attribute
 * 					FriendlyName="PlaceOfBirth"
 * 					Name=" http://eidas.europa.eu/attributes/naturalperson/PlaceOfBirth"
 * 					NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
 * 						<saml:AttributeValue xsi:type="eidas:PlaceOfBirthType">
 * 							Peterborough
 * 						</saml:AttributeValue>
 * 				</saml:Attribute>
 * 			
 * 
 * <p>Clase Java para PlaceOfBirthType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PlaceOfBirthType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlaceOfBirthType", namespace = "http://eidas.europa.eu/attributes/naturalperson", propOrder = {
    "value"
})
public class PlaceOfBirthType {

    @XmlValue
    protected String value;

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

}
