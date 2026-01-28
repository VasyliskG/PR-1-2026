package com.example.pr.domain.dto.candidate;

import java.util.UUID;

/**
 * DTO для створення кандидата.
 */
public record CandidateCreateDto(
    String firstName,
    String lastName,
    String passportNumber,
    String partyCode,          // може бути null для незалежних
    UUID electionId,
    String program,
    String photoPath,
    String biography
) {
  public CandidateCreateDto {
    if (firstName == null || firstName.trim().isEmpty()) {
      throw new IllegalArgumentException("Ім'я кандидата є обов'язковим");
    }
    if (lastName == null || lastName.trim().isEmpty()) {
      throw new IllegalArgumentException("Прізвище кандидата є обов'язковим");
    }
    if (passportNumber == null || passportNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Номер паспорта є обов'язковим");
    }
    if (electionId == null) {
      throw new IllegalArgumentException("ID виборів є обов'язковим");
    }

    firstName = firstName.trim();
    lastName = lastName.trim();
    passportNumber = passportNumber.trim().toUpperCase();
  }
}
