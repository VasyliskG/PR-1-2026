package com.example.pr.domain.dto.vote;

import java.util.UUID;

/**
 * DTO для результатів голосування.
 */
public record VoteResultDto(
    UUID candidateId,
    String candidateFullName,
    String partyName,
    long voteCount,
    double percentage
) {
}
