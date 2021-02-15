package eu.idk.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

/**
 * MorItemText
 */
@Validated
public class MorItemText   {
  @JsonProperty("lang")
  private String lang = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("definition")
  private String definition = null;

  public MorItemText lang(String lang) {
    this.lang = lang;
    return this;
  }

  /**
   * Get lang
   * @return lang
   **/
  @Schema(description = "")
  
    public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public MorItemText label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
   **/
  @Schema(description = "")
  
    public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public MorItemText definition(String definition) {
    this.definition = definition;
    return this;
  }

  /**
   * Get definition
   * @return definition
   **/
  @Schema(description = "")
  
    public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MorItemText morItemText = (MorItemText) o;
    return Objects.equals(this.lang, morItemText.lang) &&
        Objects.equals(this.label, morItemText.label) &&
        Objects.equals(this.definition, morItemText.definition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lang, label, definition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MorItemText {\n");
    
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
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
