package com.example.datacapture.domain;

import java.util.UUID;

import com.example.common.domain.EntityId;

//@Repository
//@Service
public class PackageRepository {

  public PackageId nextIdentity() {
    return new PackageId(UUID.randomUUID().toString().toUpperCase());
  }

  public Package find(EntityId id) {
    throw new UnsupportedOperationException("not yet implemented......");
  }

  public void save(Package overgood) {
    throw new UnsupportedOperationException("not yet implemented......");
  }
}
