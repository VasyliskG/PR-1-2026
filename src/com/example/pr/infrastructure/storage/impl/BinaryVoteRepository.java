package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Vote;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.VoteRepository;
import java.util.List;
import java.util.UUID;

class BinaryVoteRepository extends BinaryRepository<Vote> implements VoteRepository {

  public BinaryVoteRepository() {
    super(BinaryFilePath.VOTES.getPath());
  }

  @Override
  public void deleteByElectionId(UUID electionId) {
    List<Vote> entities = findAllInternal();
    List<Vote> toKeep = entities.stream()
        .filter(vote -> !vote.getElectionId().equals(electionId))
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
        .filter(vote -> !vote.getVoterId().equals(voterId))
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
        .filter(vote -> !vote.getCandidateId().equals(candidateId))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }

  @Override
  public boolean hasVoted(UUID voterId, UUID electionId) {
    return findAllInternal().stream()
        .anyMatch(vote -> vote.getVoterId().equals(voterId)
            && vote.getElectionId().equals(electionId));
  }

  @Override
  public long countByCandidate(UUID candidateId) {
    return findAllInternal().stream()
        .filter(vote -> vote.getCandidateId().equals(candidateId))
        .count();
  }

  @Override
  public long countByElection(UUID electionId) {
    return findAllInternal().stream()
        .filter(vote -> vote.getElectionId().equals(electionId))
        .count();
  }
}
