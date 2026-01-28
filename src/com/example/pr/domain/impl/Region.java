package com.example.pr.domain.impl;

import com.example.pr.domain.BaseEntity;
import com.example.pr.domain.exeption.EntityValidationException;
import com.example.pr.domain.util.ValidationError;

public class Region extends BaseEntity implements Comparable<Region> {

  public static final String FIELD_NAME = "name";
  public static final String FIELD_CODE = "code";

  private String name;
  private String code;
  private String description;

  public Region() {
    super();
  }

  public Region(String name, String code, String description) {
    this();
    setName(name);
    setCode(code);
    setDescription(description);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    clearError(FIELD_NAME);
    if (name == null || name.trim().isEmpty()) {
      addError(FIELD_NAME, ValidationError.REGION_NAME_REQUIRED.getMessage());
    } else if (name.length() < 2 || name.length() > 100) {
      addError(FIELD_NAME, ValidationError.REGION_NAME_LENGTH.getMessage());
    }
    this.name = name;
    updateTimestamp();
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    clearError(FIELD_CODE);
    if (code == null || code.trim().isEmpty()) {
      addError(FIELD_CODE, ValidationError.REGION_CODE_REQUIRED.getMessage());
    } else if (!code.matches("^[A-Z]{2,5}$")) {
      addError(FIELD_CODE, ValidationError.REGION_CODE_FORMAT.getMessage());
    }
    this.code = code;
    updateTimestamp();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
    updateTimestamp();
  }

  @Override
  public int compareTo(Region o) {
    return this.name.compareTo(o.name);
  }

  @Override
  public String toString() {
    return String.format("Region{name='%s', code='%s'}", name, code);
  }
}
