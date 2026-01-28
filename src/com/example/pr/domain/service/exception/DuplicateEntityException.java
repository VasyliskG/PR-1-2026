package com.example.pr.domain.service.exception;

/**
 * Виняток, коли сутність вже існує.
 */
public class DuplicateEntityException extends ServiceException {

  private final String entityType;
  private final String field;
  private final String value;

  public DuplicateEntityException(String entityType, String field, String value) {
    super(String.format("%s з %s='%s' вже існує", entityType, field, value));
    this.entityType = entityType;
    this.field = field;
    this.value = value;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getField() {
    return field;
  }

  public String getValue() {
    return value;
  }
}
