package com.example.pr.infrastructure.storage.contract;

import com.example.pr.domain.impl.Candidate;
import com.example.pr.infrastructure.storage.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository extends Repository<Candidate> {

  /**
   * Знаходить кандидата за номером паспорта.
   */
  Optional<Candidate> findByPassportNumber(String passportNumber);

  /**
   * Знаходить всіх кандидатів на виборах.
   */
  List<Candidate> findByElectionId(UUID electionId);

  /**
   * Знаходить всіх кандидатів партії.
   */
  List<Candidate> findByPartyId(UUID partyId);

  /**
   * Видаляє всіх кандидатів з виборів.
   */
  void deleteByElectionId(UUID electionId);

  /**
   * Видаляє всіх кандидатів партії.
   */
  void deleteByPartyId(UUID partyId);

  /**
   * Підраховує кандидатів на виборах.
   */
  long countByElectionId(UUID electionId);

  /**
   * Підраховує кандидатів партії.
   */
  long countByPartyId(UUID partyId);
}
