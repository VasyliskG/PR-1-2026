package com.example.pr.domain.impl;

import com.example.pr.domain.BaseEntity;
import com.example.pr.domain.exeption.EntityValidationException;
import com.example.pr.domain.util.ValidationError;
import java.util.UUID;

public class Candidate extends BaseEntity implements Comparable<Candidate> {

  public static final String FIELD_FIRST_NAME = "firstName";
  public static final String FIELD_LAST_NAME = "lastName";
  public static final String FIELD_PARTY_ID = "partyId";
  public static final String FIELD_ELECTION_ID = "electionId";
  public static final String FIELD_PROGRAM = "program";

  private String firstName;
  private String lastName;
  private UUID partyId;
  private UUID electionId;
  private String program;
  private String photoPath;
  private String biography;

  public Candidate() {
    super();
  }

  public Candidate(String firstName, String lastName, UUID partyId, UUID electionId,
      String program, String photoPath, String biography) {
    this();
    setFirstName(firstName);
    setLastName(lastName);
    setPartyId(partyId);
    setElectionId(electionId);
    setProgram(program);
    setPhotoPath(photoPath);
    setBiography(biography);

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

  public UUID getPartyId() {
    return partyId;
  }

  public void setPartyId(UUID partyId) {
    clearError(FIELD_PARTY_ID);
    if (partyId == null) {
      addError(FIELD_PARTY_ID, ValidationError.PARTY_ID_REQUIRED.getMessage());
    }
    this.partyId = partyId;
    updateTimestamp();
  }

  public UUID getElectionId() {
    return electionId;
  }

  public void setElectionId(UUID electionId) {
    clearError(FIELD_ELECTION_ID);
    if (electionId == null) {
      addError(FIELD_ELECTION_ID, ValidationError.ELECTION_ID_REQUIRED.getMessage());
    }
    this.electionId = electionId;
    updateTimestamp();
  }

  public String getProgram() {
    return program;
  }

  public void setProgram(String program) {
    clearError(FIELD_PROGRAM);
    if (program != null && program.length() > 4096) {
      addError(FIELD_PROGRAM, ValidationError.CANDIDATE_PROGRAM_LENGTH.getMessage());
    }
    this.program = program;
    updateTimestamp();
  }

  public String getPhotoPath() {
    return photoPath;
  }

  public void setPhotoPath(String photoPath) {
    this.photoPath = photoPath;
    updateTimestamp();
  }

  public String getBiography() {
    return biography;
  }

  public void setBiography(String biography) {
    this.biography = biography;
    updateTimestamp();
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Override
  public int compareTo(Candidate o) {
    return this.lastName.compareTo(o.lastName);
  }

  @Override
  public String toString() {
    return String.format("Candidate{%s %s, partyId=%s}", firstName, lastName, partyId);
  }
}