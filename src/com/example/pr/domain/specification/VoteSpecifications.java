package com.example.pr.domain.specification;

import com.example.pr.domain.impl.Vote;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Фабрика специфікацій для пошуку голосів (Vote).
 * <p>
 * Поля Vote: - voterId (UUID) - candidateId (UUID) - electionId (UUID) - timestamp (LocalDateTime)
 */
public final class VoteSpecifications {

  private VoteSpecifications() {
  }

  /**
   * Голоси конкретного виборця.
   */
  public static Specification<Vote> byVoterId(UUID voterId) {
    return vote -> vote.getVoterId() != null
        && vote.getVoterId().equals(voterId);
  }

  /**
   * Голоси за конкретного кандидата.
   */
  public static Specification<Vote> byCandidateId(UUID candidateId) {
    return vote -> vote.getCandidateId() != null
        && vote.getCandidateId().equals(candidateId);
  }

  /**
   * Голоси на конкретних виборах.
   */
  public static Specification<Vote> byElectionId(UUID electionId) {
    return vote -> vote.getElectionId() != null
        && vote.getElectionId().equals(electionId);
  }

  /**
   * Голос конкретного виборця на конкретних виборах.
   */
  public static Specification<Vote> byVoterAndElection(UUID voterId, UUID electionId) {
    return byVoterId(voterId).and(byElectionId(electionId));
  }

  /**
   * Голоси за кандидата на конкретних виборах.
   */
  public static Specification<Vote> byCandidateAndElection(UUID candidateId, UUID electionId) {
    return byCandidateId(candidateId).and(byElectionId(electionId));
  }

  /**
   * Голоси, зроблені після вказаного часу.
   */
  public static Specification<Vote> votedAfter(LocalDateTime dateTime) {
    return vote -> vote.getTimestamp() != null
        && vote.getTimestamp().isAfter(dateTime);
  }

  /**
   * Голоси, зроблені до вказаного часу.
   */
  public static Specification<Vote> votedBefore(LocalDateTime dateTime) {
    return vote -> vote.getTimestamp() != null
        && vote.getTimestamp().isBefore(dateTime);
  }

  /**
   * Голоси в діапазоні часу.
   */
  public static Specification<Vote> votedBetween(LocalDateTime from, LocalDateTime to) {
    return vote -> vote.getTimestamp() != null
        && vote.getTimestamp().isAfter(from)
        && vote.getTimestamp().isBefore(to);
  }

  /**
   * Голоси за сьогодні.
   */
  public static Specification<Vote> votedToday() {
    LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);
    return votedBetween(startOfDay, endOfDay);
  }

  /**
   * Всі голоси.
   */
  public static Specification<Vote> all() {
    return vote -> true;
  }

  /**
   * Жодного голосу.
   */
  public static Specification<Vote> none() {
    return vote -> false;
  }
}
