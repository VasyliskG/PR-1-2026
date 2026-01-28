package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Election;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.ElectionRepository;

class BinaryElectionRepository extends BinaryRepository<Election> implements ElectionRepository {

  public BinaryElectionRepository() {
    super(BinaryFilePath.ELECTIONS.getPath());
  }
}
