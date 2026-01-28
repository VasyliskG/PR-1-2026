package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Party;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.PartyRepository;
import java.util.ArrayList;
import java.util.Optional;

class BinaryPartyRepository extends BinaryRepository<Party> implements PartyRepository {

  public BinaryPartyRepository() {
    super(BinaryFilePath.PARTIES.getPath());
  }

  @Override
  public Optional<Party> findByCode(String code) {
    return findAllInternal().stream()
        .filter(party -> code.equals(party.getPartyCode()))
        .findFirst();
  }

  @Override
  public boolean existsByCode(String code) {
    return findAllInternal().stream()
        .anyMatch(party -> code.equals(party.getPartyCode()));
  }

  @Override
  public boolean deleteByCode(String code) {
    invalidateCache();

    var entities = new ArrayList<>(loadFromFile());
    boolean removed = entities.removeIf(party -> code.equals(party.getPartyCode()));

    if (removed) {
      writeToFile(entities);
    }
    return removed;
  }
}
