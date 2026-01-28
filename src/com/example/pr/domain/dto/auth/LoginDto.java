package com.example.pr.domain.dto.auth;

/**
 * DTO для входу в систему.
 */
public record LoginDto(
    String email,
    String password
) {
  public LoginDto {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email є обов'язковим");
    }
    if (password == null || password.isEmpty()) {
      throw new IllegalArgumentException("Пароль є обов'язковим");
    }

    email = email.trim().toLowerCase();
  }
}
