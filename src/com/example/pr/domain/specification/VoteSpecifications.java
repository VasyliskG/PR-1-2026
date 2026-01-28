package com.example.pr.domain.specification;

import com.example.pr.domain.impl.Vote;
import java.util.UUID;

public final class VoteSpecifications {

  private VoteSpecifications() {}

  public static Specification<Vote> byElectionId(UUID electionId) {
    return vote -> vote.getElectionId() != null
        && vote.getElectionId().equals(electionId);
  }

  public static Specification<Vote> byVoterId(UUID voterId) {
    return vote -> vote.getVoterId() != null
        && vote.getVoterId().equals(voterId);
  }

  public static Specification<Vote> byCandidateId(UUID candidateId) {
    return vote -> vote.getCandidateId() != null
        && vote.getCandidateId().equals(candidateId);
  }

  public static Specification<Vote> byVoterAndElection(UUID voterId, UUID electionId) {
    return byVoterId(voterId).and(byElectionId(electionId));
  }

  public static Specification<Vote> all() {
    return vote -> true;
  }
}