package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Voter;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.VoterRepository;
import java.util.List;
import java.util.UUID;

class BinaryVoterRepository extends BinaryRepository<Voter> implements VoterRepository {

  public BinaryVoterRepository() {
    super(BinaryFilePath.VOTERS.getPath());
  }

  @Override
  public void deleteByRegionId(UUID regionId) {
    List<Voter> entities = findAllInternal();
    List<Voter> toKeep = entities.stream()
        .filter(voter -> !voter.getRegionId().equals(regionId))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }
}
