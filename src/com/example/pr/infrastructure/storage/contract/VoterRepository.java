package com.example.pr.infrastructure.storage.contract;

import com.example.pr.domain.impl.Voter;
import com.example.pr.infrastructure.storage.Repository;
import java.util.Optional;
import java.util.UUID;

public interface VoterRepository extends Repository<Voter> {

  /**
   * Знаходить виборця за email.
   */
  Optional<Voter> findByEmail(String email);

  /**
   * Знаходить виборця за номером паспорта.
   */
  Optional<Voter> findByPassportNumber(String passportNumber);

  /**
   * Перевіряє, чи існує виборець з таким email.
   */
  boolean existsByEmail(String email);

  /**
   * Перевіряє, чи існує виборець з таким паспортом.
   */
  boolean existsByPassportNumber(String passportNumber);

  /**
   * Видаляє всіх виборців регіону.
   */
  void deleteByRegionId(UUID regionId);

  /**
   * Підраховує виборців у регіоні.
   */
  long countByRegionId(UUID regionId);
}
