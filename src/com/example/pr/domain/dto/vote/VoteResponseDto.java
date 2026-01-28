package com.example.pr.domain.dto.vote;

import com.example.pr.domain.impl.Vote;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для відповіді з даними голосу.
 */
public record VoteResponseDto(
    UUID id,
    UUID voterId,
    UUID candidateId,
    UUID electionId,
    LocalDateTime timestamp
) {
  public static VoteResponseDto fromEntity(Vote vote) {
    return new VoteResponseDto(
        vote.getId(),
        vote.getVoterId(),
        vote.getCandidateId(),
        vote.getElectionId(),
        vote.getTimestamp()
    );
  }
}
