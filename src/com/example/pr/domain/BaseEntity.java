package com.example.pr.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseEntity implements Entity {

  private final UUID id;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  protected Map<String, List<String>> errors;

  protected BaseEntity() {
    this.id = UUID.randomUUID();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.errors = new HashMap<>();
  }

  protected void updateTimestamp() {
    this.updatedAt = LocalDateTime.now();
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  protected void addError(String field, String message) {
    this.errors.computeIfAbsent(field, k -> new ArrayList<>())
        .add(message);
  }

  protected void clearError(String field) {
    this.errors.remove(field);
  }

  public Map<String, List<String>> getErrors() {
    return new HashMap<>(errors);
  }

  public boolean isValid() {
    return errors.isEmpty();
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BaseEntity that = (BaseEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}