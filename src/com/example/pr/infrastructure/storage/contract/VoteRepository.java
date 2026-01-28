package com.example.pr.infrastructure.storage.contract;

import com.example.pr.domain.impl.Vote;
import com.example.pr.infrastructure.storage.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends Repository<Vote> {

  /**
   * Перевіряє, чи виборець вже голосував на виборах.
   */
  boolean hasVoted(UUID voterId, UUID electionId);

  /**
   * Знаходить голос виборця на виборах.
   */
  Optional<Vote> findByVoterAndElection(UUID voterId, UUID electionId);

  /**
   * Знаходить всі голоси на виборах.
   */
  List<Vote> findByElectionId(UUID electionId);

  /**
   * Знаходить всі голоси за кандидата.
   */
  List<Vote> findByCandidateId(UUID candidateId);

  /**
   * Знаходить всі голоси виборця.
   */
  List<Vote> findByVoterId(UUID voterId);

  /**
   * Підраховує голоси за кандидата.
   */
  long countByCandidate(UUID candidateId);

  /**
   * Підраховує голоси на виборах.
   */
  long countByElection(UUID electionId);

  /**
   * Підраховує голоси за кандидата на конкретних виборах.
   */
  long countByCandidateAndElection(UUID candidateId, UUID electionId);

  /**
   * Видаляє всі голоси на виборах.
   */
  void deleteByElectionId(UUID electionId);

  /**
   * Видаляє всі голоси виборця.
   */
  void deleteByVoterId(UUID voterId);

  /**
   * Видаляє всі голоси за кандидата.
   */
  void deleteByCandidateId(UUID candidateId);
}
