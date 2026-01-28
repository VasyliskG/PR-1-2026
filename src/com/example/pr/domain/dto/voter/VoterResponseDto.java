package com.example.pr.domain.dto.voter;

import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.impl.Voter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для відповіді з даними виборця.
 * Не містить чутливих даних (пароль).
 */
public record VoterResponseDto(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String passportNumber,
    LocalDate birthDate,
    VoterRole role,
    UUID regionId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  /**
   * Створює DTO з сутності Voter.
   */
  public static VoterResponseDto fromEntity(Voter voter) {
    return new VoterResponseDto(
        voter.getId(),
        voter.getFirstName(),
        voter.getLastName(),
        voter.getEmail(),
        voter.getPassportNumber(),
        voter.getBirthDate(),
        voter.getRole(),
        voter.getRegionId(),
        voter.getCreatedAt(),
        voter.getUpdatedAt()
    );
  }

  /**
   * Повне ім'я виборця.
   */
  public String fullName() {
    return firstName + " " + lastName;
  }
}
