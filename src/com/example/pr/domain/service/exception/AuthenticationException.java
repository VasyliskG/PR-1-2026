package com.example.pr.domain.service.exception;

/**
 * Виняток аутентифікації.
 */
public class AuthenticationException extends ServiceException {

  public AuthenticationException(String message) {
    super(message);
  }

  public static AuthenticationException invalidCredentials() {
    return new AuthenticationException("Невірний email або пароль");
  }

  public static AuthenticationException accountLocked() {
    return new AuthenticationException("Акаунт заблоковано");
  }

  public static AuthenticationException notAuthenticated() {
    return new AuthenticationException("Користувач не автентифікований");
  }
}
