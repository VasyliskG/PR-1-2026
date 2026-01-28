package com.example.pr.domain.specification;

import com.example.pr.domain.impl.Candidate;
import java.util.UUID;

public final class CandidateSpecifications {

  private CandidateSpecifications() {}

  public static Specification<Candidate> byElectionId(UUID electionId) {
    return candidate -> candidate.getElectionId() != null
        && candidate.getElectionId().equals(electionId);
  }

  public static Specification<Candidate> byPartyId(UUID partyId) {
    return candidate -> candidate.getPartyId() != null
        && candidate.getPartyId().equals(partyId);
  }

  public static Specification<Candidate> fullNameContains(String text) {
    return candidate -> candidate.getFullName().toLowerCase().contains(text.toLowerCase());
  }

  public static Specification<Candidate> all() {
    return candidate -> true;
  }
}