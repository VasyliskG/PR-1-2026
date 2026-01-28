package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Candidate;
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

  @Override
  public List<Candidate> findByPartyId(UUID partyId) {
    return findAll(CandidateSpecifications.byPartyId(partyId));
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

  @Override
  public void deleteByPartyId(UUID partyId) {
    List<Candidate> entities = findAllInternal();
    List<Candidate> toKeep = entities.stream()
        .filter(c -> !partyId.equals(c.getPartyId()))
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
  public long countByPartyId(UUID partyId) {
    return count(CandidateSpecifications.byPartyId(partyId));
  }
}
