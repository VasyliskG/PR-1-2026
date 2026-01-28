package com.example.pr.domain.dto.vote;

import java.util.UUID;

/**
 * DTO для створення голосу.
 */
public record VoteCreateDto(
    UUID voterId,
    UUID candidateId,
    UUID electionId
) {
  public VoteCreateDto {
    if (voterId == null) {
      throw new IllegalArgumentException("ID виборця є обов'язковим");
    }
    if (candidateId == null) {
      throw new IllegalArgumentException("ID кандидата є обов'язковим");
    }
    if (electionId == null) {
      throw new IllegalArgumentException("ID виборів є обов'язковим");
    }
  }
}
