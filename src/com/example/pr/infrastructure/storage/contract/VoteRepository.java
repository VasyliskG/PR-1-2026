package com.example.pr.infrastructure.storage.contract;

import com.example.pr.domain.impl.Vote;
import com.example.pr.infrastructure.storage.Repository;
import java.util.UUID;

public interface VoteRepository extends Repository<Vote> {

  void deleteByElectionId(UUID electionId);

  void deleteByVoterId(UUID voterId);

  void deleteByCandidateId(UUID candidateId);

  boolean hasVoted(UUID voterId, UUID electionId);

  long countByCandidate(UUID candidateId);

  long countByElection(UUID electionId);
}
