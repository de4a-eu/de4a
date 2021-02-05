//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.02.04 a las 04:55:32 PM CET 
//


package eu.de4a.conn.api.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;sequence&gt;
 *           &lt;element name="RequestId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}RequestIdType"/&gt;
 *           &lt;element name="SpecificationId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}SpecificationIdType"/&gt;
 *           &lt;element name="TimeStamp" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}TimeStampType"/&gt;
 *           &lt;element name="ProcedureId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}ProcedureIdType" minOccurs="0"/&gt;
 *           &lt;element name="DataEvaluator" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}AgentCVType"/&gt;
 *           &lt;element name="DataOwner" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}AgentCVType"/&gt;
 *           &lt;element name="DataRequestSubject" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}DataRequestSubjectCVType"/&gt;
 *           &lt;element name="CanonicalEvidenceId" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}CanonicalEvidenceIdType"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="CanonicalEvidence" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}CanonicalEvidenceType"/&gt;
 *             &lt;element name="DomesticEvidenceList" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}DomesticsEvidencesType" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;element name="Error" type="{http://www.de4a.eu/2020/data/requestor/pattern/intermediate}ErrorType"/&gt;
 *         &lt;/choice&gt;
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
    "requestId",
    "specificationId",
    "timeStamp",
    "procedureId",
    "dataEvaluator",
    "dataOwner",
    "dataRequestSubject",
    "canonicalEvidenceId",
    "canonicalEvidence",
    "domesticEvidenceList",
    "error"
})
@XmlRootElement(name = "ResponseTransferEvidence")
public class ResponseTransferEvidence {

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
    @XmlElement(name = "CanonicalEvidenceId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String canonicalEvidenceId;
    @XmlElement(name = "CanonicalEvidence")
    protected CanonicalEvidenceType canonicalEvidence;
    @XmlElement(name = "DomesticEvidenceList")
    protected DomesticsEvidencesType domesticEvidenceList;
    @XmlElement(name = "Error")
    protected ErrorType error;

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
     * Obtiene el valor de la propiedad canonicalEvidence.
     * 
     * @return
     *     possible object is
     *     {@link CanonicalEvidenceType }
     *     
     */
    public CanonicalEvidenceType getCanonicalEvidence() {
        return canonicalEvidence;
    }

    /**
     * Define el valor de la propiedad canonicalEvidence.
     * 
     * @param value
     *     allowed object is
     *     {@link CanonicalEvidenceType }
     *     
     */
    public void setCanonicalEvidence(CanonicalEvidenceType value) {
        this.canonicalEvidence = value;
    }

    /**
     * Obtiene el valor de la propiedad domesticEvidenceList.
     * 
     * @return
     *     possible object is
     *     {@link DomesticsEvidencesType }
     *     
     */
    public DomesticsEvidencesType getDomesticEvidenceList() {
        return domesticEvidenceList;
    }

    /**
     * Define el valor de la propiedad domesticEvidenceList.
     * 
     * @param value
     *     allowed object is
     *     {@link DomesticsEvidencesType }
     *     
     */
    public void setDomesticEvidenceList(DomesticsEvidencesType value) {
        this.domesticEvidenceList = value;
    }

    /**
     * Obtiene el valor de la propiedad error.
     * 
     * @return
     *     possible object is
     *     {@link ErrorType }
     *     
     */
    public ErrorType getError() {
        return error;
    }

    /**
     * Define el valor de la propiedad error.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorType }
     *     
     */
    public void setError(ErrorType value) {
        this.error = value;
    }

}
