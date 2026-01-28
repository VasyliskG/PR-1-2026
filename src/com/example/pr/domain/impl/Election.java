package com.example.pr.domain.impl;

import com.example.pr.domain.BaseEntity;
import com.example.pr.domain.enums.ElectionStatus;
import com.example.pr.domain.exeption.EntityValidationException;
import com.example.pr.domain.util.ValidationError;
import java.time.LocalDateTime;

public class Election extends BaseEntity implements Comparable<Election> {

  public static final String FIELD_NAME = "name";
  public static final String FIELD_START_DATE = "startDate";
  public static final String FIELD_END_DATE = "endDate";
  public static final String FIELD_STATUS = "status";

  private String name;
  private String description;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private ElectionStatus status;

  public Election() {
    super();
  }

  public Election(String name, String description, LocalDateTime startDate,
      LocalDateTime endDate, ElectionStatus status) {
    this();
    setName(name);
    setDescription(description);
    setStartDate(startDate);
    setEndDate(endDate);
    setStatus(status);

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
      addError(FIELD_NAME, ValidationError.ELECTION_NAME_REQUIRED.getMessage());
    } else if (name.length() < 5 || name.length() > 200) {
      addError(FIELD_NAME, ValidationError.ELECTION_NAME_LENGTH.getMessage());
    }
    this.name = name;
    updateTimestamp();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
    updateTimestamp();
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    clearError(FIELD_START_DATE);
    if (startDate == null) {
      addError(FIELD_START_DATE, ValidationError.ELECTION_START_DATE_REQUIRED.getMessage());
    }
    this.startDate = startDate;
    validateDates();
    updateTimestamp();
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    clearError(FIELD_END_DATE);
    if (endDate == null) {
      addError(FIELD_END_DATE, ValidationError.ELECTION_END_DATE_REQUIRED.getMessage());
    }
    this.endDate = endDate;
    validateDates();
    updateTimestamp();
  }

  private void validateDates() {
    if (startDate != null && endDate != null && !endDate.isAfter(startDate)) {
      addError(FIELD_END_DATE, ValidationError.ELECTION_DATE_INVALID.getMessage());
    }
  }

  public ElectionStatus getStatus() {
    return status;
  }

  public void setStatus(ElectionStatus status) {
    clearError(FIELD_STATUS);
    if (status == null) {
      addError(FIELD_STATUS, ValidationError.ELECTION_STATUS_REQUIRED.getMessage());
    }
    this.status = status;
    updateTimestamp();
  }

  public boolean isActive() {
    return status == ElectionStatus.ACTIVE;
  }

  @Override
  public int compareTo(Election o) {
    return this.startDate.compareTo(o.startDate);
  }

  @Override
  public String toString() {
    return String.format("Election{name='%s', status=%s, %s - %s}",
        name, status, startDate, endDate);
  }
}
