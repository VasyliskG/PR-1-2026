package com.example.pr.domain.dto.candidate;

import java.util.Optional;
import java.util.UUID;

/**
 * DTO для оновлення кандидата.
 */
public record CandidateUpdateDto(
    Optional<String> firstName,
    Optional<String> lastName,
    Optional<String> partyCode,
    Optional<String> program,
    Optional<String> photoPath,
    Optional<String> biography
) {
  public static CandidateUpdateDto empty() {
    return new CandidateUpdateDto(
        Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty()
    );
  }
}
