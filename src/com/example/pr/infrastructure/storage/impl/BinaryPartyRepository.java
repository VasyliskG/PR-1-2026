package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Party;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.PartyRepository;

class BinaryPartyRepository extends BinaryRepository<Party> implements PartyRepository {

  public BinaryPartyRepository() {
    super(BinaryFilePath.PARTIES.getPath());
  }
}
