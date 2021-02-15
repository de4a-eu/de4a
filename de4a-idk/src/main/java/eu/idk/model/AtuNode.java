package eu.idk.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * AtuNode
 */
@Validated
public class AtuNode   {
  @JsonProperty("element")
  private AtuItem element = null;

  @JsonProperty("childen")
  @Valid
  private List<AtuNode> childen = null;

  public AtuNode element(AtuItem element) {
    this.element = element;
    return this;
  }

  /**
   * Get element
   * @return element
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public AtuItem getElement() {
    return element;
  }

  public void setElement(AtuItem element) {
    this.element = element;
  }

  public AtuNode childen(List<AtuNode> childen) {
    this.childen = childen;
    return this;
  }

  public AtuNode addChildenItem(AtuNode childenItem) {
    if (this.childen == null) {
      this.childen = new ArrayList<>();
    }
    this.childen.add(childenItem);
    return this;
  }

  /**
   * Get childen
   * @return childen
   **/
  @Schema(description = "")
      @Valid
    public List<AtuNode> getChilden() {
    return childen;
  }

  public void setChilden(List<AtuNode> childen) {
    this.childen = childen;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AtuNode atuNode = (AtuNode) o;
    return Objects.equals(this.element, atuNode.element) &&
        Objects.equals(this.childen, atuNode.childen);
  }

  @Override
  public int hashCode() {
    return Objects.hash(element, childen);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AtuNode {\n");
    
    sb.append("    element: ").append(toIndentedString(element)).append("\n");
    sb.append("    childen: ").append(toIndentedString(childen)).append("\n");
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
