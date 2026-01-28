package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.impl.Region;
import com.example.pr.infrastructure.storage.BinaryFilePath;
import com.example.pr.infrastructure.storage.BinaryRepository;
import com.example.pr.infrastructure.storage.contract.RegionRepository;

class BinaryRegionRepository extends BinaryRepository<Region> implements RegionRepository {

  public BinaryRegionRepository() {
    super(BinaryFilePath.REGIONS.getPath());
  }
}
