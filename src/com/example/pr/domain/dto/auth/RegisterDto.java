package com.example.pr.domain.dto.auth;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO для реєстрації нового користувача.
 */
public record RegisterDto(
    String firstName,
    String lastName,
    String email,
    String password,
    String confirmPassword,
    String passportNumber,
    LocalDate birthDate,
    UUID regionId
) {
  public RegisterDto {
    if (firstName == null || firstName.trim().isEmpty()) {
      throw new IllegalArgumentException("Ім'я є обов'язковим");
    }
    if (lastName == null || lastName.trim().isEmpty()) {
      throw new IllegalArgumentException("Прізвище є обов'язковим");
    }
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email є обов'язковим");
    }
    if (password == null || password.length() < 6) {
      throw new IllegalArgumentException("Пароль повинен містити мінімум 6 символів");
    }
    if (!password.equals(confirmPassword)) {
      throw new IllegalArgumentException("Паролі не співпадають");
    }
    if (passportNumber == null || passportNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Номер паспорта є обов'язковим");
    }
    if (birthDate == null) {
      throw new IllegalArgumentException("Дата наро��ження є обов'язковою");
    }
    if (regionId == null) {
      throw new IllegalArgumentException("Регіон є обов'язковим");
    }

    firstName = firstName.trim();
    lastName = lastName.trim();
    email = email.trim().toLowerCase();
    passportNumber = passportNumber.trim().toUpperCase();
  }
}
