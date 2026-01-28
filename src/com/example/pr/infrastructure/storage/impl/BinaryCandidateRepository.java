package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Candidate;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.CandidateRepository;
import java.util.List;
import java.util.UUID;

class BinaryCandidateRepository extends BinaryRepository<Candidate> implements CandidateRepository {

  public BinaryCandidateRepository() {
    super(BinaryFilePath.CANDIDATES.getPath());
  }

  @Override
  public void deleteByElectionId(UUID electionId) {
    List<Candidate> entities = findAllInternal();
    List<Candidate> toKeep = entities.stream()
        .filter(candidate -> !candidate.getElectionId().equals(electionId))
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
        .filter(candidate -> !candidate.getPartyId().equals(partyId))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }
}
