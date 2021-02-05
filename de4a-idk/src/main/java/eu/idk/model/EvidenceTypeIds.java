package eu.idk.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Canonical evidence types
 *
 */
public enum EvidenceTypeIds {
	HIGHEREDCERTIFICATE("HigherEdCertificate"), SECONDARYEDCERTIFICATE("SecondaryEdCertificate"),
	RESIDENCYPROOF("ResidencyProof"), BIRTHCERTIFICATE("BirthCertificate"), MARRIAGECERTIFICATE("MarriageCertificate"),
	DEATHCERTIFICATE("DeathCertificate");

	private String value;

	EvidenceTypeIds(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static EvidenceTypeIds fromValue(String text) {
		for (EvidenceTypeIds b : EvidenceTypeIds.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
