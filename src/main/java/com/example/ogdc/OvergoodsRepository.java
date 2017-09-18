package com.example.ogdc;

import java.util.UUID;

import com.example.infrastructure.DomainEntityId;

//@Repository
//@Service
public class OvergoodsRepository {

  public OvergoodsId nextIdentity() {
    return new OvergoodsId(UUID.randomUUID().toString().toUpperCase());
  }

  public Overgoods find(DomainEntityId id) {
    throw new UnsupportedOperationException("not yet implemented......");
  }

  public void save(Overgoods overgood) {
    throw new UnsupportedOperationException("not yet implemented......");
  }
}
