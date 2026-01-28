package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Vote;
import com.example.pr.domain.specification.VoteSpecifications;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.VoteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class BinaryVoteRepository extends BinaryRepository<Vote> implements VoteRepository {

  public BinaryVoteRepository() {
    super(BinaryFilePath.VOTES.getPath());
  }

  @Override
  public boolean hasVoted(UUID voterId, UUID electionId) {
    return exists(VoteSpecifications.byVoterAndElection(voterId, electionId));
  }

  @Override
  public Optional<Vote> findByVoterAndElection(UUID voterId, UUID electionId) {
    return findOne(VoteSpecifications.byVoterAndElection(voterId, electionId));
  }

  @Override
  public List<Vote> findByElectionId(UUID electionId) {
    return findAll(VoteSpecifications.byElectionId(electionId));
  }

  @Override
  public List<Vote> findByCandidateId(UUID candidateId) {
    return findAll(VoteSpecifications.byCandidateId(candidateId));
  }

  @Override
  public List<Vote> findByVoterId(UUID voterId) {
    return findAll(VoteSpecifications.byVoterId(voterId));
  }

  @Override
  public long countByCandidate(UUID candidateId) {
    return count(VoteSpecifications.byCandidateId(candidateId));
  }

  @Override
  public long countByElection(UUID electionId) {
    return count(VoteSpecifications.byElectionId(electionId));
  }

  @Override
  public long countByCandidateAndElection(UUID candidateId, UUID electionId) {
    return count(VoteSpecifications.byCandidateAndElection(candidateId, electionId));
  }

  @Override
  public void deleteByElectionId(UUID electionId) {
    List<Vote> entities = findAllInternal();
    List<Vote> toKeep = entities.stream()
        .filter(v -> !electionId.equals(v.getElectionId()))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }

  @Override
  public void deleteByVoterId(UUID voterId) {
    List<Vote> entities = findAllInternal();
    List<Vote> toKeep = entities.stream()
        .filter(v -> !voterId.equals(v.getVoterId()))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }

  @Override
  public void deleteByCandidateId(UUID candidateId) {
    List<Vote> entities = findAllInternal();
    List<Vote> toKeep = entities.stream()
        .filter(v -> !candidateId.equals(v.getCandidateId()))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }
}
