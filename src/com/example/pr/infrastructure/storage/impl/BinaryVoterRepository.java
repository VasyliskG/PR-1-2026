package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Voter;
import com.example.pr.domain.specification.VoterSpecifications;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.VoterRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class BinaryVoterRepository extends BinaryRepository<Voter> implements VoterRepository {

  public BinaryVoterRepository() {
    super(BinaryFilePath.VOTERS.getPath());
  }

  @Override
  public Optional<Voter> findByEmail(String email) {
    return findOne(VoterSpecifications.byEmail(email));
  }

  @Override
  public Optional<Voter> findByPassportNumber(String passportNumber) {
    return findOne(VoterSpecifications.byPassportNumber(passportNumber));
  }

  @Override
  public boolean existsByEmail(String email) {
    return exists(VoterSpecifications.byEmail(email));
  }

  @Override
  public boolean existsByPassportNumber(String passportNumber) {
    return exists(VoterSpecifications.byPassportNumber(passportNumber));
  }

  @Override
  public void deleteByRegionId(UUID regionId) {
    List<Voter> entities = findAllInternal();
    List<Voter> toKeep = entities.stream()
        .filter(voter -> !regionId.equals(voter.getRegionId()))
        .toList();

    if (toKeep.size() < entities.size()) {
      invalidateCache();
      writeToFile(toKeep);
    }
  }

  @Override
  public long countByRegionId(UUID regionId) {
    return count(VoterSpecifications.byRegionId(regionId));
  }
}
