package com.example.pr.domain.dto.auth;

/**
 * DTO для зміни пароля.
 */
public record ChangePasswordDto(
    String currentPassword,
    String newPassword,
    String confirmNewPassword
) {
  public ChangePasswordDto {
    if (currentPassword == null || currentPassword.trim().isEmpty()) {
      throw new IllegalArgumentException("Поточний пароль є обов'язковим");
    }
    if (newPassword == null || newPassword.length() < 6) {
      throw new IllegalArgumentException("Новий пароль повинен містити мінімум 6 символів");
    }
    if (!newPassword.equals(confirmNewPassword)) {
      throw new IllegalArgumentException("Паролі не співпадають");
    }
    if (currentPassword.equals(newPassword)) {
      throw new IllegalArgumentException("Новий пароль повинен відрізнятись від поточного");
    }
  }
}
