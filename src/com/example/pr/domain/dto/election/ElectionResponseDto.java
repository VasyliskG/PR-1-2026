package com.example.pr.domain.dto.election;

import com.example.pr.domain.enums.ElectionStatus;
import com.example.pr.domain.impl.Election;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для відповіді з даними виборів.
 */
public record ElectionResponseDto(
    UUID id,
    String name,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    ElectionStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static ElectionResponseDto fromEntity(Election election) {
    return new ElectionResponseDto(
        election.getId(),
        election.getName(),
        election.getDescription(),
        election.getStartDate(),
        election.getEndDate(),
        election.getStatus(),
        election.getCreatedAt(),
        election.getUpdatedAt()
    );
  }

  /**
   * Перевіряє, чи вибори активні зараз.
   */
  public boolean isActiveNow() {
    LocalDateTime now = LocalDateTime.now();
    return status == ElectionStatus.ACTIVE
        && now.isAfter(startDate)
        && now.isBefore(endDate);
  }
}
