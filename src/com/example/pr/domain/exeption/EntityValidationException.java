package com.example.pr.domain.exeption;

import java.util.List;
import java.util.Map;

public class EntityValidationException extends RuntimeException {

  private final Map<String, List<String>> errors;

  public EntityValidationException(Map<String, List<String>> errors) {
    super("Entity validation failed. Check 'errors' for details.");
    this.errors = errors;
  }

  public Map<String, List<String>> getErrors() {
    return errors;
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder(super.getMessage());
    sb.append("\n");
    errors.forEach((field, messages) ->
      sb.append(field).append(": ").append(messages).append("\n")
    );
    return sb.toString();
  }
}