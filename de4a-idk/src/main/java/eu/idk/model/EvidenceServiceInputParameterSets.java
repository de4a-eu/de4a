package eu.idk.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

/**
 * EvidenceServiceInputParameterSets
 */
@Validated
public class EvidenceServiceInputParameterSets   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("title")
  private String title = null;

  /**
   * Gets or Sets recordMatchingAssurance
   */
  public enum RecordMatchingAssuranceEnum {
    LOW("low"),
    
    MEDIUM("medium"),
    
    HIGH("high"),
    
    EXACT("exact");

    private String value;

    RecordMatchingAssuranceEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RecordMatchingAssuranceEnum fromValue(String text) {
      for (RecordMatchingAssuranceEnum b : RecordMatchingAssuranceEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("recordMatchingAssurance")
  private RecordMatchingAssuranceEnum recordMatchingAssurance = null;

  @JsonProperty("parameters")
  @Valid
  private List<MorItem> parameters = null;

  public EvidenceServiceInputParameterSets name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   **/
  @Schema(description = "")
  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EvidenceServiceInputParameterSets title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   **/
  @Schema(description = "")
  
    public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public EvidenceServiceInputParameterSets recordMatchingAssurance(RecordMatchingAssuranceEnum recordMatchingAssurance) {
    this.recordMatchingAssurance = recordMatchingAssurance;
    return this;
  }

  /**
   * Get recordMatchingAssurance
   * @return recordMatchingAssurance
   **/
  @Schema(description = "")
  
    public RecordMatchingAssuranceEnum getRecordMatchingAssurance() {
    return recordMatchingAssurance;
  }

  public void setRecordMatchingAssurance(RecordMatchingAssuranceEnum recordMatchingAssurance) {
    this.recordMatchingAssurance = recordMatchingAssurance;
  }

  public EvidenceServiceInputParameterSets parameters(List<MorItem> parameters) {
    this.parameters = parameters;
    return this;
  }

  public EvidenceServiceInputParameterSets addParametersItem(MorItem parametersItem) {
    if (this.parameters == null) {
      this.parameters = new ArrayList<MorItem>();
    }
    this.parameters.add(parametersItem);
    return this;
  }

  /**
   * Get parameters
   * @return parameters
   **/
  @Schema(description = "")
      @Valid
    public List<MorItem> getParameters() {
    return parameters;
  }

  public void setParameters(List<MorItem> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvidenceServiceInputParameterSets evidenceServiceInputParameterSets = (EvidenceServiceInputParameterSets) o;
    return Objects.equals(this.name, evidenceServiceInputParameterSets.name) &&
        Objects.equals(this.title, evidenceServiceInputParameterSets.title) &&
        Objects.equals(this.recordMatchingAssurance, evidenceServiceInputParameterSets.recordMatchingAssurance) &&
        Objects.equals(this.parameters, evidenceServiceInputParameterSets.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, title, recordMatchingAssurance, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvidenceServiceInputParameterSets {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    recordMatchingAssurance: ").append(toIndentedString(recordMatchingAssurance)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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
