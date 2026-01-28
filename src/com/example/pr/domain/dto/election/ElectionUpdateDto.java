package com.example.pr.domain.dto.election;

import com.example.pr.domain.enums.ElectionStatus;
import java.util.Optional;

/**
 * DTO для оновлення виборів.
 */
public record ElectionUpdateDto(
    Optional<String> name,
    Optional<String> description,
    Optional<ElectionStatus> status
) {
  public static ElectionUpdateDto empty() {
    return new ElectionUpdateDto(Optional.empty(), Optional.empty(), Optional.empty());
  }

  public ElectionUpdateDto withStatus(ElectionStatus status) {
    return new ElectionUpdateDto(name, description, Optional.ofNullable(status));
  }
}
