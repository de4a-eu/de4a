package eu.toop.rest.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;


public enum AtuLevel {
  NUTS1("nuts1"),
    NUTS2("nuts2"),
    NUTS3("nuts3"),
    LAU("lau"),
    UNIVERSITY("university");

  private String value;

  AtuLevel(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static AtuLevel fromValue(String text) {
    for (AtuLevel b : AtuLevel.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
