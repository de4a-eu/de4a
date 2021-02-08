package eu.idk.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * MorItem
 */
@Validated
public class MorItem   {
  @JsonProperty("itemId")
  private String itemId = null;

  /**
   * Gets or Sets itemType
   */
  public enum ItemTypeEnum {
    ATTRIBUTE("attribute"),
    
    ELEMENT("element"),
    
    COMPLEXTYPE("complexType"),
    
    VALUE("value");

    private String value;

    ItemTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ItemTypeEnum fromValue(String text) {
      for (ItemTypeEnum b : ItemTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("itemType")
  private ItemTypeEnum itemType = null;

  @JsonProperty("dataType")
  private String dataType = null;

  @JsonProperty("constraints")
  private String constraints = null;

  @JsonProperty("text")
  @Valid
  private List<MorItemText> text = null;

  @JsonProperty("children")
  @Valid
  private List<MorItem> children = null;

  public MorItem itemId(String itemId) {
    this.itemId = itemId;
    return this;
  }

  /**
   * Identifier of the semantic item in the MOR catalogue
   * @return itemId
   **/
  @Schema(required = true, description = "Identifier of the semantic item in the MOR catalogue")
      @NotNull

    public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public MorItem itemType(ItemTypeEnum itemType) {
    this.itemType = itemType;
    return this;
  }

  /**
   * Get itemType
   * @return itemType
   **/
  @Schema(description = "")
  
    public ItemTypeEnum getItemType() {
    return itemType;
  }

  public void setItemType(ItemTypeEnum itemType) {
    this.itemType = itemType;
  }

  public MorItem dataType(String dataType) {
    this.dataType = dataType;
    return this;
  }

  /**
   * Type of the item if it is an attribute or an element
   * @return dataType
   **/
  @Schema(description = "Type of the item if it is an attribute or an element")
  
    public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public MorItem constraints(String constraints) {
    this.constraints = constraints;
    return this;
  }

  /**
   * XML snippet with the item constraints if any
   * @return constraints
   **/
  @Schema(description = "XML snippet with the item constraints if any")
  
    public String getConstraints() {
    return constraints;
  }

  public void setConstraints(String constraints) {
    this.constraints = constraints;
  }

  public MorItem text(List<MorItemText> text) {
    this.text = text;
    return this;
  }

  public MorItem addTextItem(MorItemText textItem) {
    if (this.text == null) {
      this.text = new ArrayList<MorItemText>();
    }
    this.text.add(textItem);
    return this;
  }

  /**
   * At least the item text in English is available
   * @return text
   **/
  @Schema(description = "At least the item text in English is available")
      @Valid
    public List<MorItemText> getText() {
    return text;
  }

  public void setText(List<MorItemText> text) {
    this.text = text;
  }

  public MorItem children(List<MorItem> children) {
    this.children = children;
    return this;
  }

  public MorItem addChildrenItem(MorItem childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<MorItem>();
    }
    this.children.add(childrenItem);
    return this;
  }

  /**
   * Get children
   * @return children
   **/
  @Schema(description = "")
      @Valid
    public List<MorItem> getChildren() {
    return children;
  }

  public void setChildren(List<MorItem> children) {
    this.children = children;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MorItem morItem = (MorItem) o;
    return Objects.equals(this.itemId, morItem.itemId) &&
        Objects.equals(this.itemType, morItem.itemType) &&
        Objects.equals(this.dataType, morItem.dataType) &&
        Objects.equals(this.constraints, morItem.constraints) &&
        Objects.equals(this.text, morItem.text) &&
        Objects.equals(this.children, morItem.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, itemType, dataType, constraints, text, children);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MorItem {\n");
    
    sb.append("    itemId: ").append(toIndentedString(itemId)).append("\n");
    sb.append("    itemType: ").append(toIndentedString(itemType)).append("\n");
    sb.append("    dataType: ").append(toIndentedString(dataType)).append("\n");
    sb.append("    constraints: ").append(toIndentedString(constraints)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
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
