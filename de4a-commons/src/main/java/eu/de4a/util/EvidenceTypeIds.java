package eu.de4a.util;


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
	public String toString() {
		return String.valueOf(value);
	}

	public static EvidenceTypeIds fromValue(String text) {
		for (EvidenceTypeIds b : EvidenceTypeIds.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
