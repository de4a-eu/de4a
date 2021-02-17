package eu.de4a.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Canonical evidence types
 *
 */
public enum EvidenceTypeIds {
	HIGHEREDCERTIFICATE("HigherEdCertificate"), SECONDARYEDCERTIFICATE("SecondaryEdCertificate"),
	RESIDENCYPROOF("ResidencyProof"), BIRTHCERTIFICATE("BirthCertificate"), MARRIAGECERTIFICATE("MarriageCertificate"),
	DEATHCERTIFICATE("DeathCertificate"), DOINGBUSINESSABROAD("DoingBusinessAbroad");

	private String value;
	private static Map<String, EvidenceTypeIds> lookup = new HashMap<>();
	
	static {
		lookup = Arrays.asList(EvidenceTypeIds.values()).stream()
				.collect(Collectors.toMap(EvidenceTypeIds::toString, x -> x));
	}

	EvidenceTypeIds(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
	
	public String getValue() {
		return this.value;
	}

	public static EvidenceTypeIds fromValue(String text) {
		for (EvidenceTypeIds b : EvidenceTypeIds.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
	
	public static EvidenceTypeIds getByName(String name) {
		return lookup.get(name);
	}
}
