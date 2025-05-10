package com.acodexm.datatransformer.model;

public enum TimeWindowType {
  HOUR("1h"),
  DAY("1d"),
  WEEK("1w");

  private final String value;

  TimeWindowType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static TimeWindowType fromValue(String value) {
    for (TimeWindowType type : TimeWindowType.values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid TimeWindowType value: " + value);
  }
}
