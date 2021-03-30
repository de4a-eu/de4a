package eu.de4a.scsp.translate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.DomesticEvidenceType;
import eu.de4a.iem.jaxb.common.types.IssuingTypeType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseExtractEvidenceType;
import eu.de4a.util.DOMUtils;
import un.unece.uncefact.codelist.specification.ianamimemediatype._2003.BinaryObjectMimeCodeContentType;

public interface EvidenceTranslator {
	public static final String LANGUAGE_DEFAULT = "es";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String ID_PETICION_PARAM = "idPeticion";
	public static final String TIMESTAMP_PARAM = "timeStamp";
	public static final String NIFSOLICITANTE_PARAM = "nifSolicitante";
	public static final String PROCEDIMIENTO_PARAM = "procedimiento";
	public static final String TIPO_DOCUMENTACION_PARAM = "tipoDocumentacion";
	public static final String DOCUMENTACION_PARAM = "documentacion";
	public static final String NOMBRE_PARAM = "nombre";
	public static final String AP1_PARAM = "ap1";
	public static final String AP2_PARAM = "ap2";
	public static final String FECHA_NACIMIENTO_PARAM = "fechaNacimiento";
	public static final String EVALUATOR_ID_PARAM = "evaluatorId";
	public static final String EVALUATOR_NAME_PARAM = "evaluatorName";
	public static final String OWNER_ID_PARAM = "ownerId";
	public static final String OWNER_NAME_PARAM = "ownerName";
	public static final String CANONICAL_EVIDENCE_PARAM = "canonicalEvidenceId";
	public static final String DOMESTIC_EVIDENCES_PARAM = "domesticEvidences";

	public Element translateEvidenceRequest(Element request) throws MessageException;

	public Element translateEvidenceResponse(Element response) throws MessageException;

	public ResponseExtractEvidenceType translateExtractEvidenceResponse(Element response) throws MessageException;
	public RequestForwardEvidenceType translateRequestForwardEvidence(Element response) throws MessageException;

	public static String getIdPeticion(String seed) {
		return seed + Calendar.getInstance().getTimeInMillis();
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(new Date());
	}

	public static DomesticEvidenceType buildDomesticEvidence(IssuingTypeType issue, String mimetype,
			String language, Document data) {
		DomesticEvidenceType domestic = new DomesticEvidenceType();
		domestic.setDataLanguage(language);
		domestic.setIssuingType(issue);
		domestic.setMimeType(BinaryObjectMimeCodeContentType.fromValue(mimetype));
		domestic.setEvidenceData(DOMUtils.encodeCompressed(data));
		return domestic;
	}
}
