package com.example.pr.domain.service.exception;

import java.util.UUID;

/**
 * Виняток, коли сутність не знайдено.
 */
public class EntityNotFoundException extends ServiceException {

  private final String entityType;
  private final String identifier;

  public EntityNotFoundException(String entityType, UUID id) {
    super(String.format("%s з ID '%s' не знайдено", entityType, id));
    this.entityType = entityType;
    this.identifier = id.toString();
  }

  public EntityNotFoundException(String entityType, String code) {
    super(String.format("%s з ID '%s' не знайдено", entityType, code));
    this.entityType = entityType;
    this.identifier = code;
  }

  public EntityNotFoundException(String entityType, String field, String value) {
    super(String.format("%s з %s='%s' не знайдено", entityType, field, value));
    this.entityType = entityType;
    this.identifier = value;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getIdentifier() {
    return identifier;
  }
}
