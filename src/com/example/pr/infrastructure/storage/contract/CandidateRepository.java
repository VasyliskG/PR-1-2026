package com.example.pr.infrastructure.storage.contract;

import com.example.pr.domain.impl.Candidate;
import com.example.pr.infrastructure.storage.Repository;
import java.util.UUID;

public interface CandidateRepository extends Repository<Candidate> {

  void deleteByElectionId(UUID electionId);

  void deleteByPartyId(UUID partyId);
}
