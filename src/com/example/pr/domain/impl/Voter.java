package com.example.pr.domain.impl;

import com.example.pr.domain.BaseEntity;
import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.exeption.EntityValidationException;
import com.example.pr.domain.util.ValidationError;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Pattern;

public class Voter extends BaseEntity implements Comparable<Voter> {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$");

  public static final String FIELD_FIRST_NAME = "firstName";
  public static final String FIELD_LAST_NAME = "lastName";
  public static final String FIELD_EMAIL = "email";
  public static final String FIELD_PASSWORD = "passwordHash";
  public static final String FIELD_PASSPORT_NUMBER = "passportNumber";
  public static final String FIELD_BIRTH_DATE = "birthDate";
  public static final String FIELD_ROLE = "role";
  public static final String FIELD_REGION_ID = "regionId";

  private String firstName;
  private String lastName;
  private String email;
  private String passwordHash;
  private String passportNumber;
  private LocalDate birthDate;
  private VoterRole role;
  private UUID regionId;

  public Voter() {
    super();
  }

  public Voter(String firstName, String lastName, String email, String passwordHash,
      String passportNumber, LocalDate birthDate, VoterRole role, UUID regionId) {
    this();
    setFirstName(firstName);
    setLastName(lastName);
    setEmail(email);
    setPasswordHash(passwordHash);
    setPassportNumber(passportNumber);
    setBirthDate(birthDate);
    setRole(role);
    setRegionId(regionId);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    clearError(FIELD_FIRST_NAME);
    if (firstName == null || firstName.trim().isEmpty()) {
      addError(FIELD_FIRST_NAME, ValidationError.FIRST_NAME_REQUIRED.getMessage());
    } else if (firstName.length() < 2 || firstName.length() > 50) {
      addError(FIELD_FIRST_NAME, ValidationError.FIRST_NAME_LENGTH.getMessage());
    }
    this.firstName = firstName;
    updateTimestamp();
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    clearError(FIELD_LAST_NAME);
    if (lastName == null || lastName.trim().isEmpty()) {
      addError(FIELD_LAST_NAME, ValidationError.LAST_NAME_REQUIRED.getMessage());
    } else if (lastName.length() < 2 || lastName.length() > 50) {
      addError(FIELD_LAST_NAME, ValidationError.LAST_NAME_LENGTH.getMessage());
    }
    this.lastName = lastName;
    updateTimestamp();
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    clearError(FIELD_EMAIL);
    if (email == null || email.trim().isEmpty()) {
      addError(FIELD_EMAIL, ValidationError.EMAIL_REQUIRED.getMessage());
    } else if (!EMAIL_PATTERN.matcher(email).matches()) {
      addError(FIELD_EMAIL, ValidationError.EMAIL_FORMAT.getMessage());
    }
    this.email = email;
    updateTimestamp();
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    clearError(FIELD_PASSWORD);
    if (passwordHash == null || passwordHash.trim().isEmpty()) {
      addError(FIELD_PASSWORD, ValidationError.PASSWORD_REQUIRED.getMessage());
    }
    this.passwordHash = passwordHash;
    updateTimestamp();
  }

  public String getPassportNumber() {
    return passportNumber;
  }

  public void setPassportNumber(String passportNumber) {
    clearError(FIELD_PASSPORT_NUMBER);
    if (passportNumber == null || passportNumber.trim().isEmpty()) {
      addError(FIELD_PASSPORT_NUMBER, ValidationError.PASSPORT_NUMBER_REQUIRED.getMessage());
    } else if (!passportNumber.matches("^[A-Z]{2}\\d{6}$")) {
      addError(FIELD_PASSPORT_NUMBER, ValidationError.PASSPORT_NUMBER_FORMAT.getMessage());
    }
    this.passportNumber = passportNumber;
    updateTimestamp();
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    clearError(FIELD_BIRTH_DATE);
    if (birthDate == null) {
      addError(FIELD_BIRTH_DATE, ValidationError.BIRTH_DATE_REQUIRED.getMessage());
    } else if (Period.between(birthDate, LocalDate.now()).getYears() < 18) {
      addError(FIELD_BIRTH_DATE, ValidationError.VOTER_TOO_YOUNG.getMessage());
    }
    this.birthDate = birthDate;
    updateTimestamp();
  }

  public VoterRole getRole() {
    return role;
  }

  public void setRole(VoterRole role) {
    clearError(FIELD_ROLE);
    if (role == null) {
      addError(FIELD_ROLE, ValidationError.ROLE_REQUIRED.getMessage());
    }
    this.role = role;
    updateTimestamp();
  }

  public UUID getRegionId() {
    return regionId;
  }

  public void setRegionId(UUID regionId) {
    clearError(FIELD_REGION_ID);
    if (regionId == null) {
      addError(FIELD_REGION_ID, ValidationError.REGION_ID_REQUIRED.getMessage());
    }
    this.regionId = regionId;
    updateTimestamp();
  }

  @Override
  public int compareTo(Voter o) {
    return this.lastName.compareTo(o.lastName);
  }

  @Override
  public String toString() {
    return String.format("Voter{%s %s, email='%s', role=%s}", firstName, lastName, email, role);
  }
}
