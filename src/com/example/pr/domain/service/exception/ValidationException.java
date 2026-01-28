package com.example.pr.domain.service.exception;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Виняток валідації даних.
 */
public class ValidationException extends ServiceException {

  private final Map<String, List<String>> errors;

  public ValidationException(String message) {
    super(message);
    this.errors = Collections.emptyMap();
  }

  public ValidationException(Map<String, List<String>> errors) {
    super("Помилка валідації даних");
    this.errors = errors;
  }

  public ValidationException(String field, String message) {
    super(message);
    this.errors = Map.of(field, List.of(message));
  }

  public Map<String, List<String>> getErrors() {
    return errors;
  }
}
