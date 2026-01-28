package com.example.pr.domain.dto.voter;

import java.util.Optional;
import java.util.UUID;

/**
 * DTO для оновлення виборця.
 * Використовує Optional для часткового оновлення.
 */
public record VoterUpdateDto(
    Optional<String> firstName,
    Optional<String> lastName,
    Optional<String> email,
    Optional<UUID> regionId
) {
  public VoterUpdateDto {
    // Валідація, якщо значення присутнє
    firstName.ifPresent(v -> {
      if (v.trim().isEmpty()) {
        throw new IllegalArgumentException("Ім'я не може бути порожнім");
      }
    });
    lastName.ifPresent(v -> {
      if (v.trim().isEmpty()) {
        throw new IllegalArgumentException("Прізвище не може бути порожнім");
      }
    });
    email.ifPresent(v -> {
      if (v.trim().isEmpty()) {
        throw new IllegalArgumentException("Email не може бути порожнім");
      }
    });
  }

  /**
   * Створює DTO з усіма порожніми Optional.
   */
  public static VoterUpdateDto empty() {
    return new VoterUpdateDto(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );
  }

  /**
   * Builder-подібні методи для зручності.
   */
  public VoterUpdateDto withFirstName(String firstName) {
    return new VoterUpdateDto(Optional.ofNullable(firstName), lastName, email, regionId);
  }

  public VoterUpdateDto withLastName(String lastName) {
    return new VoterUpdateDto(firstName, Optional.ofNullable(lastName), email, regionId);
  }

  public VoterUpdateDto withEmail(String email) {
    return new VoterUpdateDto(firstName, lastName, Optional.ofNullable(email), regionId);
  }

  public VoterUpdateDto withRegionId(UUID regionId) {
    return new VoterUpdateDto(firstName, lastName, email, Optional.ofNullable(regionId));
  }
}
