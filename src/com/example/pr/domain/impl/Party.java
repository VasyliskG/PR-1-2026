package com.example.pr.domain.impl;

import com.example.pr.domain.BaseEntity;
import com.example.pr.domain.exeption.EntityValidationException;
import com.example.pr.domain.util.ValidationError;
import java.util.UUID;

public class Party extends BaseEntity implements Comparable<Party> {

  public static final String FIELD_PARTY_CODE = "partyCode";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_ABBREVIATION = "abbreviation";

  private String partyCode;
  private String name;
  private String abbreviation;
  private String logoPath;
  private String program;

  public Party() {
    super();
  }

  public Party(String partyCode, String name, String abbreviation, String logoPath, String program) {
    this();
    setPartyCode(partyCode);
    setName(name);
    setAbbreviation(abbreviation);
    setLogoPath(logoPath);
    setProgram(program);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public String getPartyCode() {
    return partyCode;
  }

  public void setPartyCode(String partyCode) {
    clearError(FIELD_PARTY_CODE);
    if (partyCode == null) {
      addError(FIELD_PARTY_CODE, ValidationError.PARTY_ID_REQUIRED.getMessage());
    }
    this.partyCode = partyCode;
    updateTimestamp();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    clearError(FIELD_NAME);
    if (name == null || name.trim().isEmpty()) {
      addError(FIELD_NAME, ValidationError.PARTY_NAME_REQUIRED.getMessage());
    } else if (name.length() < 2 || name.length() > 100) {
      addError(FIELD_NAME, ValidationError.PARTY_NAME_LENGTH.getMessage());
    }
    this.name = name;
    updateTimestamp();
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    clearError(FIELD_ABBREVIATION);
    if (abbreviation != null && (abbreviation.length() < 2 || abbreviation.length() > 10)) {
      addError(FIELD_ABBREVIATION, ValidationError.PARTY_ABBREVIATION_LENGTH.getMessage());
    }
    this.abbreviation = abbreviation;
    updateTimestamp();
  }

  public String getLogoPath() {
    return logoPath;
  }

  public void setLogoPath(String logoPath) {
    this.logoPath = logoPath;
    updateTimestamp();
  }

  public String getProgram() {
    return program;
  }

  public void setProgram(String program) {
    this.program = program;
    updateTimestamp();
  }

  @Override
  public int compareTo(Party o) {
    return this.name.compareTo(o.name);
  }

  @Override
  public String toString() {
    return String.format("Party{name='%s', abbreviation='%s'}", name, abbreviation);
  }
}
