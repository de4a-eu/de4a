package eu.de4a.scsp.translate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.requestor.DomesticEvidenceType;
import eu.de4a.conn.api.requestor.IssuingTypeType;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;

public interface EvidenceTranslator {
	public static final String LANGUAGE_DEFAULT="es";
	public static final String DEFAULT_ENCODING = "UTF-8";  
	public static final String ID_PETICION_PARAM="idPeticion";
	public static final String TIMESTAMP_PARAM="timeStamp";
	public static final String NIFSOLICITANTE_PARAM="nifSolicitante";
	public static final String PROCEDIMIENTO_PARAM="procedimiento";
	public static final String TIPO_DOCUMENTACION_PARAM="tipoDocumentacion";
	public static final String DOCUMENTACION_PARAM="documentacion";
	public static final String NOMBRE_PARAM="nombre";
	public static final String AP1_PARAM="ap1";
	public static final String AP2_PARAM="ap2";
	public static final String FECHA_NACIMIENTO_PARAM="fechaNacimiento"; 
	public static final String EVALUATOR_ID_PARAM="evaluatorId"; 
	public static final String EVALUATOR_NAME_PARAM="evaluatorName"; 
	public static final String OWNER_ID_PARAM="ownerId"; 
	public static final String OWNER_NAME_PARAM="ownerName"; 
	public static final String CANONICAL_EVIDENCE_PARAM="canonicalEvidenceId"; 
	public static final String DOMESTIC_EVIDENCES_PARAM="domesticEvidences"; 
	
	public Element translateEvidenceRequest(Element request)throws MessageException;
	public Element translateEvidenceResponse(Element response)throws MessageException;
	public static String getIdPeticion(String seed) {
		return seed+Calendar.getInstance().getTimeInMillis();
	}
	public static  String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss" ); 
		return sdf.format(new Date()) ;
	}
	public static DomesticEvidenceType buildDomesticEvidence(String id,IssuingTypeType issue,String mimetype,String language ) {
		DomesticEvidenceType  domestic = new DomesticEvidenceType();
		domestic.setDataLanguage(language);
		domestic.setDomesticEvidenceIdRef(id);
		domestic.setIssuingType(issue);
		domestic.setMimeType(mimetype); 
		return domestic;
	}
}
