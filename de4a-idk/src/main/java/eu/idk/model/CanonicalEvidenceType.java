package eu.idk.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets CanonicalEvidenceType
 */
public enum CanonicalEvidenceType {
  HIGHEREDCERTIFICATE("HigherEdCertificate"),
    SECONDARYEDCERTIFICATE("SecondaryEdCertificate"),
    RESIDENCYPROOF("ResidencyProof"),
    BIRTHCERTIFICATE("BirthCertificate"),
    MARRIAGECERTIFICATE("MarriageCertificate"),
    COMPANYREGISTRATION("CompanyRegistration");

  private String value;

  CanonicalEvidenceType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static CanonicalEvidenceType fromValue(String text) {
    for (CanonicalEvidenceType b : CanonicalEvidenceType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
