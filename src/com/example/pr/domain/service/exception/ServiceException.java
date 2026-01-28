package com.example.pr.domain.service.exception;

/**
 * Базовий виняток для сервісного шару.
 */
public class ServiceException extends RuntimeException {

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
