package com.example.pr.domain.dto.voter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO для створення нового виборця. Immutable record - гарантує незмінність даних.
 */
public record VoterCreateDto(
    String firstName,
    String lastName,
    String email,
    String password,
    String passportNumber,
    LocalDate birthDate,
    UUID regionId
) {

  /**
   * Компактний конструктор з валідацією.
   */
  public VoterCreateDto {
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
    if (passportNumber == null || passportNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Номер паспорта є обов'язковим");
    }
    if (birthDate == null) {
      throw new IllegalArgumentException("Дата народження є обов'язковою");
    }
    if (regionId == null) {
      throw new IllegalArgumentException("Регіон є обов'язковим");
    }

    // Нормалізація
    firstName = firstName.trim();
    lastName = lastName.trim();
    email = email.trim().toLowerCase();
    passportNumber = passportNumber.trim().toUpperCase();
  }
}
