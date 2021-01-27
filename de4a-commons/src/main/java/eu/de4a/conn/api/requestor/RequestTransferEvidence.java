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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="RequestId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}RequestIdType"/&gt;
 *         &lt;element name="SpecificationId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}SpecificationIdType"/&gt;
 *         &lt;element name="TimeStamp" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}TimeStampType"/&gt;
 *         &lt;element name="ProcedureId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}ProcedureIdType" minOccurs="0"/&gt;
 *         &lt;element name="DataEvaluator" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}AgentCVType"/&gt;
 *         &lt;element name="DataOwner" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}AgentCVType"/&gt;
 *         &lt;element name="DataRequestSubject" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}DataRequestSubjectCVType"/&gt;
 *         &lt;element name="RequestGrounds" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}RequestGroundsType"/&gt;
 *         &lt;element name="CanonicalEvidenceId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}CanonicalEvidenceIdType"/&gt;
 *         &lt;element name="EvidenceServiceData" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}EvidenceServiceDataType"/&gt;
 *         &lt;element name="ReturnServiceId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}ReturnServiceIdType"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Language" type="{http://www.w3.org/2001/XMLSchema}language" default="en" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "requestId",
    "specificationId",
    "timeStamp",
    "procedureId",
    "dataEvaluator",
    "dataOwner",
    "dataRequestSubject",
    "requestGrounds",
    "canonicalEvidenceId",
    "evidenceServiceData",
    "returnServiceId"
})
@XmlRootElement(name = "RequestTransferEvidence")
public class RequestTransferEvidence {

    @XmlElement(name = "RequestId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String requestId;
    @XmlElement(name = "SpecificationId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String specificationId;
    @XmlElement(name = "TimeStamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeStamp;
    @XmlElement(name = "ProcedureId")
    protected String procedureId;
    @XmlElement(name = "DataEvaluator", required = true)
    protected AgentCVType dataEvaluator;
    @XmlElement(name = "DataOwner", required = true)
    protected AgentCVType dataOwner;
    @XmlElement(name = "DataRequestSubject", required = true)
    protected DataRequestSubjectCVType dataRequestSubject;
    @XmlElement(name = "RequestGrounds", required = true)
    protected RequestGroundsType requestGrounds;
    @XmlElement(name = "CanonicalEvidenceId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String canonicalEvidenceId;
    @XmlElement(name = "EvidenceServiceData", required = true)
    protected EvidenceServiceDataType evidenceServiceData;
    @XmlElement(name = "ReturnServiceId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String returnServiceId;
    @XmlAttribute(name = "Language")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String language;

    /**
     * Obtiene el valor de la propiedad requestId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Define el valor de la propiedad requestId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Obtiene el valor de la propiedad specificationId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecificationId() {
        return specificationId;
    }

    /**
     * Define el valor de la propiedad specificationId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecificationId(String value) {
        this.specificationId = value;
    }

    /**
     * Obtiene el valor de la propiedad timeStamp.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Define el valor de la propiedad timeStamp.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeStamp(XMLGregorianCalendar value) {
        this.timeStamp = value;
    }

    /**
     * Obtiene el valor de la propiedad procedureId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedureId() {
        return procedureId;
    }

    /**
     * Define el valor de la propiedad procedureId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedureId(String value) {
        this.procedureId = value;
    }

    /**
     * Obtiene el valor de la propiedad dataEvaluator.
     * 
     * @return
     *     possible object is
     *     {@link AgentCVType }
     *     
     */
    public AgentCVType getDataEvaluator() {
        return dataEvaluator;
    }

    /**
     * Define el valor de la propiedad dataEvaluator.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentCVType }
     *     
     */
    public void setDataEvaluator(AgentCVType value) {
        this.dataEvaluator = value;
    }

    /**
     * Obtiene el valor de la propiedad dataOwner.
     * 
     * @return
     *     possible object is
     *     {@link AgentCVType }
     *     
     */
    public AgentCVType getDataOwner() {
        return dataOwner;
    }

    /**
     * Define el valor de la propiedad dataOwner.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentCVType }
     *     
     */
    public void setDataOwner(AgentCVType value) {
        this.dataOwner = value;
    }

    /**
     * Obtiene el valor de la propiedad dataRequestSubject.
     * 
     * @return
     *     possible object is
     *     {@link DataRequestSubjectCVType }
     *     
     */
    public DataRequestSubjectCVType getDataRequestSubject() {
        return dataRequestSubject;
    }

    /**
     * Define el valor de la propiedad dataRequestSubject.
     * 
     * @param value
     *     allowed object is
     *     {@link DataRequestSubjectCVType }
     *     
     */
    public void setDataRequestSubject(DataRequestSubjectCVType value) {
        this.dataRequestSubject = value;
    }

    /**
     * Obtiene el valor de la propiedad requestGrounds.
     * 
     * @return
     *     possible object is
     *     {@link RequestGroundsType }
     *     
     */
    public RequestGroundsType getRequestGrounds() {
        return requestGrounds;
    }

    /**
     * Define el valor de la propiedad requestGrounds.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestGroundsType }
     *     
     */
    public void setRequestGrounds(RequestGroundsType value) {
        this.requestGrounds = value;
    }

    /**
     * Obtiene el valor de la propiedad canonicalEvidenceId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCanonicalEvidenceId() {
        return canonicalEvidenceId;
    }

    /**
     * Define el valor de la propiedad canonicalEvidenceId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCanonicalEvidenceId(String value) {
        this.canonicalEvidenceId = value;
    }

    /**
     * Obtiene el valor de la propiedad evidenceServiceData.
     * 
     * @return
     *     possible object is
     *     {@link EvidenceServiceDataType }
     *     
     */
    public EvidenceServiceDataType getEvidenceServiceData() {
        return evidenceServiceData;
    }

    /**
     * Define el valor de la propiedad evidenceServiceData.
     * 
     * @param value
     *     allowed object is
     *     {@link EvidenceServiceDataType }
     *     
     */
    public void setEvidenceServiceData(EvidenceServiceDataType value) {
        this.evidenceServiceData = value;
    }

    /**
     * Obtiene el valor de la propiedad returnServiceId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnServiceId() {
        return returnServiceId;
    }

    /**
     * Define el valor de la propiedad returnServiceId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnServiceId(String value) {
        this.returnServiceId = value;
    }

    /**
     * Obtiene el valor de la propiedad language.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        if (language == null) {
            return "en";
        } else {
            return language;
        }
    }

    /**
     * Define el valor de la propiedad language.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

}
