package com.example.pr.infrastructure.storage.contract;

import com.example.pr.domain.impl.Voter;
import com.example.pr.infrastructure.storage.Repository;
import java.util.UUID;

public interface VoterRepository extends Repository<Voter> {

  void deleteByRegionId(UUID regionId);
}
