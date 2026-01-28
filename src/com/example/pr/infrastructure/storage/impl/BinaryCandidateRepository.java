package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Candidate;
import com.example.pr.domain.service.exception.EntityNotFoundException;
import com.example.pr.domain.specification.CandidateSpecifications;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.CandidateRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class BinaryCandidateRepository extends BinaryRepository<Candidate> implements CandidateRepository {

  public BinaryCandidateRepository() {
    super(BinaryFilePath.CANDIDATES.getPath());
  }

  @Override
  public Optional<Candidate> findByPassportNumber(String passportNumber) {
    return findOne(CandidateSpecifications.byPassportNumber(passportNumber));
  }

  @Override
  public List<Candidate> findByElectionId(UUID electionId) {
    return findAll(CandidateSpecifications.byElectionId(electionId));
  }

  public List<Candidate> findByPartyCode(String partyCode) {
    return findAll(CandidateSpecifications.byPartyCode(partyCode));
  }

  @Override
  public void deleteByElectionId(UUID electionId) {
    List<Candidate> entities = findAllInternal();
    List<Candidate> toKeep = entities.stream()
        .filter(c -> !electionId.equals(c.getElectionId()))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }

  public void deleteByPartyCode(String partyCode) {
    List<Candidate> entities = findAllInternal();
    List<Candidate> toKeep = entities.stream()
        .filter(c -> !partyCode.equals(c.getPartyCode()))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }

  @Override
  public long countByElectionId(UUID electionId) {
    return count(CandidateSpecifications.byElectionId(electionId));
  }

  @Override
  public long countByPartyCode(String partyCode) {
    return count(CandidateSpecifications.byPartyCode(partyCode));
  }

  @Override
  public Optional<Candidate> findByCode(String code) {
    throw new UnsupportedOperationException(
        "Candidate does not support findByCode"
    );
  }

  // Fixed: Return type changed from void to boolean
  @Override
  public boolean deleteByCode(String code) {
    // Candidate doesn't support code-based operations,
    // so this always returns false or throws an exception
    throw new UnsupportedOperationException(
        "Candidate does not support deleteByCode"
    );
  }

  // Added: Missing method from Repository interface
  @Override
  public boolean existsByCode(String code) {
    throw new UnsupportedOperationException(
        "Candidate does not support existsByCode"
    );
  }
}
