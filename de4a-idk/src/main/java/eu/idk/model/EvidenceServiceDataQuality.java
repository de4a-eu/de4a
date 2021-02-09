package eu.idk.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.*;

/**
 * EvidenceServiceDataQuality
 */
@Validated
public class EvidenceServiceDataQuality   {
  @JsonProperty("populationCoverage")
  private Integer populationCoverage = null;

  @JsonProperty("additionalInfo")
  private String additionalInfo = null;

  public EvidenceServiceDataQuality populationCoverage(Integer populationCoverage) {
    this.populationCoverage = populationCoverage;
    return this;
  }

  /**
   * Get populationCoverage
   * minimum: 1
   * maximum: 100
   * @return populationCoverage
   **/
  @Schema(description = "")
  
  @Min(1) @Max(100)   public Integer getPopulationCoverage() {
    return populationCoverage;
  }

  public void setPopulationCoverage(Integer populationCoverage) {
    this.populationCoverage = populationCoverage;
  }

  public EvidenceServiceDataQuality additionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
    return this;
  }

  /**
   * Get additionalInfo
   * @return additionalInfo
   **/
  @Schema(description = "")
  
    public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvidenceServiceDataQuality evidenceServiceDataQuality = (EvidenceServiceDataQuality) o;
    return Objects.equals(this.populationCoverage, evidenceServiceDataQuality.populationCoverage) &&
        Objects.equals(this.additionalInfo, evidenceServiceDataQuality.additionalInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(populationCoverage, additionalInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvidenceServiceDataQuality {\n");
    
    sb.append("    populationCoverage: ").append(toIndentedString(populationCoverage)).append("\n");
    sb.append("    additionalInfo: ").append(toIndentedString(additionalInfo)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
